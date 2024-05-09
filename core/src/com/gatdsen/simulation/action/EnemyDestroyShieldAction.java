package com.gatdsen.simulation.action;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.IntVector2;


/**
 * Die Klasse EnemyDestroyShieldAction ist eine Unterklasse von {@link EnemyAction EnemyAction}
 * und repräsentiert eine Aktion, bei der Schild eines Gegners zerstört wird.
 */
public class EnemyDestroyShieldAction extends EnemyAction {

    /**
     * Konstruktor der Klasse EnemyDestroyShieldAction.
     *
     * @param delay Verzögerung bis zum Ausführen der Aktion
     * @param pos   Position des Gegners
     * @param level Level des Gegners
     * @param team  Team des Gegners
     * @param id    ID des Gegners
     */
    public EnemyDestroyShieldAction(float delay, IntVector2 pos, int level, int team, Enemy.EnemyType enemyType, int id) {
        super(delay, pos, level, team, enemyType, id);
    }

    @Override
    public String toString() {
        return "EnemyDestroyShieldAction{} " + super.toString();
    }
}
