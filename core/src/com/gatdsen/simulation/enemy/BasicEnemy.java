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
        System.out.println(posTile.toString());
        switch (level) {
            case 1:
                health = 100;
                break;
            case 2:
                health = 150;
                break;
            case 3:
                health = 200;
                break;
            default:
                health = 0;
        }

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

}
