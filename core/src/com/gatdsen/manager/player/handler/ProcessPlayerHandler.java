package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.concurrent.ProcessExecutor;
import com.gatdsen.manager.concurrent.ResourcePool;
import com.gatdsen.manager.player.Player;
import com.gatdsen.simulation.PlayerController;

public final class ProcessPlayerHandler extends RemotePlayerHandler {

    private final ProcessExecutor process;

    public ProcessPlayerHandler(int playerIndex, Class<? extends Player> playerClass, PlayerController controller) {
        this(ResourcePool.getInstance().requestProcessExecutor(), playerIndex, playerClass, controller);
    }

    public ProcessPlayerHandler(ProcessExecutor process, int playerIndex, Class<? extends Player> playerClass, PlayerController controller) {
        super(process.communicator, playerIndex, playerClass, controller);
        this.process = process;
    }

    @Override
    public void dispose() {
        ResourcePool.getInstance().releaseProcessExecutor(process, !isDisqualified());
    }
}
