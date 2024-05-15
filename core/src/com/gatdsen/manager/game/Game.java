package com.gatdsen.manager.game;

import com.gatdsen.manager.CompletionHandler;
import com.gatdsen.manager.command.Command;
import com.gatdsen.manager.player.data.PlayerType;
import com.gatdsen.manager.player.handler.PlayerHandler;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Simulation;
import com.gatdsen.simulation.action.ActionLog;
import com.gatdsen.simulation.gamemode.PlayableGameMode;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Game extends Executable {

    private static final long BASE_SEED = 345342624;

    private static final AtomicInteger gameNumber = new AtomicInteger(0);
    private static final boolean isDebug;

    static {
        isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().contains("-agentlib:jdwp");
        if (isDebug) System.err.println("Warning: Debugger engaged; Disabling Bot-Timeout!");
    }

    protected final Object schedulingLock = new Object();

    private GameResults gameResults;
    private Simulation simulation;
    private GameState state;
    private final PlayerHandlerFactory[] playerFactories;
    private final String mapName;
    private PlayerHandler[] playerHandlers;

    private long seed = BASE_SEED;

    private Thread simulationThread;

    public Game(GameConfig config, PlayerHandlerFactory[] playerFactories, String mapName) {
        super(config);
        gameResults = new GameResults(config);
        gameResults.setStatus(getStatus());
        this.playerFactories = playerFactories;
        this.mapName = mapName;
    }

    private void create() {
        int playerCount = playerFactories.length;
        simulation = new Simulation((PlayableGameMode) config.gameMode, mapName, playerCount);
        state = simulation.getState();
        if (saveReplay)
            gameResults.setInitialState(state);

        playerHandlers = new PlayerHandler[playerCount];
        @SuppressWarnings("unchecked")
        Future<PlayerHandler>[] playerHandlerFutures = (Future<PlayerHandler>[]) new Future[playerCount];
        for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
            playerHandlerFutures[playerIndex] = playerFactories[playerIndex].createPlayerHandler(
                    playerIndex,
                    simulation.getController(playerIndex),
                    config.inputProcessor
            );
        }
        @SuppressWarnings("unchecked")
        Future<Long>[] longFutures = (Future<Long>[]) new Future[playerCount];
        int gameNumber = Game.gameNumber.get();
        for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
            PlayerHandler playerHandler;
            try {
                playerHandler = playerHandlerFutures[playerIndex].get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            playerHandlers[playerIndex] = playerHandler;
            longFutures[playerIndex] = playerHandler.create(isDebug, gameNumber);
        }
        for (Future<Long> future : longFutures) {
            try {
                seed += future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                return;
            }
        }

        Future<?>[] futures = new Future[playerCount];
        PlayerState[] playerStates = state.getPlayerStates();
        for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
            // Wenn der PlayerState des Spielers deaktiviert ist, da er bspw. disqualifiziert wurde, wird der Spieler
            // übersprungen und dessen executeTurn() nicht aufgerufen.
            if (playerStates[playerIndex].isDeactivated()) {
                continue;
            }
            futures[playerIndex] = playerHandlers[playerIndex].init(state, seed);
        }
        try {
            for (Future<?> future : futures) {
                if (future != null) {
                    future.get();
                }
            }
        } catch (InterruptedException e) {
            if (pendingShutdown) {
                return;
            }
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            for (Future<?> future : futures) {
                if (future != null && !future.isDone()) {
                    future.cancel(true);
                }
            }
        }
        gameResults.setPlayerInformation(
                Arrays.stream(playerHandlers).map(PlayerHandler::getPlayerInformation).toArray(PlayerInformation[]::new)
        );
        config = null;
    }

    public void start() {
        synchronized (schedulingLock) {
            if (getStatus() == Status.ABORTED) return;
            setStatus(Status.ACTIVE);
            gameNumber.getAndIncrement();
            create();
            //Init the Log Processor
            if (gui) {
                animationLogProcessor.init(
                        state.copy(),
                        Arrays.stream(gameResults.getPlayerInformation()).map(PlayerInformation::getName).toArray(String[]::new),
                        new String[][]{});
            }
            //Run the Game
            simulationThread = new Thread(this::run);
            simulationThread.setName("Game_Simulation_Thread");
            simulationThread.setUncaughtExceptionHandler(this::crashHandler);
            simulationThread.start();
        }
    }

    @Override
    protected void setStatus(Status newStatus) {
        super.setStatus(newStatus);
        gameResults.setStatus(newStatus);
    }

    /**
     * Controls Player Execution
     */
    private void run() {
        Thread.currentThread().setName("Game_Thread_" + gameNumber.get());
        while (!pendingShutdown && state.isActive()) {
            synchronized (schedulingLock) {
                if (getStatus() == Status.PAUSED)
                    try {
                        schedulingLock.wait();

                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
            }

            PlayerState[] playerStates = state.getPlayerStates();
            CompletableFuture<?>[] futures = new CompletableFuture[playerHandlers.length];
            BlockingQueue<Supplier<ActionLog>> commandQueue = new LinkedBlockingQueue<>();
            for (int playerIndex = 0; playerIndex < playerHandlers.length; playerIndex++) {
                // Wenn der PlayerState des Spielers deaktiviert ist, da er bspw. keine Leben mehr hat oder
                // disqualifiziert wurde, wird der Spieler übersprungen und dessen executeTurn() nicht aufgerufen.
                if (playerStates[playerIndex].isDeactivated()) {
                    continue;
                }

                ActionLog firstLog = simulation.clearAndReturnActionLog();
                if (saveReplay)
                    gameResults.addActionLog(firstLog);
                if (gui) {
                    animationLogProcessor.animate(firstLog);
                }

                PlayerHandler playerHandler = playerHandlers[playerIndex];
                futures[playerIndex] = playerHandler.executeTurn(
                        state,
                        (List<Command> commands) -> {
                            for (Command command : commands) {
                                commandQueue.add(() -> command.run(playerHandler));
                            }
                        }
                );
                ActionLog log = simulation.clearAndReturnActionLog();
                if (saveReplay) {
                    gameResults.addActionLog(log);
                }
                if (gui && playerHandler.getPlayerInformation().getType() == PlayerType.HUMAN) {
                    //Contains Action produced by entering new turn
                    animationLogProcessor.animate(log);
                }
            }
            try {
                awaitFutures(futures, commandQueue);
            } catch (InterruptedException e) {
                if (pendingShutdown) {
                    break;
                }
                throw new RuntimeException(e);
            }
            if (inputGenerator != null) {
                inputGenerator.endTurn();
            }
            //Contains actions produced by ending the turn (after last command is executed)
            ActionLog finalLog = simulation.endTurn();
            if (saveReplay) {
                gameResults.addActionLog(finalLog);
            }
            if (gui) {
                animationLogProcessor.animate(finalLog);
                animationLogProcessor.awaitNotification();
            }
        }
        if (inputGenerator != null) {
            inputGenerator.endTurn();
        }
        if (!pendingShutdown) {
            float[] health = state.getHealth();
            float[] wins = new float[health.length];
            for (int i = 0; i < health.length; i++) {
                wins[i] = health[i] > 0 ? 1 : 0;
            }
            gameResults.setScores(wins);
            setStatus(Status.COMPLETED);
            for (CompletionHandler<Executable> completionListener : completionListeners) {
                completionListener.onComplete(this);
            }
        }
    }

    @Override
    public void dispose() {
        //Shutdown all running threads
        super.dispose();
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
        if (state != null) {
            float[] health = state.getHealth();
            float[] wins = new float[health.length];
            for (int i = 0; i < health.length; i++) {
                wins[i] = health[i] > 0 ? 1 : 0;
            }
            gameResults.setScores(wins);
            state = null;
        }
        simulation = null;
        simulationThread = null;
        synchronized (schedulingLock) {
            if (playerHandlers != null) {
                boolean gameCompleted = getStatus() == Status.COMPLETED;
                for (PlayerHandler playerHandler : playerHandlers) {
                    playerHandler.dispose(gameCompleted);
                }
                playerHandlers = null;
            }
        }
    }

    /**
     * Hilfsmethode, die auf das Beenden aller übergebenen {@link Future} Objekte wartet.
     * @param futures Die {@link Future} Objekte, auf die gewartet werden soll
     * @throws InterruptedException Wenn das Warten auf das Beenden der {@link Future} Objekte unterbrochen wird. 
     *                              In diesem Fall werden alle Objekte mit {@link Future#cancel(boolean)} abgebrochen.
     */
    private void awaitFutures(CompletableFuture<?>[] futures, BlockingQueue<Supplier<ActionLog>> queue) throws InterruptedException {
        try {
            awaitFuturesHelper(futures, queue);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            for (CompletableFuture<?> future : futures) {
                if (future != null && !future.isDone()) {
                    future.cancel(true);
                }
            }
        }
    }

    private void awaitFuturesHelper(CompletableFuture<?>[] futures, BlockingQueue<Supplier<ActionLog>> queue) throws InterruptedException, ExecutionException {
        for (CompletableFuture<?> future : futures) {
            if (future == null) {
                continue;
            }
            future.whenComplete((result, error) -> {
                queue.add(() -> null); // Damit der queue.take() Aufruf unterbrochen wird
            });
            while (!future.isDone()) {
                handleActionLogSupplier(queue.take());
            }
        }
        if (!queue.isEmpty()) {
            for (Supplier<ActionLog> supplier : queue) {
                handleActionLogSupplier(supplier);
            }
        }
        for (CompletableFuture<?> future : futures) {
            future.get();
        }
    }

    private void handleActionLogSupplier(Supplier<ActionLog> supplier) {
        ActionLog log = supplier.get();
        if (log == null) {
            return;
        }
        if (saveReplay) {
            gameResults.addActionLog(log);
        }
        if (gui) {
            animationLogProcessor.animate(log);
        }
    }

    public boolean shouldSaveReplay() {
        return super.saveReplay;
    }

    public GameResults getGameResults() {
        return gameResults;
    }

    @Override
    public String toString() {
        return "Game{" +
                "status=" + getStatus() +
                ", completionListeners=" + super.completionListeners +
                ", inputGenerator=" + inputGenerator +
                ", animationLogProcessor=" + animationLogProcessor +
                ", gui=" + gui +
                ", gameResults=" + gameResults +
                ", simulation=" + simulation +
                ", state=" + state +
                ", simulationThread=" + simulationThread +
                ", pendingShutdown=" + pendingShutdown +
                ", config=" + config +
                '}';
    }
}
