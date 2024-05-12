package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode1_1 extends CampaignMode {
    public CampaignMode1_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign1_1";
        towers.remove(2);
        towers.remove(1);
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 13, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                String.valueOf(getType().ordinal()),
                "Campaign1_1", "Campaign 1.1", "Campaign 1", "Campaign",
                "c1_1", "c1.1", "c1", "c",
                "1_1"
        };
    }
}
