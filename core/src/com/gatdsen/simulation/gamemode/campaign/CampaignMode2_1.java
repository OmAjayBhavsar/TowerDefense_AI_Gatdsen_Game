package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.PlayerState;
import com.gatdsen.simulation.Tower;

import java.time.LocalDateTime;

public class CampaignMode2_1 extends CampaignMode {

    public CampaignMode2_1() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign2_1");
        towers.remove(Tower.TowerType.MINIGUN_CAT);
        towers.remove(Tower.TowerType.CATANA_CAT);
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
        return 1;
    }

    @Override
    public int calculateSpawnCoinsForRound(PlayerState playerState) {
        return 0;
    }
}
