package com.gatdsen.manager.player.handler;

import com.gatdsen.manager.command.Command;
import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.manager.player.data.penalty.DisqualificationPenalty;
import com.gatdsen.manager.player.data.penalty.MissTurnsPenalty;
import com.gatdsen.manager.player.data.penalty.Penalty;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.PlayerController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public abstract class PlayerHandler {

    protected final int playerIndex;
    protected final PlayerController controller;
    protected PlayerInformation playerInformation;

    protected int turnsToMiss = 0;

    public PlayerHandler(int playerIndex, PlayerController controller) {
        this.playerIndex = playerIndex;
        this.controller = controller;
    }

    public final PlayerController getPlayerController() {
        return controller;
    }

    public final PlayerInformation getPlayerInformation() {
        return playerInformation;
    }

    public final void penalize(Penalty penalty) {
        if (isDisqualified()) {
            return;
        }
        if (penalty instanceof MissTurnsPenalty) {
            turnsToMiss += ((MissTurnsPenalty) penalty).turns;
        } else if (penalty instanceof DisqualificationPenalty) {
            controller.disqualify();
            turnsToMiss = -1;
        }
    }

    public final boolean isDisqualified() {
        return turnsToMiss < 0;
    }

    public abstract Future<Long> create(boolean isDebug, int gameId);

    public abstract Future<?> init(GameState gameState, long seed);

    public final Future<?> executeTurn(GameState gameState, Command.CommandHandler commandHandler) {
        if (turnsToMiss > 0) {
            turnsToMiss--;
            return CompletableFuture.completedFuture(null);
        }
        return onExecuteTurn(gameState, commandHandler);
    }

    protected abstract Future<?> onExecuteTurn(GameState gameState, Command.CommandHandler commandHandler);

    public abstract void dispose();
}
