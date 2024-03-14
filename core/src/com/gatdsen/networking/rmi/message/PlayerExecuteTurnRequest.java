package com.gatdsen.networking.rmi.message;

import com.gatdsen.simulation.GameState;

/**
 * Diese Nachricht wird vom Spielprozess an einen Spielerprozess gesendet, um diesen mit den gegebenen Parametern auf
 * die Durchführung eines Spielzugs durch den Spieler vorzubereiten.
 * Als Antwort auf diese Nachricht werden mehrere {@link PlayerCommandResponse}, sowie anschließend eine finale
 * {@link PlayerExecuteTurnResponse} erwartet.
 */
public final class PlayerExecuteTurnRequest implements Message {

    /** Der Zustand des Spiels zur aktuellen Runde */
    public final GameState state;

    /**
     * @param state Der Zustand des Spiels zur aktuellen Runde
     */
    public PlayerExecuteTurnRequest(GameState state) {
        this.state = state;
    }

    @Override
    public Type getType() {
        return Type.PlayerExecuteTurnRequest;
    }
}
