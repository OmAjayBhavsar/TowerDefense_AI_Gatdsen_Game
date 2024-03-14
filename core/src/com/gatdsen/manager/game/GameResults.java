package com.gatdsen.manager.game;

import com.gatdsen.manager.player.data.PlayerInformation;
import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.action.ActionLog;

import java.io.Serializable;
import java.util.ArrayList;

public final class GameResults implements Serializable {

    private final transient GameConfig config;

    private final ArrayList<ActionLog> actionLogs = new ArrayList<>();
    private GameState initialState;
    private Game.Status status;

    private PlayerInformation[] playerInformation;
    private float[] scores;

    private String[][] skins;

    GameResults(GameConfig config) {
        this.config = config;
    }

    public GameConfig getConfig() {
        return config;
    }

    public ArrayList<ActionLog> getActionLogs() {
        return actionLogs;
    }

    void addActionLog(ActionLog log){
        actionLogs.add(log);
    }

    public GameState getInitialState() {
        return initialState;
    }

    void setInitialState(GameState initialState) {
        this.initialState = initialState.copy();
    }

    public Game.Status getStatus() {
        return status;
    }

    void setStatus(Game.Status status) {
        this.status = status;
    }

    public PlayerInformation[] getPlayerInformation() {
        return playerInformation;
    }

    void setPlayerInformation(PlayerInformation[] playerInformation) {
        this.playerInformation = playerInformation;
    }

    public float[] getScores() {
        return scores;
    }

    void setScores(float[] scores) {
        this.scores = scores;
    }

    public String[][] getSkins() {
        return skins;
    }

    void setSkins(String[][] skins) {
        this.skins = skins;
    }
}
