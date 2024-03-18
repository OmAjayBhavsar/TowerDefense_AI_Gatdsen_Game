package com.gatdsen.simulation.enemy;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.PathTile;
import com.gatdsen.simulation.PlayerState;

public class ArmorEnemy extends Enemy {
    public ArmorEnemy(PlayerState playerState, int level, PathTile posTile) {
        super(playerState, level, posTile);
        type = Type.ARMOR_ENEMY;
        health = 200 * level;
        damage = 5 * level;
    }

    @Override
    protected Enemy copy(PathTile posTile) {
        return new ArmorEnemy(playerState, level, posTile);
    }


    public static int getPrice(int level) {
        return 10 * level;
    }
}
