package com.gatdsen.simulation;

import org.apache.commons.cli.CommandLine;

import java.io.Serializable;

/**
 * Abstrakte Klasse für die verschiedenen Spielmodi, welche die Standardwerte für die Spielmodi enthält.
 */
public abstract class GameMode implements Serializable {

    public enum Type {
        /* Rückwärtskompatibilität:
         * Diese Reihenfolge stammt von einer früheren Version des Enums und sollte nicht verändert werden, damit die
         * numerischen IDs der GameModes gleich bleiben und über die Kommandozeile weiterhin funktionieren.
         */
        NORMAL,
        CAMPAIGN,
        EXAM_ADMISSION,
        TOURNAMENT_PHASE_1,
        TOURNAMENT_PHASE_2,
        REPLAY,
        CHRISTMAS_TASK,
    }

    public boolean isAvailable() {
        return true;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    abstract public String getDisplayName();

    abstract public Type getType();

    abstract public String[] getIdentifiers();

    abstract public void parseFromCommandArguments(CommandLine params);

    /**
     * Überprüft, ob die GameMode-Konfiguration gültig ist. Wenn nicht, werden die Fehlermeldungen an den übergebenen
     * StringBuilder angehängt und false zurückgegeben.
     * @param errorMessages Der StringBuilder, an den die Fehlermeldungen angehängt werden sollen
     * @return true, wenn die Konfiguration gültig ist, ansonsten false
     */
    abstract public boolean validate(StringBuilder errorMessages);
}
