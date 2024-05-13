package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.*;
import com.gatdsen.simulation.PlayerController;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.concurrent.Future;

public abstract class PlayerHandlerFactory {

    /**
     * Diese Klasse wird im UI in der PlayerAttribute Klasse bei der Auswahl von Spielerklassen verwendet, wobei jede
     * dieser Klassen einem Eintrag zugeordnet wird. Für die Anzeige wird dabei die String-Repräsentation der jeweiligen
     * Klassen verwendet, daher gibt diese toString() Methode den Namen zurück, damit die Spielernamen korrekt angezeigt
     * werden können.
     */
    @Override
    public String toString() {
        return getName();
    }

    public abstract String getName();

    public abstract Future<PlayerHandler> createPlayerHandler(int playerIndex, PlayerController controller, InputProcessor inputProcessor);

    public static PlayerHandlerFactory[] getAvailablePlayerFactories() {
        PlayerClassReference[] references = PlayerClassReference.getAvailablePlayerClassReferences();
        PlayerHandlerFactory[] factories = new PlayerHandlerFactory[references.length];
        for (int i = 0; i < references.length; i++) {
            factories[i] = new LocalPlayerHandlerFactory(references[i]);
        }
        return factories;
    }

    public static PlayerHandlerFactory[] getPlayerFactories(String[] fileNames) {
        PlayerHandlerFactory[] factories = new PlayerHandlerFactory[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            factories[i] = getPlayerFactory(fileNames[i]);
        }
        return factories;
    }

    public static PlayerHandlerFactory getPlayerFactory(PlayerClassReference classReference) {
        return new LocalPlayerHandlerFactory(classReference);
    }

    public static PlayerHandlerFactory getPlayerFactory(String fileName) {
        return getPlayerFactory(PlayerClassReference.getPlayerClassReference(fileName));
    }
}
