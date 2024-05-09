package com.gatdsen.simulation.gamemode.campaign;

public class CampaignMode1_1 extends CampaignMode {
    public CampaignMode1_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign1_1";
        towers.remove(2);
        towers.remove(1);
    }
}
