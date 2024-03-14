package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.simulation.PlayerController;

import java.util.concurrent.Future;

public final class RemotePlayerHandlerFactory extends PlayerHandlerFactory {

    @Override
    public String getName() {
        return "Remote Player";
    }

    @Override
    public Future<PlayerHandler> createPlayerHandler(int playerId, PlayerController controller, InputProcessor inputProcessor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
