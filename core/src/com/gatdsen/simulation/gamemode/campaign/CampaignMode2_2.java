package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode2_2 extends CampaignMode {

    public CampaignMode2_2() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign2_2");
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 20, 8, 0);
    }

    @Override
    protected int getCampaignWeek() {
        return 2;
    }

    @Override
    protected int getCampaignTask() {
        return 2;
    }
}
