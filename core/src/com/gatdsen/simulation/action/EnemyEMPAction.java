package com.gatdsen.simulation.action;

import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.IntVector2;
public class EnemyEMPAction extends EnemyAction{
    private final int range;
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
