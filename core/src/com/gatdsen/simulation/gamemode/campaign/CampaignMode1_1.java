package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.GameMode;
import com.gatdsen.simulation.Tower;

import java.util.ArrayList;

public class CampaignMode1_1 extends GameMode {
    public CampaignMode1_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign1_1";
        towers.remove(2);
        towers.remove(1);
    }
}
