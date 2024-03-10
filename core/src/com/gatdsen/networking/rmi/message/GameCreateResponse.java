package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.player.data.PlayerInformation;

/**
 * Diese Nachricht wird von einem Spielerprozess an den Spielprozess gesendet, um diesen über die Initialisierung des
 * Spiels, sowie über die Daten des Spielers zu informieren.
 * Diese Nachricht wird als Antwort auf eine {@link GameCreateRequest} gesendet.
 */
public final class GameCreateResponse implements Message {

    /** Die Informationen über den Spieler */
    public final PlayerInformation playerInformation;
    /** Der Seed-Modifikator dieses Spielers, aus welchem sich der Gesamt-Seed des Spiels berechnet */
    public final long seedModifier;

    /**
     * @param playerInformation Die Informationen über den Spieler
     * @param seedModifier Der Seed-Modifikator dieses Spielers, aus welchem sich der Gesamt-Seed des Spiels berechnet
     */
    public GameCreateResponse(PlayerInformation playerInformation, long seedModifier) {
        this.playerInformation = playerInformation;
        this.seedModifier = seedModifier;
    }

    @Override
    public Type getType() {
        return Type.GameCreateResponse;
    }
}
