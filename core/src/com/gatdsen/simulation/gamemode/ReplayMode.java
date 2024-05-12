package com.gatdsen.simulation.gamemode;

import com.gatdsen.simulation.GameMode;
import org.apache.commons.cli.CommandLine;

public final class ReplayMode extends GameMode {

    private String replayName;

    public String getReplayName() {
        return replayName;
    }

    public void setReplayName(String replayName) {
        this.replayName = replayName;
    }

    @Override
    public Type getType() {
        return Type.REPLAY;
    }

    @Override
    public String[] getIdentifiers() {
        return new String[]{String.valueOf(getType().ordinal()), "Replay", "ReplayMode"};
    }

    @Override
    public void parseFromCommandArguments(CommandLine params) {
        if (params.hasOption("replay")) {
            setReplayName(params.getOptionValue("replay"));
        /* Rückwärtskompatibilität:
         * Früher wurden Replays gestartet, indem die numerische ID als Spielmodus sowie die -m Flag missbraucht wurde,
         * um den Replay-Namen zu übergeben.
         */
        } else if (params.hasOption("m")) {
            setReplayName(params.getOptionValue("m"));
        }
    }
}
