package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tile;
import com.gatdsen.simulation.Tower;

/**
 * Speichert einen MageCat.
 */
public class MageCat extends Tower {
    /**
     * Erstellt einen MageCat an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    public MageCat(PlayerState playerState, int x, int y, Tile[][] board) {
        super(playerState, TowerType.MAGE_CAT, x, y, board);
    }

    /**
     * Erstellt eine Kopie eines MageCat.
     *
     * @param original der zu kopierende MageCat
     */
    public MageCat(Tower original, PlayerState playerState) {
        super(original, playerState);
    }

    /**
     * Erstellt eine Kopie eines MageCat.
     *
     * @return eine Kopie des MageCat
     */
    @Override
    protected Tower copy(PlayerState NewPlayerState) {
        return new MageCat(this, NewPlayerState);
    }

    /**
     * @return Den Schaden des MageCat
     */
    @Override
    public int getDamage() {
        switch (level) {
            case 1: return 150; // One shot one kill
            case 2: return 250;
            case 3: return 350;
            default: return 0;
        }
    }

    /**
     * @return Die Reichweite des MageCat
     */
    @Override
    public int getRange() {
        return 4;
    }

    /**
     * @return Die Zeit, die der MageCat zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 3;
    }

    @Override
    public void incrementRechargeTime() {
        cooldown += 3;
    }

    /**
     * @return Den Preis des MageCat
     */
    @Override
    public int getUpgradePrice() {
        switch (level) {
            case 1: return 100;
            case 2: return 150;
            case 3: return 250;
            default: return 0;
        }
    }
}

