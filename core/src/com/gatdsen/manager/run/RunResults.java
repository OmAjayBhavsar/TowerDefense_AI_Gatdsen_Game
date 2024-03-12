package com.gatdsen.manager.run;

import com.gatdsen.manager.player.data.PlayerInformation;

public final class RunResults {

    private PlayerInformation[] playerInformation;
    private float[] scores;

    public PlayerInformation[] getPlayerInformation() {
        return playerInformation;
    }

    void setPlayerInformation(PlayerInformation[] playerInformation) {
        this.playerInformation = playerInformation;
    }

    public void setPlayerInformation(int index, PlayerInformation playerInformation) {
        this.playerInformation[index] = playerInformation;
    }

    public float[] getScores() {
        return scores;
    }

    float getScore(int index) {
        return scores[index];
    }

    void setScores(float[] scores) {
        this.scores = scores;
    }

    void setScore(int index, float score) {
        scores[index] = score;
    }
}
