package com.gatdsen.simulation.action;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.IntVector2;

/**
 * Die Klasse EnemyEMPAction ist eine Unterklasse von {@link EnemyAction EnemyAction}
 * und repräsentiert eine Aktion, bei der ein EMP-Angriff eines Gegners ausgeführt wird.
 */

public class EnemyEMPAction extends EnemyAction {
    private final int range;

    /**
     * Konstruktor der Klasse EnemyEMPAction.
     *
     * @param delay Verzögerung bis zum Ausführen der Aktion
     * @param pos   Position des Gegners
     * @param level Level des Gegners
     * @param team  Team des Gegners
     * @param type  Typ des Gegners
     * @param range Reichweite des EMP-Angriffs
     * @param id    ID des Gegners
     */

    public EnemyEMPAction(float delay, IntVector2 pos, int level, int team, Enemy.Type type, int range, int id) {
        super(delay, pos, level, team, type, id);
        this.range = range;
    }

    public int getRange() {
        return range;
    }

    @Override
    public String toString() {
        return "EnemyEMPAction{} " + super.toString();
    }
}
