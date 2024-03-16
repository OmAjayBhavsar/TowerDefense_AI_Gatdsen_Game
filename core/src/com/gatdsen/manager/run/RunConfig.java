package com.gatdsen.manager.run;

import com.gatdsen.manager.AnimationLogProcessor;
import com.gatdsen.manager.game.GameConfig;
import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.handler.LocalPlayerHandlerFactory;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.GameState.GameMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse repräsentiert die Konfiguration eines {@link Run}s.
 */
public final class RunConfig {

    public RunConfig() {
    }

    /**
     * Privater Konstruktor, der eine tiefe Kopie einer vorhandenen RunConfig erstellt.
     * @param original Die ursprüngliche RunConfig, von der eine Kopie erstellt wird.
     */
    private RunConfig(RunConfig original) {
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

    /**
     * Überprüft, ob die RunConfig gültig ist. Wenn nicht, werden Fehlermeldungen auf der Standardfehlerausgabe
     * ausgegeben und false zurückgegeben.
     * @return true, wenn die RunConfig gültig ist, ansonsten false
     */
    public boolean validate() {
        StringBuilder errorMessages = new StringBuilder();
        boolean isValid = validate(errorMessages);
        System.err.print(errorMessages);
        return isValid;
    }

    /**
     * Überprüft, ob die RunConfig gültig ist. <br>
     * Anders als {@link #validate()} werden keine Fehlermeldungen ausgegeben.
     * @return true, wenn die RunConfig gültig ist, ansonsten false
     */
    public boolean validateSilent() {
        return validate(null);
    }

    /**
     * Überprüft, ob die RunConfig gültig ist. Wenn nicht, werden die Fehlermeldungen an den übergebenen StringBuilder
     * angehängt und false zurückgegeben.
     * @param errorMessages Der StringBuilder, an den die Fehlermeldungen angehängt werden sollen
     * @return true, wenn die RunConfig gültig ist, ansonsten false
     */
    private boolean validate(StringBuilder errorMessages) {
        boolean isValid = true;
        switch (gameMode) {
            case Normal:
                if (mapName == null) {
                    appendStringToStringBuilder(errorMessages, "RunConfig: No map name was provided.\n");
                    isValid = false;
                }
                if (playerFactories.size() != 2) {
                    appendStringToStringBuilder(errorMessages, "RunConfig: Only two players are allowed in normal game mode.\n");
                    isValid = false;
                }
                break;
            case Christmas_Task:
                if (mapName != null) {
                    appendStringToStringBuilder(errorMessages, "RunConfig: A map can't be provided for the christmas task.\n");
                    isValid = false;
                }
                if (playerFactories.size() != 1) {
                    appendStringToStringBuilder(errorMessages, "RunConfig: Only one player is allowed for the christmas task.\n");
                    isValid = false;
                }
                break;
            case Replay:
                if (mapName == null) {
                    appendStringToStringBuilder(errorMessages, "RunConfig: A replay file name has to be provided for the replay mode.\n");
                    isValid = false;
                }
                if (replay) {
                    appendStringToStringBuilder(errorMessages, "RunConfig: A replay of the replay mode can't be created. Why would you do that anyway??\n");
                    isValid = false;
                }
                if (!playerFactories.isEmpty()) {
                    appendStringToStringBuilder(errorMessages, "RunConfig: Players can't be provided for the replay mode.\n");
                    isValid = false;
                }
                break;
            default:
                throw new RuntimeException("RunConfig: Gamemode " + gameMode + " is not unlocked yet.\n");
        }
        return isValid;
    }

    /**
     * Hilfsmethode, um einen String an einen StringBuilder anzuhängen, falls der StringBuilder nicht null ist.
     * @param builder Der StringBuilder, an den der String angehängt werden soll
     * @param string Der String, der an den StringBuilder angehängt werden soll
     */
    private static void appendStringToStringBuilder(StringBuilder builder, String string) {
        if (builder != null) {
            builder.append(string);
        }
    }

    public GameConfig asGameConfig() {
        RunConfig config = copy();
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
                throw new RuntimeException("RunConfig: Gamemode " + gameMode + " is not unlocked yet.");
        }
        return new GameConfig(config);
    }

    /**
     * Erstellt und gibt eine Kopie des aktuellen RunConfig-Objekts zurück.
     * @return Eine Kopie des aktuellen RunConfig-Objekts.
     */
    public RunConfig copy(){
        return new RunConfig(this);
    }

    public String toString() {
        return "RunConfig{" +
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
