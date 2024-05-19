package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode4_1 extends CampaignMode {

    public CampaignMode4_1() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign4_1");
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 3, 8, 0);
    }

    @Override
    protected int getCampaignWeek() {
        return 4;
    }

    @Override
    protected int getCampaignTask() {
        return 1;
    }
}
