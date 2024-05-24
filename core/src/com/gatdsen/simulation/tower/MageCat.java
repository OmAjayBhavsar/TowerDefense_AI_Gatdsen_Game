package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.*;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ProjectileAction;
import com.gatdsen.simulation.action.TowerAttackAction;

/**
 * Speichert einen MageCat.
 */
public class MageCat extends Tower {
    /**
     * Erstellt einen MageCat an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    public MageCat(PlayerState playerState, int x, int y, Tile[][] board) {
        super(playerState, TowerType.MAGE_CAT, x, y, board);
    }

    /**
     * Erstellt eine Kopie eines MageCat.
     *
     * @param original der zu kopierende MageCat
     */
    public MageCat(Tower original, PlayerState playerState) {
        super(original, playerState);
    }

    /**
     * Erstellt eine Kopie eines MageCat.
     *
     * @return eine Kopie des MageCat
     */
    @Override
    protected Tower copy(PlayerState newPlayerState) {
        return new MageCat(this, newPlayerState);
    }

    /**
     * @return Den Schaden des MageCat
     */
    @Override
    public int getDamage() {
        int damage;
        switch (level) {
            case 1:
                damage = 150;
                break;
            case 2:
                damage = 250;
                break;
            case 3:
                damage = 350;
                break;
            default:
                damage = 0;
        }
        return (int) (damage * (1 - (float) mageCatsInRange() / 10));
    }

    /**
     * Prüft, wie viele MageCats in Reichweite sind.
     *
     * @return Anzahl der MageCats in Reichweite
     */
    private int mageCatsInRange() {
        return catsInRange(5);
    }

    /**
     * @return Die Reichweite des MageCat
     */
    @Override
    public int getRange() {
        return 4;
    }

    /**
     * @return Die Zeit, die der MageCat zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 3;
    }

    @Override
    public void incrementCooldown() {
        cooldown += 3;
    }

    /**
     * @return Den Preis des MageCat
     */
    @Override
    public int getUpgradePrice() {
        switch (level) {
            case 1:
                return 100;
            case 2:
                return 150;
            case 3:
                return 250;
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
    @Override
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
            Path path = new LinearPath(pos.toFloat(), target.getPosition().toFloat(), 0.1f);
            path.setDuration(0.5f);
            head.addChild(new ProjectileAction(0, ProjectileAction.ProjectileType.STANDARD_TYPE, path, playerState.getIndex()));
            if (target.getEnemyType() != Enemy.EnemyType.SHIELD_ENEMY) {
                head = updateEnemyHealth(target, head);
            }
            cooldown = getRechargeTime();
        }
        return head;
    }
}

