package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode1_2 extends CampaignMode {

    public CampaignMode1_2() {
        super();
        enemyBotHealth = 100;
        map = "Campaign1_2";
        towers.remove(2);
        towers.remove(0);
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 13, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign1_2", "Campaign 1.2",
                "c1_2", "c1.2",
                "1_2"
        };
    }
}
