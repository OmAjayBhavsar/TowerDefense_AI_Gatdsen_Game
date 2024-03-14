package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.InputProcessor;
import com.gatdsen.manager.PlayerExecutor;
import com.gatdsen.manager.command.Command;
import com.gatdsen.manager.player.Player;
import com.gatdsen.manager.player.data.penalty.Penalty;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.PlayerController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public final class LocalPlayerHandler extends PlayerHandler {

    private final PlayerClassReference playerClassReference;
    private final InputProcessor inputGenerator;
    private PlayerExecutor playerExecutor;

    public LocalPlayerHandler(int playerIndex, PlayerClassReference playerClassReference, PlayerController controller, InputProcessor inputGenerator) {
        super(playerIndex, controller);
        this.playerClassReference = playerClassReference;
        this.inputGenerator = inputGenerator;
    }

    @Override
    public Future<Long> create(boolean isDebug, int gameId) {
        playerExecutor = new PlayerExecutor(isDebug, playerIndex, playerClassReference.getPlayerClass(), inputGenerator);
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
    protected Future<?> onExecuteTurn(GameState gameState, Command.CommandHandler commandHandler) {
        return playerExecutor.executeTurn(gameState, commandHandler);
    }

    @Override
    public void dispose(boolean gameCompleted) {
        // Die Threads vom PlayerExecutor können nur wiederverwendet werden, wenn der Spieler nicht disqualifiziert
        // wurde und das Spiel nicht abgebrochen wurde.
        playerExecutor.dispose(gameCompleted && !isDisqualified());
    }
}
