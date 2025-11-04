package com.gatdsen.manager.player;

import com.gatdsen.manager.Controller;
import com.gatdsen.manager.state;

/**
 * Dieser Bot repräsentiert einen Spieler, der keine Aktionen ausführt.
 */
public final class IdleBot extends Bot {

    @Override
    public String getName() {
        return "IdleBot";
    }

    @Override
    public int getMatrikel() {
        return 1337;
    }

    @Override
    public String getStudentName() {
        return "Gadsen: 2023";
    }

    @Override
    public void init(state state) {
    }

    @Override
    public void executeTurn(state state, Controller controller) {
    }
}
