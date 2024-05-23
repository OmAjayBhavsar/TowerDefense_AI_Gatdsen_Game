package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.*;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ProjectileAction;
import com.gatdsen.simulation.action.TowerAttackAction;

import java.util.List;

/**
 * Speichert einen MinigunCat.
 */
public class MinigunCat extends Tower {
    /**
     * Erstellt einen MinigunCat an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    public MinigunCat(PlayerState playerState, int x, int y, Tile[][] board) {
        super(playerState, TowerType.MINIGUN_CAT, x, y, board);
    }

    /**
     * Erstellt eine Kopie eines BasicTowers.
     *
     * @param original der zu kopierende MinigunCat
     */
    public MinigunCat(Tower original, PlayerState playerState) {
        super(original, playerState);
    }

    /**
     * Erstellt eine Kopie eines BasicTowers.
     *
     * @return eine Kopie des BasicTowers
     */
    @Override
    protected Tower copy(PlayerState newPlayerstate) {
        return new MinigunCat(this, newPlayerstate);
    }

    /**
     * @return Den Schaden des BasicTowers
     */
    @Override
    public int getDamage() {
       int damage;
        switch (level) {
            case 1: damage = 35;
            break;
            case 2: damage =  60;
            break;
            case 3: damage =  90;
            break;
            default: damage =  0;
        }
        return basicTowerInRange() ? damage/2 : damage;
    }

    /**
     * Prüft, ob ein MinigunCat in Reichweite ist.
     * @return true, wenn ein MinigunCat in Reichweite ist, sonst false
     */
    private boolean basicTowerInRange() {
        List<Tile> inRange = getNeighbours(getRange(), playerState.getBoard());
        for (Tile tile : inRange) {
            if (tile == null || tile.getPosition().equals(pos)) continue;
            if (tile instanceof TowerTile) {
                TowerTile towerTile = (TowerTile) tile;
                if (towerTile.getTower().getType() == TowerType.MINIGUN_CAT) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return Die Reichweite des BasicTowers
     */
    @Override
    public int getRange() {
        return 2;
    }

    /**
     * @return Die Zeit, die der MinigunCat zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 0;
    }

    @Override
    public void incrementCooldown() {
        cooldown += 2;
    }

    /**
     * @return Den Preis des BasicTowers
     */
    @Override
    public int getUpgradePrice() {
        switch (level) {
            case 1: return 80;
            case 2: return 100;
            case 3: return 120;
            default: return 0;
        }
    }

    /**
     * Führt einen Angriff aus, wenn möglich.
     *
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    protected Action attack(Action head) {
        if (pathInRange.isEmpty()) {
            return head;
        }

        if (getCooldown() > 0) {
            --cooldown;
            return head;
        }

        Enemy target = getTarget();

        if (target != null) {
            head.addChild(new TowerAttackAction(0, pos, target.getPosition(), type.ordinal(), playerState.getIndex(), id));
            Path path = new LinearPath(pos.toFloat(), target.getPosition().toFloat(), 1);
            path.setDuration(0);
            head.addChild(new ProjectileAction(0, ProjectileAction.ProjectileType.STANDARD_TYPE, path, playerState.getIndex()));
            head = updateEnemyHealth(target, head);
            cooldown = getRechargeTime();
        }
        return head;
    }
}
