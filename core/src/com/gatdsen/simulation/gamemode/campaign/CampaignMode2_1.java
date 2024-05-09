package com.gatdsen.simulation.gamemode.campaign;

public class CampaignMode2_1 extends CampaignMode {
    public CampaignMode2_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign2_1";
        towers.remove(1);
        towers.remove(0);
    }
}
