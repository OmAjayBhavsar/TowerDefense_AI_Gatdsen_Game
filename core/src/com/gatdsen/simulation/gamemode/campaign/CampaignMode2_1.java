package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode2_1 extends CampaignMode {

    public CampaignMode2_1() {
        super();
        enemyBotHealth = 100;
        map = "Campaign2_1";
        towers.remove(1);
        towers.remove(0);
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 20, 8, 0);
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Campaign2_1", "Campaign 2.1", "Campaign 2",
                "c2_1", "c2.1", "c2",
                "2_1"
        };
    }
}
