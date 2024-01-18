package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tile;
import com.gatdsen.simulation.Tower;
import com.gatdsen.simulation.action.Action;

/**
 * Speichert einen SniperTower.
 */
public class SniperTower extends Tower {
    /**
     * Erstellt einen SniperTower an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    public SniperTower(PlayerState playerState, int x, int y, Tile[][] board) {
        super(playerState, TowerType.SNIPER_TOWER, x, y, board);
    }

    /**
     * Erstellt eine Kopie eines SniperTower.
     *
     * @param original der zu kopierende SniperTower
     */
    public SniperTower(Tower original) {
        super(original);
    }

    /**
     * Erstellt eine Kopie eines SniperTower.
     *
     * @return eine Kopie des SniperTower
     */
    @Override
    protected Tower copy() {
        return new SniperTower(this);
    }

    /**
     * @return Den Schaden des SniperTower
     */
    @Override
    public int getDamage() {
        switch (level) {
            case 1: return 100; // One shot one kill
            case 2: return 200;
            case 3: return 300;
            default: return 0;
        }
    }

    /**
     * @return Die Reichweite des SniperTower
     */
    @Override
    public int getRange() {
        return 4;
    }

    /**
     * @return Die Zeit, die der SniperTower zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 3;
    }

    /**
     * @return Den Preis des SniperTower
     */
    @Override
    public int getPrice() {
        switch (level) {
            case 1: return 100;
            case 2: return 150;
            case 3: return 250;
            default: return 0;
        }
    }
}

