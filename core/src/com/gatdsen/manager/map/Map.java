package com.gatdsen.manager.map;

import com.gatdsen.simulation.GameState;

/**
 * Diese Klasse repräsentiert eine Karte.
 */
public final class Map {

	private final String name;
    private final boolean isHidden;
    private final GameState.MapTileType[][] tileTypes;

    /**
     * @param name Der Name der Karte
     * @param isHidden Ob die Karte direkt über das UI ausgewählt werden kann oder versteckt bleiben soll
     * @param tileTypes Die Tile-Typen der Karte
     */
	public Map(String name, boolean isHidden, GameState.MapTileType[][] tileTypes) {
		this.name = name;
        this.isHidden = isHidden;
        this.tileTypes = tileTypes;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return getName();
	}

    public boolean isHidden() {
        return isHidden;
    }

    public GameState.MapTileType[][] getTileTypes() {
        return tileTypes;
    }
}
