package com.gatdsen.simulation.enemy;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.PathTile;
import com.gatdsen.simulation.PlayerState;

public class ShieldEnemy extends Enemy {
    /**
     * Erstellt einen neuen Shield-Gegner.
     *
     * @param playerState Der Spieler, dem der Gegner gehört.
     * @param level       Die Stufe des Gegners.
     * @param posTile     Die Position des Gegners.
     */
    public ShieldEnemy(PlayerState playerState, int level, PathTile posTile) {
        super(playerState, level, posTile);
        enemyType = EnemyType.SHIELD_ENEMY;
        health = 150 * level;
        damage = 5 * level;
    }

    @Override
    protected Enemy copy(PathTile posTile) {
        return new ShieldEnemy(playerState, level, posTile);
    }

    /**
     * Gibt den Preis des Gegners zurück.
     * @param level
     * @return Der Preis des Gegners.
     */
    public static int getPrice(int level) {
        return 8 * level;
    }
}
