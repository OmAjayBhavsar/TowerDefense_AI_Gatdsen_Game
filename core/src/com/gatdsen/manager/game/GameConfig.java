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
    public final boolean replay;

    public GameConfig(RunConfig runConfig) {
        gameMode = runConfig.gameMode;
        gui = runConfig.gui;
        animationLogProcessor = runConfig.animationLogProcessor;
        inputProcessor = runConfig.inputProcessor;
        replay = runConfig.replay;
    }
}
