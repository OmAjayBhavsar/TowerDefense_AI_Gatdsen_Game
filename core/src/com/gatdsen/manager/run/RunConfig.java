package com.gatdsen.manager.run;

import com.gatdsen.manager.AnimationLogProcessor;
import com.gatdsen.manager.game.GameConfig;
import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.GameMode;

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
        replay = original.replay;
    }

    public GameMode gameMode = null;
    public boolean gui = true;
    public AnimationLogProcessor animationLogProcessor = null;
    public InputProcessor inputProcessor = null;
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
        return validate(new StringBuilder());
    }

    /**
     * Überprüft, ob die RunConfig gültig ist. Wenn nicht, werden die Fehlermeldungen an den übergebenen StringBuilder
     * angehängt und false zurückgegeben.
     * @param errorMessages Der StringBuilder, an den die Fehlermeldungen angehängt werden sollen
     * @return true, wenn die RunConfig gültig ist, ansonsten false
     */
    private boolean validate(StringBuilder errorMessages) {
        boolean isValid;
        if (gameMode == null) {
            errorMessages.append("RunConfig: No game mode was provided.\n");
            isValid = false;
        } else {
            isValid = gameMode.validate(errorMessages);
            if (gameMode.getType() == GameMode.Type.REPLAY && replay) {
                errorMessages.append("RunConfig: A replay of the replay mode can't be created. Why would you do that anyway??\n");
                isValid = false;
            }
        }
        return isValid;
    }

    public GameConfig asGameConfig() {
        return new GameConfig(copy());
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
                ", replay=" + replay +
                "}";
    }
}
