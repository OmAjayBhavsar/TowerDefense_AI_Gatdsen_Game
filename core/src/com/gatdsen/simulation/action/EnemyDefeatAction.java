package com.gatdsen.simulation.action;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.IntVector2;

/**
 * Die Klasse EnemyDefeatAction ist eine Unterklasse von {@link EnemyAction EnemyAction}
 * und repräsentiert eine Aktion, bei der ein Gegner besiegt wird.
 */
public class EnemyDefeatAction extends EnemyAction {

    /**
     * Konstruktor der Klasse EnemyDefeatAction.
     *
     * @param delay Verzögerung bis zum Ausführen der Aktion
     * @param pos   Position des Gegners
     * @param level Level des Gegners
     * @param team  Team des Gegners
     * @param id    ID des Gegners
     */
    public EnemyDefeatAction(float delay, IntVector2 pos, int level, int team, Enemy.Type type, int id) {
        super(delay, pos, level, team, type, id);
    }

    @Override
    public String toString() {
        return "EnemyDefeatAction{} " + super.toString();
    }
}
