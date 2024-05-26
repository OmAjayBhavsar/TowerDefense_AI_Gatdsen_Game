package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.*;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ProjectileAction;
import com.gatdsen.simulation.action.TowerAttackAction;

/**
 * Speichert eine MinigunCat.
 */
public class MinigunCat extends Tower {
    /**
     * Erstellt eine MinigunCat an der angegebenen Position.
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
     * Erstellt eine Kopie der MinigunCat.
     *
     * @param original die zu kopierende MinigunCat
     */
    public MinigunCat(Tower original, PlayerState playerState) {
        super(original, playerState);
    }

    /**
     * Erstellt eine Kopie der MinigunCat
     *
     * @return eine Kopie der MinigunCat
     */
    @Override
    protected Tower copy(PlayerState newPlayerstate) {
        return new MinigunCat(this, newPlayerstate);
    }

    /**
     * @return Den Schaden der MinigunCat
     */
    @Override
    public int getDamage() {
        int damage;
        switch (level) {
            case 1:
                damage = 35;
                break;
            case 2:
                damage = 60;
                break;
            case 3:
                damage = 90;
                break;
            default:
                damage = 0;
        }
        return (int) (damage * (1 - (float) minigunCatsInRange() / 10));
    }

    /**
     * Prüft, wie viele MinigunCats in Reichweite sind.
     *
     * @return Anzahl der MinigunCats in Reichweite
     */
    private int minigunCatsInRange() {
        return catsInRange(5);
    }

    /**
     * @return Die Reichweite der MinigunCat
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
     * @return Den Preis der MinigunCat
     */
    @Override
    public int getUpgradePrice() {
        switch (level) {
            case 1:
                return 80;
            case 2:
                return 100;
            case 3:
                return 120;
            default:
                return 0;
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
