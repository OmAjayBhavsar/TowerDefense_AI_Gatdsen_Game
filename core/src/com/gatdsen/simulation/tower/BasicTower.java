package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tile;
import com.gatdsen.simulation.Tower;

/**
 * Speichert einen BasicTower.
 */
public class BasicTower extends Tower {
    /**
     * Erstellt einen BasicTower an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    public BasicTower(PlayerState playerState, int x, int y, Tile[][] board) {
        super(playerState, TowerType.BASIC_TOWER, x, y, board);
    }

    /**
     * Erstellt eine Kopie eines BasicTowers.
     *
     * @param original der zu kopierende BasicTower
     */
    public BasicTower(Tower original) {
        super(original);
    }

    /**
     * Erstellt eine Kopie eines BasicTowers.
     *
     * @return eine Kopie des BasicTowers
     */
    @Override
    protected Tower copy() {
        return new BasicTower(this);
    }

    /**
     * @return Den Schaden des BasicTowers
     */
    @Override
    public int getDamage() {
        switch (level) {
            case 1: return 35;
            case 2: return 60;
            case 3: return 90;
            default: return 0;
        }
    }

    /**
     * @return Die Reichweite des BasicTowers
     */
    @Override
    public int getRange() {
        return 2;
    }

    /**
     * @return Die Zeit, die der BasicTower zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 0;
    }

    /**
     * @return Den Preis des BasicTowers
     */
    @Override
    public int getPrice() {
        switch (level) {
            case 1: return 80;
            case 2: return 90;
            case 3: return 100;
            default: return 0;
        }
    }
}
