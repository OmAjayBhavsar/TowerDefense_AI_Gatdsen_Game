package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.Tower;

import java.util.ArrayList;

public class CampaignMode2_1 extends GameMode {
    public CampaignMode2_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign2_1";
        towers.remove(1);
        towers.remove(0);
    }
}
