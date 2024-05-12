package com.gatdsen.simulation.gamemode;

import com.gatdsen.manager.player.handler.LocalPlayerHandlerFactory;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.Tower;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PlayableGameMode extends GameMode {

    protected int playerHealth;
    protected int enemyBotHealth;
    protected int playerMoney;
    protected int enemyBotMoney;
    protected String[] maps = {"map1"};
    protected int startTurn = 0;
    protected List<Tower.TowerType> towers = new ArrayList<>(List.of(Tower.TowerType.BASIC_TOWER, Tower.TowerType.AOE_TOWER, Tower.TowerType.SNIPER_TOWER));
    protected List<Enemy.EnemyType> enemies = new ArrayList<>(List.of(Enemy.EnemyType.EMP_ENEMY, Enemy.EnemyType.SHIELD_ENEMY, Enemy.EnemyType.ARMOR_ENEMY));

    private int playerCount = 2;
    private PlayerHandlerFactory[] playerFactories = new PlayerHandlerFactory[playerCount];

    protected final void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
        PlayerHandlerFactory[] newPlayerFactories = new PlayerHandlerFactory[playerCount];
        System.arraycopy(playerFactories, 0, newPlayerFactories, 0, Math.min(newPlayerFactories.length, playerFactories.length));
        playerFactories = newPlayerFactories;
    }

    /**
     * Erstellt ein neues GameMode-Objekt mit den Standardwerten
     */
    protected PlayableGameMode() {
        playerHealth = 300;
        enemyBotHealth = 300;
        playerMoney = 100;
        enemyBotMoney = 100;
        playerFactories[1] = LocalPlayerHandlerFactory.IDLE_BOT;
    }

    // ToDo: Implement validate method
    /**
     * Überprüft, ob die GameMode-Konfiguration gültig ist.
     *
     * @return true, wenn die Konfiguration gültig ist, ansonsten false
     */
    // public abstract boolean validate();

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
     * @return der Name der Map
     */
    public String[] getMaps() {
        return maps;
    }

    public void setMap(String map) {
        this.map = map;
    }

    /**
     * @return die Startwelle
     */
    public int getStartTurn() {
        return startTurn;
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
    public List<Enemy.EnemyType> getEnemies() {
        return enemies;
    }

    public PlayerHandlerFactory[] getPlayerFactories() {
        return playerFactories;
    }

    public void addPlayerFactory(PlayerHandlerFactory factory) {
        for (int i = 0; i < playerFactories.length; i++) {
            if (playerFactories[i] == null) {
                playerFactories[i] = factory;
                return;
            }
        }
    }

    public void resetPlayerFactories() {
        Arrays.fill(playerFactories, null);
    }

    @Override
    public void parseFromCommandArguments(CommandLine params) {
        if (params.hasOption("m")) {
            setMap(params.getOptionValue("m"));
        }
        if (params.hasOption("p")) {
            for (PlayerHandlerFactory p : PlayerHandlerFactory.getPlayerFactories(params.getOptionValue("p").trim().split("\\s+"))) {

            }
        }
    }
}
