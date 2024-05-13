package com.gatdsen.simulation.gamemode;

/**
 * Spielmodus für die Weihnachtsaufgabe
 */
public class ChristmasMode extends PlayableGameMode {

    public ChristmasMode() {
        super();
        setPlayerHealth(1, 500);
        setMap("map2");
    }

    @Override
    public String getDisplayName() {
        return "Weihnachtsaufgabe";
    }

    @Override
    public Type getType() {
        return Type.CHRISTMAS_TASK;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "Weihnachtsaufgabe", "christmas_task", "christmas", "christmas_mode", String.valueOf(getType().ordinal())
        };
    }
}
