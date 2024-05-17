package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode5_2 extends CampaignMode {

    public CampaignMode5_2() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign5_2");
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 10, 8, 0);
    }

    @Override
    public int getCampaignWeek() {
        return 5;
    }

    @Override
    public int getCampaignTask() {
        return 2;
    }
}
