package com.gatdsen.simulation.gamemode.campaign;

public class CampaignMode1_2 extends CampaignMode {
    public CampaignMode1_2() {
        super();
        enemyBotHealth = 100;
        map = "Campaign1_2";
        towers.remove(2);
        towers.remove(0);
    }
}
