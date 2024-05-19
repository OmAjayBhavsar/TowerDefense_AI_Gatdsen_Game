package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.simulation.Enemy;

import java.time.LocalDateTime;

public class CampaignMode3_1 extends CampaignMode {

    public CampaignMode3_1() {
        super();
        setPlayerHealth(1, 100);
        setMap("Campaign3_1");
        enemies.remove(Enemy.EnemyType.EMP_ENEMY);
        enemies.remove(Enemy.EnemyType.ARMOR_ENEMY);
    }

    @Override
    protected LocalDateTime getSubmissionOpenTimestamp() {
        return LocalDateTime.of(2024, 5, 27, 8, 0);
    }

    @Override
    protected int getCampaignWeek() {
        return 3;
    }

    @Override
    protected int getCampaignTask() {
        return 1;
    }
}
