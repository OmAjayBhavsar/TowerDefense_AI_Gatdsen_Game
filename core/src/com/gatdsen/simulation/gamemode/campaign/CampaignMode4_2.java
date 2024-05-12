package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode4_2 extends CampaignMode {

    public CampaignMode4_2() {
        super();
        enemyBotHealth = 100;
        map = "Campaign4_2";
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 3, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign4_2", "Campaign 4.2",
                "c4_2", "c4.2",
                "4_2"
        };
    }
}
