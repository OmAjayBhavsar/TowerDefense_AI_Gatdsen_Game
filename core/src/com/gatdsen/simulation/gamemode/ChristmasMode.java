package com.gatdsen.simulation.gamemode;

/**
 * Spielmodus für die Weihnachtsaufgabe
 */
public class ChristmasMode extends PlayableGameMode {

    public ChristmasMode() {
        super();
        enemyBotHealth = 500;
        map = "map2";
    }

    @Override
    public Type getType() {
        return Type.CHRISTMAS_TASK;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                String.valueOf(getType().ordinal()),
                "christmas", "christmas_task", "christmas_mode"
        };
    }
}
