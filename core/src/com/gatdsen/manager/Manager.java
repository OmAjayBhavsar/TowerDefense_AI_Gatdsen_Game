package com.gatdsen.manager;

import com.gatdsen.manager.concurrent.ResourcePool;
import com.gatdsen.manager.game.Executable;
import com.gatdsen.manager.game.GameResults;
import com.gatdsen.manager.run.Run;
import com.gatdsen.manager.run.RunConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Manager {

    private static final String RESULT_DIR_NAME = "results";
    private static final File RESULT_DIR = new File(RESULT_DIR_NAME);
    private static int systemReservedProcessorCount = 2;
    private static final Manager singleton = new Manager();
    private boolean pendingShutdown = false;
    private int writtenFiles = 0;

    private final Thread executionManager;

    private final ArrayList<Executable> games = new ArrayList<>();
    private final ArrayList<Executable> scheduledGames = new ArrayList<>();

    private final ArrayList<Executable> activeGames = new ArrayList<>();

    private final ArrayList<Executable> pausedGames = new ArrayList<>();

    private final ArrayList<Executable> completedGames = new ArrayList<>();

    private final BlockingQueue<GameResults> pendingSaves = new LinkedBlockingQueue<>();

    private final Object schedulingLock = new Object();

    private static int availableCores = 0;

    public static Manager getManager() {
        return singleton;
    }

    public Run startRun(RunConfig runConfig) {
        if (runConfig.validate()) {
            return Run.getRun(this, runConfig);
        }
        return null;
    }

    private void executionManager() {
        Thread.currentThread().setName("Execution_Manager");
        while (!Thread.interrupted()) {
            try {
                synchronized (executionManager) {
                    executionManager.wait(5000);
                }
            } catch (InterruptedException e) {
                System.out.println("ExecutionManager shutting down");
                break;
            }
            int threadLimit = Math.max(Runtime.getRuntime().availableProcessors() - systemReservedProcessorCount, Executable.REQUIRED_THREAD_COUNT);
            if (threadLimit != availableCores) {
                availableCores = threadLimit;
                System.out.printf("Resource load changed to %d cores, adapting...\n", threadLimit);
            }
            synchronized (schedulingLock) {
                int runningThreads = activeGames.size() * Executable.REQUIRED_THREAD_COUNT;
                if (runningThreads > threadLimit) {
                    while (runningThreads > threadLimit) {
                        if (activeGames.size() == 0) {
                            System.err.println("Warning: System-reserved Processor Count exceeds physical limit. Simulation offline!");
                            break;
                        }
                        Executable game = activeGames.remove(activeGames.size() - 1);
                        game.pause();
                        pausedGames.add(game);
                        runningThreads -= 2;
                    }
                } else
                    while (runningThreads + 2 <= threadLimit) {
                        if (pausedGames.size() > 0) {
                            Executable game = pausedGames.remove(pausedGames.size() - 1);
                            game.resume();
                            activeGames.add(game);
                        } else if (scheduledGames.size() > 0) {
                            Executable game = scheduledGames.remove(scheduledGames.size() - 1);
                            try {
                                game.start();
                                activeGames.add(game);
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.err.println("Game crashed on start(); Aborting...\n" + game);
                                game.abort();
                            }
                        } else {
                            break;
                        }
                        runningThreads += 2;
                    }
            }
            while (!pendingSaves.isEmpty()) {
                GameResults results = pendingSaves.poll();
                if (RESULT_DIR.exists() || RESULT_DIR.mkdirs()) {
                    try (FileOutputStream fs = new FileOutputStream(String.format("%s/%s_%d_%d.replay", RESULT_DIR, results.getConfig().gameMode, System.currentTimeMillis(), writtenFiles++))) {
                        new ObjectOutputStream(fs).writeObject(results);
                    } catch (IOException e) {
                        System.err.printf("Unable to save replay at %s/%s_%d_%d.replay %n", RESULT_DIR, results.getConfig().gameMode, System.currentTimeMillis(), writtenFiles);
                    }
                } else System.err.printf("Unable to create results directory at %s %n", RESULT_DIR);
            }
        }
    }

    public void schedule(Executable game) {
        synchronized (schedulingLock) {
            if (pendingShutdown) return;
            if (game.getStatus() == Executable.Status.ABORTED) return;
            game.addCompletionListener(this::notifyExecutionManager);
            games.add(game);
            game.schedule();
            scheduledGames.add(game);
        }
        synchronized (executionManager) {
            executionManager.notify();
        }
    }


    private void notifyExecutionManager(Executable game) {
        synchronized (schedulingLock) {
            if (!activeGames.remove(game) && !pausedGames.remove(game))
                System.err.printf("Warning unsuccessfully attempted to complete Game %s\nInstance: %s", game, this);
            if (game.shouldSaveReplay()) pendingSaves.add(game.getGameResults());
            completedGames.add(game);
            game.dispose();
        }
        synchronized (executionManager) {
            executionManager.notify();
        }
    }

    public void stop(Run run) {
        run.dispose();
    }

    public void stop(Executable game) {
        synchronized (schedulingLock) {
            synchronized (game.schedulingLock) {
                switch (game.getStatus()) {
                    case SCHEDULED:
                        scheduledGames.remove(game);
                        break;
                    case ACTIVE:
                        activeGames.remove(game);
                        break;
                    case PAUSED:
                        pausedGames.remove(game);
                        break;
                    case COMPLETED:
                        return;
                }
                if (game.shouldSaveReplay()) pendingSaves.add(game.getGameResults());
                completedGames.add(game);
                game.abort();
            }
        }
    }

    private Manager() {
        //Runtime.getRuntime().addShutdownHook(new Thread(this::dispose));
        executionManager = new Thread(this::executionManager);
        executionManager.start();
    }

    public void dispose() {
        //Shutdown all running threads
        pendingShutdown = true;
        synchronized (games) {
            for (Executable game : games) {
                game.dispose();
            }
        }
        executionManager.interrupt();
        ResourcePool.getInstance().dispose();
    }

    public static int getSystemReservedProcessorCount() {
        return systemReservedProcessorCount;
    }

    public static void setSystemReservedProcessorCount(int systemReservedProcessorCount) {
        Manager.systemReservedProcessorCount = systemReservedProcessorCount;
        synchronized (getManager().executionManager) {
            getManager().executionManager.notify();
        }
    }

    @Override
    public String toString() {
        synchronized (schedulingLock) {
            return "Manager{" +
                    "pendingShutdown=" + pendingShutdown +
                    ", executionManager=" + executionManager +
                    ", games=" + games +
                    ", scheduledGames=" + scheduledGames +
                    ", activeGames=" + activeGames +
                    ", pausedGames=" + pausedGames +
                    ", completedGames=" + completedGames +
                    '}';
        }
    }
}
