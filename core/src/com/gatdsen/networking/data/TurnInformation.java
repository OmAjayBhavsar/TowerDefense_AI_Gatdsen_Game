package com.gatdsen.networking.data;

import com.gatdsen.simulation.GameState;

public final class TurnInformation implements CommunicatedInformation {

    public final GameState state;

    public TurnInformation(GameState state) {
        this.state = state;
    }
}
