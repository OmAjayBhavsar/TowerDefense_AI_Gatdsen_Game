package com.gatdsen.manager.player.data.penalty;

public final class DisqualificationPenalty implements Penalty {

    public final String reason;

    public DisqualificationPenalty(String reason) {
        this.reason = reason;
    }
}
