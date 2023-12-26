package com.gatdsen.networking.data;

import com.gatdsen.simulation.GameState;

public final class GameInformation implements CommunicatedInformation {

    public final GameState state;
    public final boolean isDebug;
    public final long seed;
    public final int playerIndex;

    public GameInformation(GameState state, boolean isDebug, long seed, int playerIndex) {
        this.state = state;
        this.isDebug = isDebug;
        this.seed = seed;
        this.playerIndex = playerIndex;
    }
}