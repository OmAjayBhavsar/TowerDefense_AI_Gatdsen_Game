package com.gatdsen.networking.rmi.message;

import com.gatdsen.simulation.GameState;

/**
 * Diese Nachricht wird vom Spielprozess an einen Spielerprozess gesendet, um diesen mit den gegebenen Parametern auf
 * die Initialisierung des Spielers vorzubereiten.
 * Als Antwort auf diese Nachricht wird eine {@link PlayerInitResponse} erwartet.
 */
public final class PlayerInitRequest implements Message {

    /** Der initiale Zustand des Spiels */
    public final GameState state;
    /** Der Seed des Spiels */
    public final long seed;

    /**
     * @param state Der initiale Zustand des Spiels
     * @param seed Der Seed des Spiels
     */
    public PlayerInitRequest(GameState state, long seed) {
        this.state = state;
        this.seed = seed;
    }

    @Override
    public Type getType() {
        return Type.PlayerInitRequest;
    }
}
