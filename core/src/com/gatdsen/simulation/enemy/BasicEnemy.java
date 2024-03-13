package com.gatdsen.simulation.enemy;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.PathTile;
import com.gatdsen.simulation.PlayerState;

public class BasicEnemy extends Enemy {

    /**
     * Erstellt einen neuen BasicEnemy.
     *
     * @param playerState
     * @param level       Die Stufe des Gegners.
     * @param posTile     Die Position des Gegners.
     */
    public BasicEnemy(PlayerState playerState, int level, PathTile posTile) {
        super(playerState, level, posTile);
        type = Type.BASIC_ENEMY;
        health = 100 * level;
        damage = 10 * level;
    }


    /**
     * Erstellt eine Kopie des Gegners.
     *
     * @param posTile Die Position des Gegners.
     * @return Die Kopie des Gegners.
     */
    @Override
    protected Enemy copy(PathTile posTile) {
        return new BasicEnemy(playerState, level, posTile);
    }


    public static int getPrice(int level) {
        return 1000000;
    }

}
