package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.player.data.penalty.Penalty;

public final class PlayerInitResponse implements Message {

    public final Penalty penalty;

    public PlayerInitResponse(Penalty penalty) {
        this.penalty = penalty;
    }

    @Override
    public Type getType() {
        return Type.PlayerInitResponse;
    }
}
