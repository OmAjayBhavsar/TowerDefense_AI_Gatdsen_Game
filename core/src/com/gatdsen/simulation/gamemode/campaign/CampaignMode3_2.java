package com.gatdsen.simulation.gamemode.campaign;

import com.gatdsen.manager.player.handler.LocalPlayerHandlerFactory;
import com.gatdsen.simulation.Enemy;

import java.time.LocalDateTime;

public class CampaignMode3_2 extends CampaignMode {

    public CampaignMode3_2() {
        super();
        setPlayerHealth(1, 100);
        setPlayerHealth(0, 100000);
        setPlayerMoney(1, 1000);
        setPlayerSpawnCoins(0, 1090);
        setMap("Campaign3_2");
        enemies.remove(Enemy.EnemyType.SHIELD_ENEMY);
        enemies.remove(Enemy.EnemyType.ARMOR_ENEMY);
        setPlayerFactory(1, LocalPlayerHandlerFactory.CAMPAIGN_32_BOT);
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
        return 2;
    }
}
