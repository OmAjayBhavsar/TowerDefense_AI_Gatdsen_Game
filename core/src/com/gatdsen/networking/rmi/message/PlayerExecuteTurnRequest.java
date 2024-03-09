package com.gatdsen.networking.rmi.message;

import com.gatdsen.simulation.GameState;

public final class PlayerExecuteTurnRequest implements Message {

    public final GameState state;

    public PlayerExecuteTurnRequest(GameState state) {
        this.state = state;
    }

    @Override
    public Type getType() {
        return Type.PlayerExecuteTurnRequest;
    }
}
