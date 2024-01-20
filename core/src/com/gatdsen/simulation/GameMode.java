package com.gatdsen.simulation;

import java.util.List;

/**
 * Abstrakte Klasse für die verschiedenen Spielmodi, welche die Standardwerte für die Spielmodi enthält.
 */
public abstract class GameMode {
    protected int playerHealth;
    protected int enemyBotHealth;
    protected int playerMoney;
    protected int enemyBotMoney;
    protected String enemyBot;
    protected String map;
    protected int wave;
    protected List<Tower.TowerType> towers;
    protected List<List<Enemy>> enemies;

    /**
     * Erstellt ein neues GameMode-Objekt mit den Standardwerten
     */
    protected GameMode() {
        playerHealth = 300;
        enemyBotHealth = 300;
        playerMoney = 100;
        enemyBotMoney = 100;
        enemyBot = "IdleBot";
        map = "map1";
        wave = 1;
        towers = null;
        enemies = null;
    }

    /**
     * @return die Startleben des Spielers
     */
    public int getPlayerHealth() {
        return playerHealth;
    }

    /**
     * @return die Startleben des Gegnerbots
     */
    public int getEnemyBotHealth() {
        return enemyBotHealth;
    }

    /**
     * @return das Startgeld des Spielers
     */
    public int getPlayerMoney() {
        return playerMoney;
    }

    /**
     * @return das Startgeld des Gegnerbots
     */
    public int getEnemyBotMoney() {
        return enemyBotMoney;
    }

    /**
     * @return der Name des Gegnerbots
     */
    public String getEnemyBot() {
        return enemyBot;
    }

    /**
     * @return der Name der Map
     */
    public String getMap() {
        return map;
    }

    /**
     * @return die Startwelle
     */
    public int getWave() {
        return wave;
    }

    /**
     * Eine Liste der Türme welche der Spieler bauen kann.
     *
     * @return die Liste der Türme
     */
    public List<Tower.TowerType> getTowers() {
        return towers;
    }

    /**
     * Eine Liste, welche Listen für die verschiedenen Gegnerwellen enthält.
     *
     * @return die Liste der zu spawnenden Gegner
     */
    public List<List<Enemy>> getEnemies() {
        return enemies;
    }
}
