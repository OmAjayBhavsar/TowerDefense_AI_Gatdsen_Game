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
     * @return True, wenn das Tile ein Pfad ist
     */
    public abstract boolean isPath();

    /**
     * @return die Position des Tiles als IntVector2
     */
    public IntVector2 getPosition() {
        return pos;
    }

    /**
     * Gibt die umliegenden Tiles in einer bestimmten Reichweite zurück
     *
     * @param range Reichweite um das Tile herum
     * @param pos   Position des Tiles
     * @param board Map auf der nachgeschaut wird
     * @return Liste der umliegenden Tiles
     */
    public static List<Tile> getNeighbours(int range, IntVector2 pos, Tile[][] board) {
        int diameter = (range * 2) + 1;
        List<Tile> neighbours = new ArrayList<>(diameter * diameter - 1);
        IntRectangle rec = new IntRectangle(0, 0, board.length - 1, board[0].length - 1);
        for (int i = 0; i < diameter; i++) {
            for (int j = 0; j < diameter; j++) {
                if (rec.contains(pos.x - range + i, pos.y - range + j)) {
                    neighbours.add(board[pos.x - range + i][pos.y - range + j]);
                }
            }
        }
        return neighbours;
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
