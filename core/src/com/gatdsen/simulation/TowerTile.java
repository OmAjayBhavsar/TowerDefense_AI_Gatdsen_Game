package com.gatdsen.simulation;

import com.gatdsen.simulation.tower.*;

/**
 * Speichert ein Tile, auf dem ein Tower steht.
 */
public class TowerTile extends Tile {
    private final Tower tower;

    /**
     * Kopiert ein TowerTile.
     *
     * @return eine Kopie des TowerTiles
     */
    @Override
    protected Tile copy() {
        return new TowerTile(this);
    }

    /**
     * Erstellt eine Kopie eines TowerTiles.
     *
     * @param original das zu kopierende TowerTile
     */
    TowerTile(TowerTile original) {
        super(original.getPosition().x, original.getPosition().y);
        this.tower = original.tower;
    }

    /**
     * Erstellt ein TowerTile an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param type        der Typ des Towers
     */
    TowerTile(PlayerState playerState, int x, int y, Tower.TowerType type) {
        super(x, y);
        Tower tower1 = null;
        switch (type) {
            case MINIGUN_CAT:
                tower1 = new MinigunCat(playerState, x, y, playerState.getBoard());
                break;
            case CATANA_CAT:
                tower1 = new CatanaCat(playerState, x, y, playerState.getBoard());
                break;
            case MAGE_CAT:
                tower1 = new MageCat(playerState, x, y, playerState.getBoard());

        }
        this.tower = tower1;
    }

    /**
     * @return False, da TowerTiles nicht bebaubar sind
     */
    @Override
    public boolean isBuildable() {
        return false;
    }

    /**
     * @return false, da TowerTiles keine Pfade sind
     */
    @Override
    public boolean isPath() {
        return false;
    }
    /**
     * @return den Tower, der auf dem Tile steht
     */
    public Tower getTower() {
        return tower;
    }
}
