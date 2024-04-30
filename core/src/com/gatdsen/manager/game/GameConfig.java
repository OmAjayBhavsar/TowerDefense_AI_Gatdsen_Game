package com.gatdsen.manager.game;

import com.gatdsen.manager.AnimationLogProcessor;
import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.manager.run.RunConfig;
import com.gatdsen.simulation.GameMode;
import java.io.Serializable;

public final class GameConfig implements Serializable {

    public final GameMode gameMode;
    public final boolean gui;
    public final transient AnimationLogProcessor animationLogProcessor;
    public final transient InputProcessor inputProcessor;
    public final String mapName;
    public final boolean replay;
    public final PlayerHandlerFactory[] playerFactories;
    public final int playerCount;

    public GameConfig(RunConfig runConfig) {
        gameMode = runConfig.gameMode;
        gui = runConfig.gui;
        animationLogProcessor = runConfig.animationLogProcessor;
        inputProcessor = runConfig.inputProcessor;
        mapName = runConfig.mapName;
        replay = runConfig.replay;
        playerFactories = runConfig.playerFactories.toArray(new PlayerHandlerFactory[0]);
        playerCount = playerFactories.length;
    }

    private GameConfig(GameConfig original) {
        gameMode = original.gameMode;
        gui = original.gui;
        animationLogProcessor = original.animationLogProcessor;
        inputProcessor = original.inputProcessor;
        mapName = original.mapName;
        replay = original.replay;
        playerFactories = original.playerFactories;
        playerCount = original.playerCount;
    }

    public GameConfig copy() {
        return new GameConfig(this);
    }
}
