package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;

import java.util.concurrent.Future;

public final class RemotePlayerHandlerFactory extends PlayerHandlerFactory {

    @Override
    public String getName() {
        return "Remote Player";
    }

    @Override
    public Future<PlayerHandler> createPlayerHandler(InputProcessor inputProcessor, int playerId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
