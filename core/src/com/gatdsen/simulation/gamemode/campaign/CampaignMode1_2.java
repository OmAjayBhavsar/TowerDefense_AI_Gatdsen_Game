package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tower;

import java.time.LocalDateTime;

public class CampaignMode1_2 extends CampaignMode {

    public CampaignMode1_2() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign1_2");
        towers.remove(Tower.TowerType.MINIGUN_CAT);
        towers.remove(Tower.TowerType.MAGE_CAT);
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 13, 8, 0);
    }

    @Override
    protected int getCampaignWeek() {
        return 1;
    }

    @Override
    protected int getCampaignTask() {
        return 2;
    }

    @Override
    public int calculateSpawnCoinsForRound(PlayerState playerState) {
        return 0;
    }
}
