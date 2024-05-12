package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode6_2 extends CampaignMode {

    public CampaignMode6_2() {
        super();
        enemyBotHealth = 100;
        map = "Campaign6_2";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 17, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign6_2", "Campaign 6.2",
                "c6_2", "c6.2",
                "6_2"
        };
    }
}
