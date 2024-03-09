package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.command.Command;
import com.gatdsen.manager.command.CommandHandler;
import com.gatdsen.manager.concurrent.RMICommunicator;
import com.gatdsen.manager.player.Player;
import com.gatdsen.networking.rmi.message.*;
import com.gatdsen.simulation.GameState;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class RemotePlayerHandler extends PlayerHandler {

    public static final String GAME_REMOTE_REFERENCE_NAME_FORMAT = "GameCommunicator_%d_%d";
    public static final String PLAYER_REMOTE_REFERENCE_NAME_FORMAT = "PlayerCommunicator_%d_%d";

    private final RMICommunicator communicator;
    private final Class<? extends Player> playerClass;
    private final CompletableFuture<Long> createFuture = new CompletableFuture<>();
    private final CompletableFuture<?> initFuture = new CompletableFuture<>();
    private CompletableFuture<?> executeTurnFuture = null;

    public RemotePlayerHandler(int playerIndex) {
        super(playerIndex);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RemotePlayerHandler(RMICommunicator communicator, int playerIndex) {
        this(communicator, playerIndex, null);
    }

    protected RemotePlayerHandler(RMICommunicator communicator, int playerIndex, Class<? extends Player> playerClass) {
        super(playerIndex);
        this.communicator = communicator;
        this.playerClass = playerClass;
    }

    @Override
    public Future<Long> create(boolean isDebug, int gameId) {
        assert !createFuture.isDone() : "PlayerHandler.create() should only be called once";
        communicator.setMessageHandler(message -> {
            if (message.getType() != Message.Type.GameCreateResponse) {
                throw new UnexpectedMessageException(message, Message.Type.GameCreateResponse);
            }
            GameCreateResponse response = (GameCreateResponse) message;
            playerInformation = response.playerInformation;
            createFuture.complete(response.seedModifier);
            communicator.setMessageHandler(null);
        });
        communicator.communicate(new GameCreateRequest(isDebug, gameId, playerIndex, playerClass));
        return createFuture;
    }

    @Override
    public Future<?> init(GameState gameState, long seed) {
        assert createFuture.isDone() : "PlayerHandler.init() should only be called after PlayerHandler.create() has completed";
        assert !initFuture.isDone() : "PlayerHandler.init() should only be called once";
        communicator.setMessageHandler(message -> {
            if (message.getType() != Message.Type.PlayerInitResponse) {
                throw new UnexpectedMessageException(message, Message.Type.PlayerInitResponse);
            }
            initFuture.complete(null);
            communicator.setMessageHandler(null);
        });
        communicator.communicate(new PlayerInitRequest(gameState, seed));
        return initFuture;
    }

    @Override
    protected Future<?> onExecuteTurn(GameState gameState, CommandHandler commandHandler) {
        assert initFuture.isDone() : "PlayerHandler.executeTurn() should only be called after PlayerHandler.init() has completed";
        assert executeTurnFuture == null || executeTurnFuture.isDone() : "PlayerHandler.executeTurn() should only be called once the previously returned Future is done";
        executeTurnFuture = new CompletableFuture<>();
        communicator.setMessageHandler(message -> {
            if (message.getType() != Message.Type.PlayerCommandResponse) {
                throw new UnexpectedMessageException(message, Message.Type.PlayerCommandResponse);
            }
            Command command = ((PlayerCommandResponse) message).command;
            commandHandler.handleCommand(command);
            if (command.endsTurn()) {
                executeTurnFuture.complete(null);
                communicator.setMessageHandler(null);
            }
        });
        communicator.communicate(new PlayerExecuteTurnRequest(gameState));
        return executeTurnFuture;
    }

    @Override
    public void dispose() {
        // TODO: release RMICommunicator back to the ResourcePool
    }
}