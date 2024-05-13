package com.gatdsen.simulation.gamemode;

/**
 * Normaler Spielmodus gegen einen IdleBot
 */
public class NormalMode extends PlayableGameMode {

    public NormalMode() {
        super();
        resetPlayerFactories();
    }

    @Override
    public String getDisplayName() {
        return "Normal";
    }

    @Override
    public Type getType() {
        return Type.NORMAL;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "normal", "default",
                String.valueOf(getType().ordinal()),
        };
    }
}
