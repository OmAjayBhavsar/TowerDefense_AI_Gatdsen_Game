package com.gatdsen.networking.rmi.message;

import com.gatdsen.simulation.GameState;

public final class PlayerInitRequest implements Message {

    public final GameState state;
    public final long seed;

    public PlayerInitRequest(GameState state, long seed) {
        this.state = state;
        this.seed = seed;
    }

    @Override
    public Type getType() {
        return Type.PlayerInitRequest;
    }
}
