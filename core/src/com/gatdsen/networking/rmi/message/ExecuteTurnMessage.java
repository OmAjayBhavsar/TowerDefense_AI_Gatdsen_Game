package com.gatdsen.networking.rmi.message;

import com.gatdsen.simulation.GameState;

public final class ExecuteTurnMessage implements Message {

    public final GameState state;

    public ExecuteTurnMessage(GameState state) {
        this.state = state;
    }
}
