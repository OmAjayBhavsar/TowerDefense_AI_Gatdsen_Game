package com.gatdsen.simulation.enemy;

import com.gatdsen.simulation.*;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.EnemyEMPAction;

import java.util.ArrayList;
import java.util.List;

public class EmpEnemy extends Enemy {

    private int range = 2;
    private int cooldown = 3;

    /**
     * Erstellt einen neuen Gegner.
     *
     * @param playerState
     * @param level       Die Stufe des Gegners.
     * @param posTile     Die Position des Gegners.
     */

    public EmpEnemy(PlayerState playerState, int level, PathTile posTile) {
        super(playerState, level, posTile);
        type = Type.EMP_ENEMY;
        health = 150 * level;
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
            head.addChild(new EnemyEMPAction(0, posTile.getPosition(), level, team, type, range, id));
        } else {
            cooldown--;
        }
        return head;
    }

    /**
     * Gibt die Nachbar-Felder des Gegners zurück.
     * @param range
     * @param board
     * @return Liste der Nachbar-Felder
     */
    private List<Tile> getNeighbours(int range, Tile[][] board) {
        IntVector2 pos = posTile.getPosition();
        int diameter = (range * 2) + 1;
        List<Tile> neighbours = new ArrayList<>(diameter * diameter - 1);
        IntRectangle rec = new IntRectangle(0, 0, board.length - 1, board[0].length - 1);
        for (int i = 0; i < diameter; i++) {
            for (int j = 0; j < diameter; j++) {
                if (rec.contains(pos.x - range + i, pos.y - range + j)) {
                    neighbours.add(board[pos.x - range + i][pos.y - range + j]);
                }
            }
        }
        return neighbours;
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

    public static int getPrice(int level) {
        return 100 * level;
    }
}
