package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode6_1 extends CampaignMode {

    public CampaignMode6_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign6_1";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 17, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign6_1", "Campaign 6.1", "Campaign 6",
                "c6_1", "c6.1", "c6",
                "6_1"
        };
    }
}
