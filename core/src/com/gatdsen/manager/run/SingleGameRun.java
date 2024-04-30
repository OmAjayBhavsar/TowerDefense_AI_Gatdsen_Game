package com.gatdsen.manager.run;

import com.gatdsen.manager.*;
import com.gatdsen.manager.game.*;
import com.gatdsen.simulation.GameState;

public class SingleGameRun extends Run {

    public SingleGameRun(Manager manager, RunConfig runConfig) {
        super(manager, runConfig);
        GameConfig gameConfig = runConfig.asGameConfig();
        Executable game = runConfig.gameMode.getClass().getSimpleName().equals("ReplayMode") ? new ReplayGame(gameConfig) : new Game(gameConfig);
        game.addCompletionListener(this::onGameCompletion);
        addGame(game);
    }

    public void onGameCompletion(Executable exec) {
        if (isCompleted()) throw new RuntimeException("In a single game run only one game may complete");
        GameResults gameResults = exec.getGameResults();
        results.setPlayerInformation(gameResults.getPlayerInformation());
        results.setScores(gameResults.getScores());
        complete();
    }

    @Override
    public String toString() {
        return "SingleGameRun{" +
                "super=" + super.toString() +
                '}';
    }
}
