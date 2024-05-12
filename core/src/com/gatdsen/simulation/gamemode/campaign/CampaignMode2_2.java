package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode2_2 extends CampaignMode {

    public CampaignMode2_2() {
        super();
        enemyBotHealth = 100;
        map = "Campaign2_2";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 20, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign2_2", "Campaign 2.2",
                "c2_2", "c2.2",
                "2_2"
        };
    }
}
