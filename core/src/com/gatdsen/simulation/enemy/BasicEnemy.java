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
    }

    @Override
    protected Enemy copy(PathTile posTile) {
        return new BasicEnemy(playerState, level, posTile);
    }
}
