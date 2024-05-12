package com.gatdsen.manager.run;

import com.gatdsen.manager.CompletionHandler;
import com.gatdsen.manager.game.Executable;
import com.gatdsen.manager.Manager;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.GameMode;

import java.util.ArrayList;
import java.util.List;

public abstract class Run {


    private boolean completed = false;

    private final Object schedulingLock = new Object();
    private final List<CompletionHandler<Run>> completionListeners = new ArrayList<>();

    protected final Manager manager;

    protected GameMode gameMode;
    private final List<Executable> games = new ArrayList<>();

    private boolean disposed = false;
    private final List<PlayerHandlerFactory> playerFactories;

    protected final RunResults results;

    public Run(Manager manager, RunConfig runConfig) {
        this.playerFactories = new ArrayList<>(runConfig.playerFactories);
        gameMode = runConfig.gameMode;
        this.manager = manager;
        results = new RunResults(runConfig);
    }

    public static Run getRun(Manager manager, RunConfig runConfig) {
        String gameModeName = runConfig.gameMode.getClass().getSimpleName();
        // if the gameModeName contains a number in format x_y remove the x_y
        if (gameModeName.contains("_")) {
            gameModeName = gameModeName.substring(0, gameModeName.indexOf("_"));
        }

        switch (gameModeName) {
            case "CampaignMode":
            case "ReplayMode":
            case "NormalMode":
            case "ChristmasMode":
                return new SingleGameRun(manager, runConfig);
            case "ExamAdmissionMode":
            case "TournamentPhase1Mode":
                return new ParallelMultiGameRun(manager, runConfig);
            case "TournamentModePhase2":
                return new TournamentRun(manager, runConfig);
            default:
                throw new IllegalArgumentException(
                        runConfig.gameMode.getClass().getName() + " is not processed by the Run interface"
                );
        }
    }

    public List<Executable> getGames() {
        return games;
    }

    protected void addGame(Executable game) {
        synchronized (schedulingLock) {
            if (!disposed) {
                games.add(game);
                manager.schedule(game);
            }
        }
    }

    public void dispose() {
        synchronized (schedulingLock) {
            disposed = true;
            for (Executable game : games) {
                manager.stop(game);
            }
        }
    }

    public String getGameModeName() {
        String gameModeName = gameMode.getClass().getSimpleName();
        if (gameModeName.contains("_")) {
            gameModeName = gameModeName.substring(0, gameModeName.indexOf("_"));
        }
        return gameModeName;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<PlayerHandlerFactory> getPlayerFactories() {
        return playerFactories;
    }

    protected void complete() {
        synchronized (schedulingLock) {
            completed = true;
            for (CompletionHandler<Run> completionListener : completionListeners) {
                completionListener.onComplete(this);
            }
        }
    }

    public RunResults getResults() {
        return results;
    }

    public void addCompletionListener(CompletionHandler<Run> handler) {
        synchronized (schedulingLock) {
            completionListeners.add(handler);
            if (completed) new Thread(() -> handler.onComplete(this)).start();
        }
    }

    @Override
    public String toString() {
        return "Run{" +
                "completed=" + completed +
                ", completionListeners=" + completionListeners +
                ", gameMode=" + gameMode +
                ", games=" + games +
                ", players=" + playerFactories +
                ", results=" + results +
                '}';
    }
}
