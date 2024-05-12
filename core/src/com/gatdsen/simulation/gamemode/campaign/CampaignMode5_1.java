package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode5_1 extends CampaignMode {

    public CampaignMode5_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign5_1";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 10, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign5_1", "Campaign 5.1", "Campaign 5",
                "c5_1", "c5.1", "c5",
                "5_1"
        };
    }
}
