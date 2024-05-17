package com.gatdsen.simulation.gamemode.campaign;

import java.time.LocalDateTime;

public class CampaignMode3_2 extends CampaignMode {

    public CampaignMode3_2() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign3_2");
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 27, 8, 0);
    }

    @Override
    public int getCampaignWeek() {
        return 3;
    }

    @Override
    public int getCampaignTask() {
        return 2;
    }
}
