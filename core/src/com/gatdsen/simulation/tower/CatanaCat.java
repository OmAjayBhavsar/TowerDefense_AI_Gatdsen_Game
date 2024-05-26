package com.gatdsen.simulation.tower;

import com.gatdsen.simulation.*;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ProjectileAction;
import com.gatdsen.simulation.action.TowerAttackAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Speichert einen CatanaCat.
 */
public class CatanaCat extends Tower {
    /**
     * Erstellt einen CatanaCat an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    public CatanaCat(PlayerState playerState, int x, int y, Tile[][] board) {
        super(playerState, TowerType.CATANA_CAT, x, y, board);
    }

    /**
     * Erstellt eine Kopie eines CatanaCat.
     *
     * @param original der zu kopierende CatanaCat
     */
    public CatanaCat(Tower original, PlayerState playerState) {
        super(original, playerState);
    }

    /**
     * Erstellt eine Kopie eines CatanaCat.
     *
     * @return eine Kopie des CatanaCat
     */
    @Override
    protected Tower copy(PlayerState NewPlayerState) {
        return new CatanaCat(this, NewPlayerState);
    }

    /**
     * @return Den Schaden des CatanaCat
     */
    @Override
    public int getDamage() {
        switch (level) {
            case 1: return 12;
            case 2: return 18;
            case 3: return 30;
            default: return 0;
        }
    }

    /**
     * @return Die Reichweite des CatanaCat
     */
    @Override
    public int getRange() {
        return 1;
    }

    /**
     * @return Die Zeit, die der CatanaCat zum Nachladen braucht
     */
    @Override
    public int getRechargeTime() {
        return 0;
    }

    @Override
    public void incrementCooldown() {
        cooldown += 1;
    }

    /**
     * @return Den Preis des CatanaCat
     */
    @Override
    public int getUpgradePrice() {
        switch (level) {
            case 1: return 100;
            case 2: return 130;
            case 3: return 160;
            default: return 0;
        }
    }

    /**
     * @return Liste von Gegnern, die vom Tower aus erreichbar sind
     */
    private List<Enemy> getTargets() {
        List<Enemy> targets = new ArrayList<>();
        for (PathTile tile : pathInRange)
            targets.addAll(tile.getEnemies());
        return targets;
    }

    /**
     * Führt einen Angriff aus, wenn möglich.
     *
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    @Override
    public Action attack(Action head) {
        if (pathInRange.isEmpty()) {
            return head;
        }

        if (getCooldown() > 0) {
            --cooldown;
            return head;
        }

        List<Enemy> targets = getTargets();

        if (!targets.isEmpty()) {
            for (Enemy target : targets) {
                head.addChild(new TowerAttackAction(0, pos, target.getPosition(), type.ordinal(), playerState.getIndex(), id));
                Path path = new LinearPath(pos.toFloat(), target.getPosition().toFloat(), 1);
                path.setDuration(0);
                head.addChild(new ProjectileAction(0, ProjectileAction.ProjectileType.STANDARD_TYPE, path, playerState.getIndex()));
                head = updateEnemyHealth(target, head);
            }

            cooldown = getRechargeTime();
        }
        return head;
    }
}
