package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.PlayerHandler;

public final class RemotePlayerHandlerFactory extends PlayerHandlerFactory {

    @Override
    public String getName() {
        return "Remote Player";
    }

    @Override
    public PlayerHandler createPlayerHandler(InputProcessor inputProcessor, int gameId, int playerId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
