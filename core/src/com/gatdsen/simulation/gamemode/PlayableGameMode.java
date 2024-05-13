package com.gatdsen.simulation.gamemode;

import com.gatdsen.manager.player.handler.LocalPlayerHandlerFactory;
import com.gatdsen.manager.player.handler.PlayerHandlerFactory;
import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tower;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PlayableGameMode extends GameMode {

    protected int startTurn = 0;
    protected List<Tower.TowerType> towers = new ArrayList<>(List.of(Tower.TowerType.MINIGUN_CAT, Tower.TowerType.CATANA_CAT, Tower.TowerType.MAGE_CAT));
    protected List<Enemy.EnemyType> enemies = new ArrayList<>(List.of(Enemy.EnemyType.EMP_ENEMY, Enemy.EnemyType.SHIELD_ENEMY, Enemy.EnemyType.ARMOR_ENEMY));

    protected List<String> maps = new ArrayList<>();

    private int playerCount = 2;
    private PlayerHandlerFactory[] playerFactories = new PlayerHandlerFactory[playerCount];
    private Integer[] playerHealth = new Integer[playerCount];
    private Integer[] playerMoney = new Integer[playerCount];
    private Integer[] playerSpawnCoins = new Integer[playerCount];

    protected final void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
        playerFactories = resizeArray(playerFactories, new PlayerHandlerFactory[playerCount]);
        playerHealth = resizeArray(playerHealth, new Integer[playerCount]);
        playerMoney = resizeArray(playerMoney, new Integer[playerCount]);
        playerSpawnCoins = resizeArray(playerSpawnCoins, new Integer[playerCount]);
    }

    private static <T> T[] resizeArray(T[] original, T[] resized) {
        System.arraycopy(original, 0, resized, 0, Math.min(resized.length, original.length));
        return resized;
    }

    /**
     * Erstellt ein neues GameMode-Objekt mit den Standardwerten
     */
    protected PlayableGameMode() {
        Arrays.fill(playerHealth, 300);
        Arrays.fill(playerMoney, 100);
        Arrays.fill(playerSpawnCoins, 0);
        playerFactories[1] = LocalPlayerHandlerFactory.IDLE_BOT;
    }

    /**
     * @return die Startleben des Spielers
     */
    public int getPlayerHealth(int playerIndex) {
        return playerHealth[playerIndex];
    }

    protected void setPlayerHealth(int playerIndex, int health) {
        playerHealth[playerIndex] = health;
    }

    /**
     * @return das Startgeld des Spielers
     */
    public int getPlayerMoney(int playerIndex) {
        return playerMoney[playerIndex];
    }

    protected void setPlayerMoney(int playerIndex, int money) {
        playerMoney[playerIndex] = money;
    }

    public int getPlayerSpawnCoins(int playerIndex) {
        return playerSpawnCoins[playerIndex];
    }

    protected void setPlayerSpawnCoins(int playerIndex, int spawnCoins) {
        playerSpawnCoins[playerIndex] = spawnCoins;
    }

    /**
     * @return der Name der Map
     */
    public List<String> getMaps() {
        return maps;
    }

    public void addMap(String map) {
        maps.add(map);
    }

    public void setMap(String... maps) {
        this.maps.clear();
        this.maps.addAll(Arrays.asList(maps));
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

    public void setPlayerFactory(int index, PlayerHandlerFactory factory) {
        playerFactories[index] = factory;
    }

    public void resetPlayerFactories() {
        Arrays.fill(playerFactories, null);
    }

    public int calculateSpawnCoinsForRound(PlayerState playerState) {
        return 1;
    }

    @Override
    public void parseFromCommandArguments(CommandLine params) {
        if (params.hasOption("m")) {
            setMap(params.getOptionValue("m").trim().split("\\s+"));
        }
        if (params.hasOption("p")) {
            for (PlayerHandlerFactory p : PlayerHandlerFactory.getPlayerFactories(params.getOptionValue("p").trim().split("\\s+"))) {
                addPlayerFactory(p);
            }
        }
    }

    @Override
    public boolean validate(StringBuilder errorMessages) {
        boolean valid = true;
        if (maps.isEmpty()) {
            errorMessages.append("No map name was provided.\n");
            valid = false;
        }
        int emptyPlayerFactories = 0;
        for (PlayerHandlerFactory factory : playerFactories) {
            if (factory == null) {
                emptyPlayerFactories++;
            }
        }
        if (emptyPlayerFactories > 0) {
            errorMessages
                    .append("There were only given ")
                    .append(playerFactories.length - emptyPlayerFactories)
                    .append(" players, but ")
                    .append(playerFactories.length)
                    .append(" are required.\n");
            valid = false;
        }
        return valid;
    }
}
