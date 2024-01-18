package com.gatdsen.simulation.action;

import com.gatdsen.simulation.IntVector2;

public class TowerUpgradeAction extends TowerAction{

    /**
     * Speichert das Ereignis, dass ein Turm platziert wurde
     * @param delay nicht-negativer zeitbasierter Offset zu seinem Elternteil in Sekunden
     * @param pos Position des Turms
     * @param type Typ des Turms
     * @param team index des Teams
     * @param id id des Turms
     */

    public TowerUpgradeAction(float delay, IntVector2 pos, int type, int team, int id) {
        super(delay, pos, type, team, id);
    }

    @Override
    public String toString() {
        return "TowerUpgradeAction{}" + super.toString();
    }
}
