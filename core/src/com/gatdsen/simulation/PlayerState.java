package com.gatdsen.simulation;

import com.gatdsen.simulation.action.*;
import com.gatdsen.simulation.enemy.ArmorEnemy;
import com.gatdsen.simulation.enemy.BasicEnemy;
import com.gatdsen.simulation.enemy.EmpEnemy;
import com.gatdsen.simulation.enemy.ShieldEnemy;
import com.gatdsen.simulation.gamemode.PlayableGameMode;
import com.gatdsen.simulation.gamemode.campaign.CampaignMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Speichert den Zustand eines Spielers.
 */
public class PlayerState implements Serializable {
    private final Tile[][] board;
    private transient PlayableGameMode gameMode;
    private int health;
    private int money;
    private int spawnCoins;
    private int enemyLevel;
    private final int index;
    private int spawnDelay;
    private boolean enemySpawn;
    private PathTile spawnTile;
    private PathTile endTile;
    private final Stack<Enemy> spawnEnemies = new Stack<>();

    private boolean disqualified;
    private boolean deactivated;

    /**
     * Erstellt einen neuen PlayerState.
     *
     * @param gameState das Spiel, zu dem der Zustand gehört
     * @param index     der Index des Spielers
     * @param health    die Lebenspunkte des Spielers
     * @param money     das Geld des Spielers
     */
    PlayerState(GameState gameState, int index, int health, int money, int spawnCoins) {
        gameMode = gameState.getGameMode();
        this.index = index;
        int width = gameState.getBoardSizeX();
        int height = gameState.getBoardSizeY();
        board = new Tile[width][height];
        this.health = health;
        this.money = money;
        this.spawnCoins = spawnCoins;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (gameState.getMap()[i][j].ordinal() >= GameState.MapTileType.PATH_RIGHT.ordinal()) {
                    board[i][j] = new PathTile(i, j);
                }
                if (gameState.getMap()[i][j].ordinal() == GameState.MapTileType.OBSTACLE.ordinal()) {
                    board[i][j] = new ObstacleTile(i, j);
                }
            }
        }

        IntRectangle mapOutline = new IntRectangle(0, 0, width, height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (gameState.getMap()[i][j].ordinal() >= GameState.MapTileType.PATH_RIGHT.ordinal()) {
                    IntVector2 destination = new IntVector2(i, j);
                    switch (gameState.getMap()[i][j]) {
                        case PATH_RIGHT:
                            destination.add(1, 0);
                            break;
                        case PATH_DOWN:
                            destination.add(0, -1);
                            break;
                        case PATH_LEFT:
                            destination.add(-1, 0);
                            break;
                        case PATH_UP:
                            destination.add(0, 1);
                            break;
                    }
                    PathTile current = (PathTile) board[i][j];
                    PathTile next = null;
                    if (mapOutline.contains(destination.toFloat())) {
                        next = (PathTile) board[destination.x][destination.y];
                    }
                    if (next == null) {
                        endTile = current;
                    } else {
                        current.setNext(next);
                    }
                }
            }
        }

        if (endTile == null) {
            throw new RuntimeException("There is no path");
        }
        spawnTile = endTile.getFirstPathTile();
        spawnTile.indexPathTiles();
    }

    /**
     * Kopierkonstruktor
     *
     * @param original  der zu kopierende PlayerState
     * @param gameState der neue GameState
     */
    private PlayerState(PlayerState original, GameState gameState) {
        this.index = original.index;
        int boardX = gameState.getBoardSizeX();
        int boardY = gameState.getBoardSizeY();

        board = new Tile[boardX][boardY];
        for (int i = 0; i < boardX; i++) {
            for (int j = 0; j < boardY; j++) {
                if (original.board[i][j] != null) {
                    board[i][j] = original.board[i][j].copy();
                }
            }
        }

        for (int i = 0; i < boardX; i++) {
            for (int j = 0; j < boardY; j++) {
                if (board[i][j] instanceof PathTile) {
                    PathTile actual = (PathTile) board[i][j];
                    PathTile originalPT = (PathTile) original.board[i][j];
                    PathTile next = null;
                    if (originalPT.getNext() != null) {
                        IntVector2 nextPos = originalPT.getNext().getPosition();
                        next = (PathTile) board[nextPos.x][nextPos.y];
                    }
                    actual.setNext(next);
                }
            }
        }
        health = original.health;
        money = original.money;
        deactivated = original.deactivated;
        disqualified = original.disqualified;
    }

    /**
     * Erstellt eine Kopie des PlayerStates.
     *
     * @param newGameState GameState
     * @return eine Kopie des PlayerStates
     */
    PlayerState copy(GameState newGameState) {
        return new PlayerState(this, newGameState);
    }

    /**
     * Deaktiviert den PlayerState
     *
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action deactivate(Action head) {
        deactivated = true;
        Action action = new PlayerDeactivateAction(0, index, disqualified);
        head.addChild(action);
        return action;
    }

    /**
     * Gibt zurück, ob der Spieler deaktiviert ist
     *
     * @return true, wenn der Spieler deaktiviert ist
     */
    public boolean isDeactivated() {
        return deactivated;
    }

    /**
     * Gibt zurück, ob der Spieler disqualifiziert ist
     *
     * @return true, wenn der Spieler disqualifiziert ist
     */
    public boolean isDisqualified() {
        return disqualified;
    }

    /**
     * Disqualifiziert den Spieler und deaktiviert seinen PlayerState
     */
    void disqualify() {
        disqualified = true;
        deactivated = true;
    }

    /**
     * Gibt das Spielfeld des Spielers zurück
     *
     * @return Spielfeld
     */
    public Tile[][] getBoard() {
        return board;
    }

    /**
     * Gibt das Ende des Pfades zurück
     *
     * @return Ende des Pfades
     */
    public PathTile getCheese() {
        return endTile;
    }

    /**
     * Gibt den Index des Spielers zurück
     *
     * @return Spielerindex
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gibt die aktuelle Lebenspunkte des Spielers zurück
     *
     * @return Lebenspunkte
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gibt das aktuelle Geld des Spielers zurück
     *
     * @return Geld
     */
    public int getMoney() {
        return money;
    }

    /**
     * Gibt die aktuellen SpawnCoins des Spielers zurück
     *
     * @return SpawnCoins
     */
    public int getSpawnCoins() {
        return spawnCoins;
    }

    /**
     * Platziert einen Tower auf dem Spielfeld
     *
     * @param x    x-Koordinate des Towers
     * @param y    y-Koordinate des Towers
     * @param type Typ des Towers
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action placeTower(int x, int y, Tower.TowerType type, Action head) {
        if (!gameMode.getTowers().contains(type)) {
            head.addChild(new ErrorAction(
                    "Für diesen Spielmodus darfst du nur folgenden Türme benutzen: " + gameMode.getTowers()
            ));
            return head;
        }

        if (x >= board.length || y >= board[0].length || x < 0 || y < 0) {
            head.addChild(new ErrorAction("Position (" + x + ", " + y + ") is out of bounds"));
        } else if (board[x][y] != null) {
            head.addChild(new ErrorAction("(" + x + ", " + y + ") is already occupied"));
        } else if (money < Tower.getTowerPrice(type)) {
            head.addChild(new ErrorAction("Not enough money to place Tower at (" + x + ", " + y + ")"));
        } else if (board[x][y] != null && !board[x][y].isBuildable()) {
            head.addChild(new ErrorAction("Tile at (" + x + ", " + y + ") is not buildable"));
        } else {
            TowerTile towerTile = new TowerTile(this, x, y, type);

            money -= Tower.getTowerPrice(type);
            Action updateAction = new UpdateCurrencyAction(0, money, spawnCoins, index);
            head.addChild(updateAction);

            board[x][y] = towerTile;
            IntVector2 pos = new IntVector2(x, y);
            Action action = new TowerPlaceAction(0, pos, type.ordinal(), index, towerTile.getTower().getId());
            head.addChild(action);
        }
        return head;
    }

    /**
     * Upgraded einen Tower auf dem Spielfeld
     *
     * @param x    x-Koordinate des Towers
     * @param y    y-Koordinate des Towers
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action upgradeTower(int x, int y, Action head) {
        if (board[x][y] == null) {
            head.addChild(new ErrorAction("No Tower at position (" + x + ", " + y + ")"));
        } else if (board[x][y] instanceof TowerTile) {
            TowerTile towerTile = (TowerTile) board[x][y];
            Tower tower = towerTile.getTower();
            if (tower.getLevel() < Tower.getMaxLevel() && money >= tower.getUpgradePrice()) {
                money -= tower.getUpgradePrice();
                tower.upgrade();
                head.addChild(new TowerUpgradeAction(0, towerTile.getPosition(), tower.getType().ordinal(), index, tower.getId()));
                head.addChild(new UpdateCurrencyAction(0, money, spawnCoins, index));
            } else {
                if (tower.getLevel() >= Tower.getMaxLevel()) {
                    head.addChild(new ErrorAction("Tower (" + x + ", " + y + ") is already at max level"));
                } else {
                    head.addChild(new ErrorAction("Not enough money to upgrade Tower (" + x + ", " + y + ")"));
                }
            }
        } else {
            head.addChild(new ErrorAction("No Tower at position (" + x + ", " + y + ")"));
        }
        return head;
    }

    /**
     * Verkauft einen Tower auf dem Spielfeld
     *
     * @param x    x-Koordinate des Towers
     * @param y    y-Koordinate des Towers
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action sellTower(int x, int y, Action head) {
        if (board[x][y] == null) {
            head.addChild(new ErrorAction("No Tower at position (" + x + ", " + y + ")"));
        } else if (board[x][y] instanceof TowerTile) {
            TowerTile towerTile = (TowerTile) board[x][y];
            Tower tower = towerTile.getTower();
            money += Tower.getTowerPrice(tower.type) / 2;
            head.addChild(new UpdateCurrencyAction(0, money, spawnCoins, index));
            head.addChild(new TowerDestroyAction(0, towerTile.getPosition(), tower.getType().ordinal(), index, tower.getId()));
            board[x][y] = null;
        } else {
            head.addChild(new ErrorAction("No Tower at position (" + x + ", " + y + ")"));
        }
        return head;
    }

    /**
     * Setzt das Target eines Towers
     *
     * @param x            x-Koordinate des Towers
     * @param y            y-Koordinate des Towers
     * @param targetOption TargetOption des Towers
     * @param head         Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action setTarget(int x, int y, Tower.TargetOption targetOption, Action head) {
        if (board[x][y] instanceof TowerTile) {
            TowerTile towerTile = (TowerTile) board[x][y];
            towerTile.getTower().setTargetOption(targetOption);
        } else {
            head.addChild(new ErrorAction("No Tower at position (" + x + ", " + y + ")"));
        }
        return head;
    }

    /**
     * Sendet einen Gegner zum Gegenspieler
     *
     * @param type      Typ des Gegners
     * @param gameState GameState
     * @param head      Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action sendEnemy(Enemy.EnemyType type, GameState gameState, Action head) {
        if (!gameMode.getEnemies().contains(type)) {
            head.addChild(new ErrorAction(
                    "Für diesen Spielmodus darfst du nur folgende Gegner verwenden: " + gameMode.getEnemies()
            ));
            return head;
        }
        enemyLevel = enemyLevel == 0 ? 1 : enemyLevel;
        if (spawnCoins >= Enemy.getEnemyTypePrice(type, enemyLevel)) {
            PlayerState playerState = gameState.getPlayerStates()[(index + 1) % 2];
            playerState.spawnEnemy(type);
            spawnCoins -= Enemy.getEnemyTypePrice(type, enemyLevel);
            head.addChild(new UpdateCurrencyAction(0, money, spawnCoins, index));
        }
        return head;
    }

    /**
     * Initialisiert die Gegner, die gespawnt werden sollen
     *
     * @param type Typ des Gegners
     */
    void spawnEnemy(Enemy.EnemyType type) {
        enemySpawn = true;
        spawnDelay++;
        switch (type) {
            case EMP_ENEMY:
                spawnEnemies.push(new EmpEnemy(this, enemyLevel, spawnTile));
                break;
            case SHIELD_ENEMY:
                spawnEnemies.push(new ShieldEnemy(this, enemyLevel, spawnTile));
                break;
            case ARMOR_ENEMY:
                spawnEnemies.push(new ArmorEnemy(this, enemyLevel, spawnTile));
        }
    }

    /**
     * Fügt den Gegner des aktuellen Zuges zur Spawnliste hinzu, falls kein Gegner vom Gegenspieler gespawned wurde
     *
     * @param wave Die aktuelle Welle
     */
    void spawnEnemy(int wave) {
        if (enemySpawn) {
            enemySpawn = false;
        } else {
            int actWave = wave - spawnDelay;
            spawnEnemies.push(new BasicEnemy(this, gameMode.calculateEnemyLevelForWave(actWave), spawnTile));
            enemyLevel = 1 + actWave / 20;
        }
    }

    /**
     * Spawnt die Gegner
     *
     * @param head Die vorherige Action
     * @param wave Die aktuelle Welle
     * @return der Action Head
     */
    Action spawnEnemies(Action head, int wave) {
        spawnEnemy(wave + 1);
        while (!spawnEnemies.isEmpty()) {
            Enemy enemy = spawnEnemies.pop();
            spawnTile.getEnemies().add(enemy);
            head.addChild(new EnemySpawnAction(0, spawnTile.getPosition(), enemy.getLevel(), enemy.getHealth(), index, enemy.enemyType, enemy.getId()));
        }
        return head;
    }

    /**
     * Bewegt die Gegner
     *
     * @param head Die vorherige Action
     * @return der neue Action Head
     */
    Action moveEnemies(Action head) {

        PathTile actual = endTile;
        while (actual.getPrev() != null) {
            List<Enemy> enemiesCopy = new ArrayList<>(actual.getEnemies());
            for (Enemy enemy : enemiesCopy) {
                head = enemy.move(head);
            }
            actual = actual.getPrev();
        }

        List<Enemy> lastEnemiesCopy = new ArrayList<>(actual.getEnemies());
        for (Enemy enemy : lastEnemiesCopy) {
            head = enemy.move(head);
        }
        return head;
    }

    /**
     * Setzt die Lebenspunkte des Spielers
     *
     * @param damage Schaden, der dem Spieler zugefügt wird (negativ für Heilung)
     * @param head   Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action setHealth(int damage, Action head) {
        health -= damage;
        Action updateHealthAction = new UpdateHealthAction(0, health, index);
        head.addChild(updateHealthAction);
        head = updateHealthAction;
        if (health <= 0) head = deactivate(head);
        return head;
    }

    /**
     * Setzt das Geld des Spielers
     *
     * @param money Geld, das dem Spieler zugefügt wird (negativ für Abzug)
     * @param head  Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action updateMoney(int money, Action head) {
        if (money == 0) {
            return head;
        }
        this.money += money;
        Action updateMoneyAction = new UpdateCurrencyAction(0, this.money, this.spawnCoins, index);
        head.addChild(updateMoneyAction);
        head = updateMoneyAction;
        return head;
    }

    Action updateSpawnCoins(int spawnCoins, Action head) {
        if (spawnCoins == 0) {
            return head;
        }
        this.spawnCoins += spawnCoins;
        Action updateSpawnCoinsAction = new UpdateCurrencyAction(0, money, this.spawnCoins, index);
        head.addChild(updateSpawnCoinsAction);
        head = updateSpawnCoinsAction;
        return head;
    }

    /**
     * Führt alle Tower-Aktionen aus
     *
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action tickTowers(Action head) {
        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                if (tile instanceof TowerTile) {
                    TowerTile towerTile = (TowerTile) tile;
                    head = towerTile.getTower().tick(head);
                }
            }
        }
        return head;
    }
}
