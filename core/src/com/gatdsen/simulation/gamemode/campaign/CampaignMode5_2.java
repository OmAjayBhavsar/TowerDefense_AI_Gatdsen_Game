package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode5_2 extends CampaignMode {

    public CampaignMode5_2() {
        super();
        enemyBotHealth = 100;
        map = "Campaign5_2";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 10, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign5_2", "Campaign 5.2",
                "c5_2", "c5.2",
                "5_2"
        };
    }
}
