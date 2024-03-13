package com.gatdsen.manager.player.data.penalty;

public final class MissTurnsPenalty implements Penalty {

    public final int turns;
    public final String reason;

    public MissTurnsPenalty(String reason) {
        this(1, reason);
    }

    public MissTurnsPenalty(int turns, String reason) {
        this.turns = turns;
        this.reason = reason;
    }
}
