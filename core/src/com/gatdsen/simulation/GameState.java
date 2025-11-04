package com.gatdsen.simulation;

import com.gatdsen.manager.map.MapRetriever;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.gamemode.PlayableGameMode;

import java.io.Serializable;
import java.util.*;

/**
 * Repräsentiert ein laufendes Spiel mit allen dazugehörigen Daten
 */
public class GameState implements Serializable {

    public Tower getTower(int x, int y) {
        if (isValidCoordinates(x, y)) {
            return towers[x][y];
        }
        return null;
    }

    public void placeTower(int x, int y, Tower.TowerType type) {
        if (isValidCoordinates(x, y)) {
            towers[x][y] = new Tower(type) {
                @Override
                protected Tower copy(PlayerState NewPlayerstate) {
                    return null;
                }

                @Override
                public int getDamage() {
                    return 0;
                }

                @Override
                public int getRange() {
                    return 0;
                }

                @Override
                public int getRechargeTime() {
                    return 0;
                }

                @Override
                public void incrementCooldown() {

                }

                @Override
                protected Action attack(Action head) {
                    return null;
                }

                @Override
                public int getUpgradePrice() {
                    return 0;
                }
            };
        }
    }

    public void upgradeTower(int x, int y) {
        if (isValidCoordinates(x, y) && towers[x][y] != null) {
            towers[x][y].upgrade();
        }
    }

    public void sellTower(int x, int y) {
        if (isValidCoordinates(x, y)) {
            towers[x][y] = null;
        }
    }

    private boolean isValidCoordinates(int x, int y) {
        return x >= 0 && x < boardSizeX && y >= 0 && y < boardSizeY;
    }


    /**
     * Enum für die verschiedenen Feldtypen
     */
    public enum MapTileType {
        LAND,
        OBSTACLE,
        PATH_RIGHT,
        PATH_DOWN,
        PATH_LEFT,
        PATH_UP
    }

    private final PlayerState[] playerStates;
    private final MapTileType[][] map;
    private final transient PlayableGameMode gameMode;
    private final int playerCount;
    private final transient Simulation sim;
    private int turn;
    private boolean active;

    private final int boardSizeX;

    private final int boardSizeY;

    private final Tower[][] towers;

    /**
     * Erstellt ein neues GameState-Objekt mit den angegebenen Attributen.
     *
     * @param gameMode    Spielmodus
     * @param mapName     Name der Map
     * @param playerCount Anzahl der Spieler
     * @param sim         Simulation Instanz
     */
    GameState(PlayableGameMode gameMode, String mapName, int playerCount, Simulation sim) {
        this.gameMode = gameMode;
        this.map = MapRetriever.getInstance().getMapFromGameModeType(mapName, gameMode.getType()).getTileTypes();
        this.playerCount = playerCount;
        this.active = true;
        this.sim = sim;
        playerStates = new PlayerState[playerCount];
        Arrays.setAll(
                playerStates,
                index -> new PlayerState(
                        this,
                        index,
                        gameMode.getPlayerHealth(index),
                        gameMode.getPlayerMoney(index),
                        gameMode.getPlayerSpawnCoins(index)
                )
        );
        this.turn = gameMode.getStartTurn();
        this.boardSizeX = map.length; // Initialize boardSizeX based on map size
        this.boardSizeY = map[0].length; // Initialize boardSizeY based on map size
        this.towers = new Tower[boardSizeX][boardSizeY]; // Initialize towers array based on board size
        /*
        if (gameMode == GameMode.Christmas_Task) {
            playerStates[1] = new PlayerState(this, 1, 500, 0);
        }
        */
    }

    /**
     * Copy constructor erstellt eine Kopie des übergebenen GameState-Objekts.
     *
     * @param original Das zu kopierende GameState-Objekt
     */
    private GameState(GameState original) {
        gameMode = original.gameMode;
        turn = original.turn;
        map = Arrays.copyOf(original.map, original.map.length);
        playerStates = new PlayerState[original.playerStates.length];
        for (int i = 0; i < playerStates.length; i++) {
            playerStates[i] = original.playerStates[i].copy(this);
        }
        playerCount = original.playerCount;
        active = original.active;
        sim = null;

        // Copy board size and towers array
        this.boardSizeX = original.boardSizeX;
        this.boardSizeY = original.boardSizeY;
        this.towers = new Tower[boardSizeX][boardSizeY];
        for (int i = 0; i < boardSizeX; i++) {
            for (int j = 0; j < boardSizeY; j++) {
                this.towers[i][j] = original.towers[i][j];
            }
        }
    }

    /**
     * Erstellt eine Kopie des GameState-Objekts.
     *
     * @return Kopie des GameState-Objekts
     */
    public GameState copy() {
        return new GameState(this);
    }

    /**
     * Gibt den Spiel-Modus des laufenden Spiels zurück.
     *
     * @return Spiel-Modus als int
     */
    public PlayableGameMode getGameMode() {
        return gameMode;
    }

    /**
     * @return die Simulation Instanz
     */
    protected Simulation getSim() {
        return sim;
    }

    /**
     * Gibt die PlayerStates beider Spieler zurück
     *
     * @return PlayerStates
     */
    public PlayerState[] getPlayerStates() {
        return playerStates;
    }

    /**
     * Gibt die PlayerState des angegebenen Spielers zurück
     *
     * @param player Index des Spielers
     * @return PlayerState des Spielers
     */
    public Tile[][] getPlayerBoard(int player) {
        return playerStates[player].getBoard();
    }

    /**
     * Gibt die Map zurück
     *
     * @return Map
     */
    public MapTileType[][] getMap() {
        return map;
    }

    /**
     * @return Horizontale Größe des Spielfeldes in #Boxen
     */
    public int getBoardSizeX() {
        return map.length;
    }

    /**
     * @return Vertikale Größe des Spielfeldes in #Boxen
     */
    public int getBoardSizeY() {
        return map[0].length;
    }

    /**
     * @return Anzahl der Spieler
     */
    public int getPlayerCount() {
        return playerCount;
    }

    /**
     * @return Lebenspunkte aller Spieler
     */
    public float[] getHealth() {
        float[] healths = new float[playerStates.length];
        for (int i = 0; i < playerStates.length; i++) {
            healths[i] = playerStates[i].getHealth();
        }
        return healths;
    }

    /**
     * @return Aktueller Turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Erhöht den Turn-Zähler um 1
     */
    void nextTurn() {
        ++turn;
    }

    /**
     * @return True, wenn das Spiel aktiv ist
     */
    public boolean isActive() {
        //ToDo migrate to Simulation
        return active;
    }

    /**
     * Deaktiviert das Spiel.
     */
    protected void deactivate() {
        //ToDo migrate to Simulation
        this.active = false;
    }
}
