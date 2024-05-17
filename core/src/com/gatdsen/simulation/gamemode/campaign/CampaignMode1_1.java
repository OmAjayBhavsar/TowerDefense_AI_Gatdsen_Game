package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tower;

import java.time.LocalDateTime;

public class CampaignMode1_1 extends CampaignMode {

    public CampaignMode1_1() {
        super();
        setPlayerHealth(0, 100);
        setMap("Campaign1_1");
        towers.remove(Tower.TowerType.CATANA_CAT);
        towers.remove(Tower.TowerType.MAGE_CAT);
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 13, 8, 0);
    }

    @Override
    public int getCampaignWeek() {
        return 1;
    }

    @Override
    public int getCampaignTask() {
        return 1;
    }

    @Override
    public int calculateSpawnCoinsForRound(PlayerState playerState) {
        return 0;
    }
}
