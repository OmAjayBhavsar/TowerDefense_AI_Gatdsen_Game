package com.gatdsen.networking.rmi.message;

import com.gatdsen.simulation.GameState;

public final class StartGameMessage implements Message {

    public final GameState state;
    public final boolean isDebug;
    public final long seed;
    public final int playerIndex;

    public StartGameMessage(GameState state, boolean isDebug, long seed, int playerIndex) {
        this.state = state;
        this.isDebug = isDebug;
        this.seed = seed;
        this.playerIndex = playerIndex;
    }
}