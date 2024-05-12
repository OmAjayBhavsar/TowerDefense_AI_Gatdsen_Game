package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode3_2 extends CampaignMode {

    public CampaignMode3_2() {
        super();
        enemyBotHealth = 100;
        map = "Campaign3_2";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 27, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign3_2", "Campaign 3.2",
                "c3_2", "c3.2",
                "3_2"
        };
    }
}
