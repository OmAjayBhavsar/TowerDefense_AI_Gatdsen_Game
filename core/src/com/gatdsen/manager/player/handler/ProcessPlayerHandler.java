package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.concurrent.ProcessExecutor;
import com.gatdsen.manager.concurrent.ResourcePool;
import com.gatdsen.manager.player.Player;

public final class ProcessPlayerHandler extends RemotePlayerHandler {

    private final ProcessExecutor process;

    public ProcessPlayerHandler(int playerIndex, Class<? extends Player> playerClass) {
        this(ResourcePool.getInstance().requestProcessExecutor(), playerIndex, playerClass);
    }

    public ProcessPlayerHandler(ProcessExecutor process, int playerIndex, Class<? extends Player> playerClass) {
        super(process.communicator, playerIndex, playerClass);
        this.process = process;
    }

    @Override
    public void dispose() {
        ResourcePool.getInstance().releaseProcessExecutor(process, !isDisqualified());
    }
}
