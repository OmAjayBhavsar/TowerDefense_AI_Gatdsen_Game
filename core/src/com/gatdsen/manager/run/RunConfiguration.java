package com.gatdsen.manager.run;

import com.gatdsen.manager.AnimationLogProcessor;
import com.gatdsen.manager.game.GameConfig;
import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.handler.LocalPlayerHandlerFactory;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.GameState.GameMode;

import java.util.ArrayList;
import java.util.List;

public final class RunConfiguration {

    public RunConfiguration() {
    }

    /**
     * Privater Konstruktor, der eine tiefe Kopie einer vorhandenen RunConfiguration erstellt.
     * @param original Die ursprüngliche RunConfiguration, von der eine Kopie erstellt wird.
     */
    private RunConfiguration(RunConfiguration original) {
        gameMode = original.gameMode;
        gui = original.gui;
        animationLogProcessor = original.animationLogProcessor;
        inputProcessor = original.inputProcessor;
        mapName = original.mapName;
        replay = original.replay;
        playerFactories = new ArrayList<>(original.playerFactories);
    }

    public GameMode gameMode = GameMode.Normal;
    public boolean gui = true;
    public AnimationLogProcessor animationLogProcessor = null;
    public InputProcessor inputProcessor = null;
    public String mapName = null;
    public boolean replay = false;
    public List<PlayerHandlerFactory> playerFactories = new ArrayList<>();

    public boolean validate() {
        boolean isValid = true;
        switch (gameMode) {
            case Normal:
                if (mapName == null) {
                    System.err.println("RunConfiguration: No map name was provided.");
                    isValid = false;
                }
                if (playerFactories.size() != 2) {
                    System.err.println("RunConfiguration: Only two players are allowed in normal game mode.");
                    isValid = false;
                }
                break;
            case Christmas_Task:
                if (mapName != null) {
                    System.err.println("RunConfiguration: A map can't be provided for the christmas task.");
                    isValid = false;
                }
                if (playerFactories.size() != 1) {
                    System.err.println("RunConfiguration: Only one player is allowed for the christmas task.");
                    isValid = false;
                }
                break;
            case Replay:
                if (mapName == null) {
                    System.err.println("RunConfiguration: A replay file name has to be provided for the replay mode.");
                    isValid = false;
                }
                if (replay) {
                    System.err.println("RunConfiguration: A replay of the replay mode can't be created. Why would you do that anyway??");
                    isValid = false;
                }
                if (!playerFactories.isEmpty()) {
                    System.err.println("RunConfiguration: Players can't be provided for the replay mode.");
                    isValid = false;
                }
                break;
            default:
                throw new RuntimeException("RunConfiguration: Gamemode " + gameMode + " is not unlocked yet.");
        }
        return isValid;
    }

    public GameConfig asGameConfig() {
        RunConfiguration config = copy();
        switch (gameMode) {
            case Normal:
                config.playerFactories = playerFactories;
                break;
            case Christmas_Task:
                config.mapName = "map2";
                config.playerFactories.add(LocalPlayerHandlerFactory.IDLE_BOT);
                break;
            case Replay:
                break;
            default:
                throw new RuntimeException("RunConfiguration: Gamemode " + gameMode + " is not unlocked yet.");
        }
        return new GameConfig(config);
    }

    /**
     * Erstellt und gibt eine Kopie des aktuellen RunConfiguration-Objekts zurück.
     * @return Eine Kopie des aktuellen RunConfiguration-Objekts.
     */
    public RunConfiguration copy(){
        return new RunConfiguration(this);
    }

    public String toString() {
        return "RunConfiguration{" +
                "gameMode=" + gameMode +
                ", gui=" + gui +
                ", animationLogProcessor=" + animationLogProcessor +
                ", inputProcessor=" + inputProcessor +
                ", mapName=\"" + mapName + "\"" +
                ", replay=" + replay +
                ", players=" + playerFactories +
                "}";
    }
}
