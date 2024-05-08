package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tile;
import com.gatdsen.simulation.Tower;
import com.gatdsen.simulation.TowerTile;

import java.util.List;

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
        int nerf = basicTowerInRange() ? 17 : 0;
        switch (level) {
            case 1: return 35 - nerf;
            case 2: return 60 - nerf;
            case 3: return 90 - nerf;
            default: return 0;
        }
    }

    /**
     * Prüft, ob ein BasicTower in Reichweite ist.
     * @return true, wenn ein BasicTower in Reichweite ist, sonst false
     */
    private boolean basicTowerInRange() {
        List<Tile> inRange = getNeighbours(getRange(), playerState.getBoard());
        for (Tile tile : inRange) {
            if (tile == null || tile.getPosition().equals(pos)) continue;
            if (tile instanceof TowerTile) {
                TowerTile towerTile = (TowerTile) tile;
                if (towerTile.getTower().getType() == TowerType.BASIC_TOWER) {
                    return true;
                }
            }
        }
        return false;
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

    @Override
    public void incrementRechargeTime() {
        cooldown += 2;
    }

    /**
     * @return Den Preis des BasicTowers
     */
    @Override
    public int getUpgradePrice() {
        switch (level) {
            case 1: return 80;
            case 2: return 100;
            case 3: return 120;
            default: return 0;
        }
    }
}
