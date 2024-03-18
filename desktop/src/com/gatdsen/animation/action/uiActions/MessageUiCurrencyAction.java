package com.gatdsen.animation.action.uiActions;

import com.gatdsen.ui.hud.UiMessenger;

public class MessageUiCurrencyAction extends MessageUiAction {
    private final int team;
    private final int balance;
    private final int spawnCoins;

    public MessageUiCurrencyAction(float start, UiMessenger uiMessenger, int team, int balance, int spawnCoins) {
        super(start, uiMessenger);
        this.team = team;
        this.balance = balance;
        this.spawnCoins = spawnCoins;
    }

    @Override
    protected void runAction(float oldTime, float current) {
        uiMessenger.setBankBalance(team, balance);
        uiMessenger.setSpawnCoins(team, spawnCoins);
        endAction(oldTime);
    }
}
