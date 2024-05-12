package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode3_1 extends CampaignMode {

    public CampaignMode3_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign3_1";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 27, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign3_1", "Campaign 3.1", "Campaign 3",
                "c3_1", "c3.1", "c3",
                "3_1"
        };
    }
}
