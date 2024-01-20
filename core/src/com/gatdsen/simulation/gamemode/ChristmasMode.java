package com.gatdsen.simulation.gamemode;

import com.gatdsen.simulation.GameMode;

/**
 * Spielmodus für die Weihnachtsaufgabe
 */
public class ChristmasMode extends GameMode {
    public ChristmasMode() {
        super();
        enemyBotHealth = 500;
        map = "map2";
    }
}
