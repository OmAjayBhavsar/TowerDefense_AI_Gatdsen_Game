package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.PlayerExecutor;
import com.gatdsen.manager.command.Command;
import com.gatdsen.manager.command.CommandHandler;
import com.gatdsen.manager.concurrent.ThreadExecutor;
import com.gatdsen.manager.player.Player;
import com.gatdsen.manager.player.data.penalty.Penalty;
import com.gatdsen.simulation.GameState;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class LocalPlayerHandler extends PlayerHandler {

    private final Class<? extends Player> playerClass;
    private final InputProcessor inputGenerator;
    private PlayerExecutor playerExecutor;

    public LocalPlayerHandler(int playerIndex, Class<? extends Player> playerClass, InputProcessor inputGenerator) {
        super(playerIndex);
        this.playerClass = playerClass;
        this.inputGenerator = inputGenerator;
    }

    @Override
    public Future<Long> create(boolean isDebug, int gameId) {
        playerExecutor = new PlayerExecutor(isDebug, playerIndex, playerClass, inputGenerator);
        playerInformation = playerExecutor.getPlayerInformation();
        return CompletableFuture.completedFuture(playerExecutor.getPlayerClassAnalyzer().getSeedModifier());
    }

    @Override
    public Future<?> init(GameState gameState, long seed) {
        Penalty penalty = playerExecutor.init(gameState, seed);
        if (penalty != null) {
            penalize(penalty);
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    protected Future<?> onExecuteTurn(GameState gameState, CommandHandler commandHandler) {
        return playerExecutor.executeTurn(gameState, commandHandler);
    }

    @Override
    public void dispose() {
        playerExecutor.dispose();
    }
}
