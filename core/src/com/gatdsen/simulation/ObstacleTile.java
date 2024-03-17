package com.gatdsen.simulation;

public class ObstacleTile extends Tile {
    /**
     * Erstellt ein Hindernis (Obstacle) an der angegebenen Position.
     * @param x x-Koordinate
     * @param y y-Koordinate
     */
    ObstacleTile(int x, int y) {
        super(x, y);
    }

    /**
     * Erstellt eine Kopie eines Hindernisses (Obstacle).
     * @param original das zu kopierende Hindernis
     */
    ObstacleTile(ObstacleTile original) {
        super(original.getPosition().x, original.getPosition().y);
    }

    /**
     * Erstellt eine Kopie des Hindernisses (Obstacle).
     * @return eine Kopie des Hindernisses (Obstacle)
     */
    @Override
    protected Tile copy() {
        return new ObstacleTile(this);
    }

    /**
     * @return False, da Hindernisse nicht bebaubar sind
     */
    @Override
    public boolean isBuildable() {
        return false;
    }
    /**
     * @return False, da Hindernisse keine Pfade sind
     */
    @Override
    public boolean isPath() {
        return false;
    }
}
