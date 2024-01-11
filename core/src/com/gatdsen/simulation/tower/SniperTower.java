package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tile;
import com.gatdsen.simulation.Tower;

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
            case 1:
                return 0;
            case 2:
                return 0;
            case 3:
                return 0;
            default:
                return 0;
        }

    }

    /**
     * @return Die Reichweite des SniperTower
     */
    @Override
    public int getRange() {
        return 0;
    }

    /**
     * @return Die Zeit, die der SniperTower zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 0;
    }

    /**
     * @return Den Preis des SniperTower
     */
    @Override
    public int getPrice() {
        switch (level) {
            case 1: return 0;
            case 2: return 0;
            case 3: return 0;
            default: return 0;
        }
    }
}

