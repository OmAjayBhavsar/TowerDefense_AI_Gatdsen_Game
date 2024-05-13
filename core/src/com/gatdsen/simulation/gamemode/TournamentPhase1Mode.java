package com.gatdsen.simulation.gamemode;

public class TournamentPhase1Mode extends PlayableGameMode {

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public String getDisplayName() {
        return "Turnier Phase 1";
    }

    @Override
    public Type getType() {
        return Type.TOURNAMENT_PHASE_1;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{
                "tournament_phase_1", "tournament_1",
                String.valueOf(getType().ordinal()),
        };
    }
}
