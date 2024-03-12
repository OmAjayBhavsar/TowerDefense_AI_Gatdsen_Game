package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.player.*;
import com.gatdsen.simulation.PlayerController;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class LocalPlayerHandlerFactory extends PlayerHandlerFactory {

    public static final LocalPlayerHandlerFactory HUMAN_PLAYER = new LocalPlayerHandlerFactory(PlayerClassReference.HUMAN_PLAYER);
    public static final LocalPlayerHandlerFactory IDLE_BOT = new LocalPlayerHandlerFactory(PlayerClassReference.IDLE_BOT);

    private final PlayerClassReference playerClassReference;
    private final String playerName;

    public LocalPlayerHandlerFactory(PlayerClassReference playerClassReference) {
        this.playerClassReference = playerClassReference;
        try {
            Player playerInstance = playerClassReference.getPlayerClass().getConstructor(new Class[]{}).newInstance();
            playerName = playerInstance.getName();
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Insufficient Privileges for instantiating bots", e);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<? extends Player> getPlayerClass() {
        return playerClassReference.getPlayerClass();
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public Future<PlayerHandler> createPlayerHandler(int playerIndex, PlayerController controller, InputProcessor inputProcessor) {
        PlayerHandler playerHandler;
        if (Bot.class.isAssignableFrom(playerClassReference.getPlayerClass())) {
            playerHandler = new ProcessPlayerHandler(playerIndex, playerClassReference, controller);
        } else {
            playerHandler = new LocalPlayerHandler(playerIndex, playerClassReference, controller, inputProcessor);
        }
        return CompletableFuture.completedFuture(playerHandler);
    }
}
