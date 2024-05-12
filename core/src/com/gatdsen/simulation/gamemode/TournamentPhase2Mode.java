package com.gatdsen.simulation.gamemode;

public class TournamentPhase2Mode extends PlayableGameMode {

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.TOURNAMENT_PHASE_2;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                String.valueOf(getType().ordinal()),
                "tournament_phase_2", "tournament_2",
        };
    }
}
