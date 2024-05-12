package com.gatdsen.simulation.enemy;

import com.gatdsen.simulation.*;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.EnemyEMPAction;

import java.util.List;

public class EmpEnemy extends Enemy {

    private int range = 2;
    private int cooldown = 3;

    /**
     * Erstellt einen neuen EMP-Gegner.
     *
     * @param playerState Der Spieler, dem der Gegner gehört.
     * @param level       Die Stufe des Gegners.
     * @param posTile     Die Position des Gegners.
     */

    public EmpEnemy(PlayerState playerState, int level, PathTile posTile) {
        super(playerState, level, posTile);
        enemyType = EnemyType.EMP_ENEMY;
        health = 120 * level;
        damage = 5 * level;
    }

    /**
     * Aktiviert die EMP-Attacke des Gegners, welche die Rechargetime der umliegenden Tower erhöht.
     * @param head
     * @return Die Action, die der Gegner ausführt.
     */
    public Action EMP(Action head) {
        if (cooldown == 0) {
            List<Tile> inRange = getNeighbours(range, playerState.getBoard());
            for (Tile tile : inRange) {
                if (tile instanceof TowerTile) {
                    TowerTile towerTile = (TowerTile) tile;
                    towerTile.getTower().incrementRechargeTime();
                }
            }
            cooldown = 3;
            head.addChild(new EnemyEMPAction(0, posTile.getPosition(), level, team, enemyType, range, id));
        } else {
            cooldown--;
        }
        return head;
    }

    public int getRange() {
        return range;
    }

    /**
     * Erstellt eine Kopie des Gegners.
     *
     * @param posTile Die Position des Gegners.
     * @return Die Kopie des Gegners.
     */
    @Override
    protected Enemy copy(PathTile posTile) {
        return new EmpEnemy(playerState, level, posTile);
    }

    /**
     * Gibt den Preis des Gegners zurück.
     *
     * @param level Die Stufe des Gegners.
     * @return Der Preis des Gegners.
     */
    public static int getPrice(int level) {
        return 8 * level;
    }
}
