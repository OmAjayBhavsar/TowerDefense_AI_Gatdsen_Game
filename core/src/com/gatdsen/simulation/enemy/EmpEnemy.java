package com.gatdsen.simulation.enemy;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.PathTile;
import com.gatdsen.simulation.PlayerState;

public class EmpEnemy extends Enemy {

    /**
     * Erstellt einen neuen Gegner.
     *
     * @param playerState
     * @param level       Die Stufe des Gegners.
     * @param posTile     Die Position des Gegners.
     */

    public EmpEnemy(PlayerState playerState, int level, PathTile posTile) {
        super(playerState, level, posTile);
        health = 150 * level;
        damage = 5 * level;
    }



    /**
     * Erstellt eine Kopie des Gegners.
     *
     * @param posTile Die Position des Gegners.
     * @return Die Kopie des Gegners.
     */
    @Override
    protected Enemy copy(PathTile posTile) {
        return new EmpEnemy(playerState, level, posTile);
    }
}
