package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode5_1 extends CampaignMode {

    public CampaignMode5_1() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign5_1");
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 10, 8, 0);
    }

    @Override
    protected int getCampaignWeek() {
        return 5;
    }

    @Override
    protected int getCampaignTask() {
        return 1;
    }
}
