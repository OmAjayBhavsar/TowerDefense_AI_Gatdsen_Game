package com.gatdsen.simulation;

import com.gatdsen.simulation.gamemode.ExamAdmissionMode;
import com.gatdsen.simulation.gamemode.NormalMode;
import com.gatdsen.simulation.gamemode.TournamentMode;
import com.gatdsen.simulation.gamemode.campaign.*;
import com.gatdsen.simulation.Tower.TowerType;
import com.gatdsen.simulation.Enemy.EnemyType;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakte Klasse für die verschiedenen Spielmodi, welche die Standardwerte für die Spielmodi enthält.
 */
public abstract class GameMode {
    @SuppressWarnings("unchecked")
    private static final Class<? extends GameMode>[][] CampaignModes = new Class[][] {
            {CampaignMode1_1.class, CampaignMode1_2.class},
            {CampaignMode2_1.class, CampaignMode2_2.class},
            {CampaignMode3_1.class, CampaignMode3_2.class},
            {CampaignMode4_1.class, CampaignMode4_2.class},
            {CampaignMode5_1.class, CampaignMode5_2.class},
            {CampaignMode6_1.class, CampaignMode6_2.class}
    };

    @SuppressWarnings("unchecked")
    private static final Class<? extends GameMode>[] GameModes = new Class[] {
            NormalMode.class, ExamAdmissionMode.class, TournamentMode.class
    };

    protected int playerHealth;
    protected int enemyBotHealth;
    protected int playerMoney;
    protected int enemyBotMoney;
    protected String enemyBot;
    protected String map;
    protected int wave;
    protected List<TowerType> towers;
    protected List<EnemyType> enemies;

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
        towers = new ArrayList<>();
        towers.add(TowerType.BASIC_TOWER);
        towers.add(TowerType.AOE_TOWER);
        towers.add(TowerType.SNIPER_TOWER);
        enemies = new ArrayList<>();
        enemies.add(EnemyType.EMP_ENEMY);
        enemies.add(EnemyType.SHIELD_ENEMY);
        enemies.add(EnemyType.ARMOR_ENEMY);
    }

    public static GameMode getGameMode(int modeID) {
        if (modeID >= 0 && modeID < GameModes.length) {
            try {
                return GameModes[modeID].getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                return null;
            }
        } else {
            try {
                return CampaignModes[modeID / 10 - 1][modeID % 10 - 1].getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException |
                     InstantiationException | IllegalAccessException e) {
                return null;
            }
        }
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

    public void setMap(String map) {
        this.map = map;
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
    public List<TowerType> getTowers() {
        return towers;
    }

    /**
     * Eine Liste, welche Listen für die verschiedenen Gegnerwellen enthält.
     *
     * @return die Liste der zu spawnenden Gegner
     */
    public List<EnemyType> getEnemies() {
        return enemies;
    }
}
