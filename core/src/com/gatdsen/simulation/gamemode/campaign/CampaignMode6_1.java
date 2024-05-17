package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode6_1 extends CampaignMode {

    public CampaignMode6_1() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign6_1");
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 6, 17, 8, 0);
    }

    @Override
    public int getCampaignWeek() {
        return 6;
    }

    @Override
    public int getCampaignTask() {
        return 1;
    }
}
