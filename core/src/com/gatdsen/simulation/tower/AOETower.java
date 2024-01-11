package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tile;
import com.gatdsen.simulation.Tower;

/**
 * Speichert einen AOETower.
 */
public class AOETower extends Tower {
    /**
     * Erstellt einen AOETower an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    public AOETower(PlayerState playerState, int x, int y, Tile[][] board) {
        super(playerState, TowerType.AOE_TOWER, x, y, board);
    }

    /**
     * Erstellt eine Kopie eines AOETower.
     *
     * @param original der zu kopierende AOETower
     */
    public AOETower(Tower original) {
        super(original);
    }

    /**
     * Erstellt eine Kopie eines AOETower.
     *
     * @return eine Kopie des AOETower
     */
    @Override
    protected Tower copy() {
        return new AOETower(this);
    }

    /**
     * @return Den Schaden des AOETower
     */
    @Override
    public int getDamage() {
        switch (level) {
            case 1: return 0;
            case 2: return 0;
            case 3: return 0;
            default: return 0;
        }
    }

    /**
     * @return Die Reichweite des AOETower
     */
    @Override
    public int getRange() {
        return 0;
    }

    /**
     * @return Die Zeit, die der AOETower zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 0;
    }

    /**
     * @return Den Preis des AOETower
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
