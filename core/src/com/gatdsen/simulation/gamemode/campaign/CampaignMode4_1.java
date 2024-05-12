package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode4_1 extends CampaignMode {

    public CampaignMode4_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign4_1";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 3, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign4_1", "Campaign 4.1", "Campaign 4",
                "c4_1", "c4.1", "c4",
                "4_1"
        };
    }
}
