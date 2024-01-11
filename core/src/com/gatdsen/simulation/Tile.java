package com.gatdsen.simulation;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * Speichert ein Tile, das Teil der Map ist.
 */
public abstract class Tile implements Serializable {
    protected IntVector2 pos;

    /**
     * erstellt eine Kopie des Tiles
     *
     * @return eine Kopie des Tiles
     */
    protected abstract Tile copy();

    /**
     * Erstellt ein Tile an der angegebenen Position.
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     */
    protected Tile(int x, int y) {
        this.pos = new IntVector2(x, y);
    }

    /**
     * @return True, wenn das Tile bebaubar ist
     */
    public abstract boolean isBuildable();

    /**
     * @return die Position des Tiles als IntVector2
     */
    public IntVector2 getPosition() {
        return pos;
    }

    /**
     * @return das Tile als String
     */
    @Override
    public String toString() {
        return "Tile{" +
                ", pos=" + pos +
                '}';
    }
}
