package com.gatdsen.simulation.enemy;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.PathTile;
import com.gatdsen.simulation.PlayerState;

public class ShieldEnemy extends Enemy {

    boolean isShielded = true;

    /**
     * Erstellt einen neuen Gegner.
     *
     * @param playerState Der Spieler, dem der Gegner gehört.
     * @param level       Die Stufe des Gegners.
     * @param posTile     Die Position des Gegners.
     */
    public ShieldEnemy(PlayerState playerState, int level, PathTile posTile) {
        super(playerState, level, posTile);
        type = Type.SHIELD_ENEMY;
        health = 200 * level;
        damage = 5 * level;
    }

    @Override
    protected Enemy copy(PathTile posTile) {
        return new ShieldEnemy(playerState, level, posTile);
    }

    public void setShielded(boolean shielded) {
        isShielded = shielded;
    }
    public boolean isShielded() {
        return isShielded;
    }

    public static int getPrice(int level) {
        return 100 * level;
    }
}
