package com.gatdsen.manager;

import com.gatdsen.simulation.ObstacleTile;
import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tile;
import com.gatdsen.simulation.PathTile;
import com.gatdsen.simulation.Tower;
import com.gatdsen.simulation.TowerTile;

/**
 * Diese Klasse repräsentiert den aktuellen Zustand eines Spielers.
 * Dies umfasst sowohl Attribute, wie dessen Leben und Geld, als auch dessen Map.
 * <p>
 * Wie es der Name bereits andeutet, ist diese Klasse statisch. Das bedeutet, dass sie sich nicht bspw. über die
 * Dauer eines Zuges verändert.
 */
public final class StaticPlayerState {

    private final PlayerState state;

    StaticPlayerState(PlayerState state) {
        this.state = state;
    }

    /**
     * @return Die Id des Spielers, welche verwendet wird, um den Spieler bspw. aus einer Liste
     * von {@link StaticPlayerState}s zu identifizieren.
     */
    public int getId() {
        return state.getIndex();
    }

    /**
     * @return Die Anzahl der verbleibenden Leben des Spielers
     */
    public int getHealth() {
        return state.getHealth();
    }

    /**
     * @return Die Anzahl des angesparten Geldes des Spielers
     */
    public int getMoney() {
        return state.getMoney();
    }

    /**
     * @return Die Anzahl der angesparten SpawnCoins des Spielers
     */
    public int getSpawnCoins() {
        return state.getSpawnCoins();
    }

    /**
     * Gibt einen zwei-dimensionalen Array von {@link Tile}s zurück, indexiert anhand ihrer x- und y-Koordinaten.
     * <p> - {@link PathTile} Instanzen symbolisieren dabei einen Pfad, auf welchem sich die Gegner bewegen.
     * <p> - {@link TowerTile} Instanzen symbolisieren einen Tower, welcher auf dem Spielfeld von dir platziert wurde.
     * <p> - {@link ObstacleTile} Instanzen symbolisieren ein Hindernis, an dem keine Türme platziert werden können.
     * <p> - {@code null} symbolisiert ein leeres Feld, an welchem neue Türme platziert werden können.
     * <p>
     * Das Array ist über die x- und y-Koordinate des {@link Tile}s indexiert.
     * {@code map[x][y]} gibt also das {@link Tile} an der Position {@code (x, y)} zurück.
     * @return Das Spielfeld des Spielers
     */
    public Tile[][] getBoard() {
        return state.getBoard();
    }

    /**
     * Gibt das letzte {@link PathTile} des Spielfeldes zurück. <br>
     * Befindet sich ein Gegner auf diesem Feld, so erreicht dieser in der nachfolgenden Runde das Ziel und fügt dem
     * Spieler Schaden zu.
     * @return Das letzte {@link PathTile} des Spielfeldes
     */
    public PathTile getLastTile() {
        return state.getCheese();
    }

    /**
     * Gibt das letzte {@link PathTile} des Spielfeldes zurück. <br>
     * Befindet sich ein Gegner auf diesem Feld, so erreicht dieser in der nachfolgenden Runde das Ziel und fügt dem
     * Spieler Schaden zu.
     * @return Das letzte {@link PathTile} des Spielfeldes
     */
    public PathTile getCheeseTile() {
        return state.getCheese();
    }
}
