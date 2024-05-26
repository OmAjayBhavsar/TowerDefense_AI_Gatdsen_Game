package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.command.Command;
import com.gatdsen.manager.concurrent.RMICommunicator;
import com.gatdsen.manager.player.Player;
import com.gatdsen.manager.player.data.penalty.Penalty;
import com.gatdsen.networking.rmi.message.*;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.PlayerController;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class RemotePlayerHandler extends PlayerHandler {

    private final RMICommunicator communicator;
    private final PlayerClassReference playerClassReference;
    private final CompletableFuture<Long> createFuture = new CompletableFuture<>();
    private final CompletableFuture<?> initFuture = new CompletableFuture<>();
    private CompletableFuture<?> executeTurnFuture = null;

    public RemotePlayerHandler(int playerIndex, PlayerController controller) {
        super(playerIndex, controller);
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RemotePlayerHandler(RMICommunicator communicator, int playerIndex, PlayerController controller) {
        this(communicator, playerIndex, null, controller);
    }

    protected RemotePlayerHandler(RMICommunicator communicator, int playerIndex, PlayerClassReference playerClassReference, PlayerController controller) {
        super(playerIndex, controller);
        this.communicator = communicator;
        this.playerClassReference = playerClassReference;
    }

    @Override
    public CompletableFuture<Long> create(boolean isDebug, int gameId) {
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
        communicator.communicate(new GameCreateRequest(isDebug, gameId, playerIndex, playerClassReference));
        return createFuture;
    }

    @Override
    public CompletableFuture<?> init(GameState gameState, long seed) {
        assert createFuture.isDone() : "PlayerHandler.init() should only be called after PlayerHandler.create() has completed";
        assert !initFuture.isDone() : "PlayerHandler.init() should only be called once";
        communicator.setMessageHandler(message -> {
            if (message.getType() != Message.Type.PlayerInitResponse) {
                throw new UnexpectedMessageException(message, Message.Type.PlayerInitResponse);
            }
            penalize(((PlayerInitResponse) message).penalty);
            initFuture.complete(null);
            communicator.setMessageHandler(null);
        });
        communicator.communicate(new PlayerInitRequest(gameState, seed));
        return initFuture;
    }

    @Override
    protected CompletableFuture<?> onExecuteTurn(GameState gameState, Command.CommandHandler commandHandler) {
        assert initFuture.isDone() : "PlayerHandler.executeTurn() should only be called after PlayerHandler.init() has completed";
        assert executeTurnFuture == null || executeTurnFuture.isDone() : "PlayerHandler.executeTurn() should only be called once the previously returned Future is done";
        executeTurnFuture = new CompletableFuture<>();
        communicator.setMessageHandler(message -> {
            if (message.getType() == Message.Type.PlayerCommandResponse) {
                commandHandler.handle(((PlayerCommandResponse) message).commands);
                return;
            } else if (message.getType() == Message.Type.PlayerExecuteTurnResponse) {
                penalize(((PlayerExecuteTurnResponse) message).penalty);
                communicator.setMessageHandler(null);
                executeTurnFuture.complete(null);
                return;
            }
            throw new UnexpectedMessageException(message, Message.Type.PlayerCommandResponse, Message.Type.PlayerExecuteTurnResponse);
        });
        communicator.communicate(new PlayerExecuteTurnRequest(gameState));
        return executeTurnFuture;
    }

    @Override
    public void dispose(boolean gameCompleted) {
        // TODO: release RMICommunicator back to the ResourcePool
    }
}