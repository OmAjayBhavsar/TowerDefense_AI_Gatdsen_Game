package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.player.Player;

public final class GameCreateRequest implements Message {

    public final boolean isDebug;
    public final int gameId;
    public final int playerIndex;
    public final Class<? extends Player> playerClass;

    public GameCreateRequest(boolean isDebug, int gameId, int playerIndex) {
        this(isDebug, gameId, playerIndex, null);
    }

    public GameCreateRequest(boolean isDebug, int gameId, int playerIndex, Class<? extends Player> playerClass) {
        this.isDebug = isDebug;
        this.gameId = gameId;
        this.playerIndex = playerIndex;
        this.playerClass = playerClass;
    }

    @Override
    public Type getType() {
        return Type.GameCreateRequest;
    }
}
