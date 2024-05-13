package com.gatdsen.manager.run;

import com.gatdsen.manager.CompletionHandler;
import com.gatdsen.manager.game.Executable;
import com.gatdsen.manager.Manager;
import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.gamemode.PlayableGameMode;

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

    protected final RunResults results;

    public Run(Manager manager, RunConfig runConfig) {
        gameMode = runConfig.gameMode;
        this.manager = manager;
        results = new RunResults(runConfig);
    }

    public static Run getRun(Manager manager, RunConfig runConfig) {
        GameMode gameMode = runConfig.gameMode;
        if (gameMode.getType() == GameMode.Type.TOURNAMENT_PHASE_2) {
            return new TournamentRun(manager, runConfig);
        }
        if (gameMode.getType() == GameMode.Type.REPLAY) {
            return new SingleGameRun(manager, runConfig);
        }
        List<String> maps = ((PlayableGameMode) gameMode).getMaps();
        if (maps.size() > 1) {
            return new ParallelMultiGameRun(manager, runConfig);
        } else {
            return new SingleGameRun(manager, runConfig);
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
                ", results=" + results +
                '}';
    }
}
