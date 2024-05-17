package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode4_2 extends CampaignMode {

    public CampaignMode4_2() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign4_2");
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 3, 8, 0);
    }

    @Override
    public int getCampaignWeek() {
        return 4;
    }

    @Override
    public int getCampaignTask() {
        return 2;
    }
}
