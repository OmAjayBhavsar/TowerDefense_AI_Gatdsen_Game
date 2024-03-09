package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.player.data.PlayerInformation;

public final class GameCreateResponse implements Message {

    public final PlayerInformation playerInformation;
    public final long seedModifier;

    public GameCreateResponse(PlayerInformation playerInformation, long seedModifier) {
        this.playerInformation = playerInformation;
        this.seedModifier = seedModifier;
    }

    @Override
    public Type getType() {
        return Type.GameCreateResponse;
    }
}
