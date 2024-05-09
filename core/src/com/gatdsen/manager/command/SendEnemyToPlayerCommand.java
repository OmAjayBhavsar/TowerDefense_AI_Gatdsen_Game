package com.gatdsen.manager.command;

import com.gatdsen.manager.player.handler.PlayerHandler;
import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.action.ActionLog;

/**
 * Dieser Befehl sendet einen Gegner zu einem gegnerischen Spieler auf das Spielfeld.
 */
public class SendEnemyToPlayerCommand extends Command {

    protected final Enemy.EnemyType enemyType;

    /**
     * Erstellt einen neuen Befehl, der einen Gegner zu einem gegnerischen Spieler auf das Spielfeld sendet.
     * @param enemyType Der Typ des Gegners, der gesendet werden soll
     */
    public SendEnemyToPlayerCommand(Enemy.EnemyType enemyType) {
        this.enemyType = enemyType;
    }

    @Override
    protected ActionLog onExecute(PlayerHandler playerHandler) {
        return playerHandler.getPlayerController().sendEnemy(enemyType);
    }
}
