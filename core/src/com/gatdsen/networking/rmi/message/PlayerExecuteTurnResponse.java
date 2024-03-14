package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.player.data.penalty.Penalty;

/**
 * Diese Nachricht wird von einem Spielerprozess an den Spielprozess gesendet, um diesen über die abgeschlossene
 * Durchführung eines Spielzugs durch den Spieler, sowie über die daraus resultierende Strafe zu informieren.
 * Diese Nachricht wird als Antwort auf eine {@link PlayerExecuteTurnRequest} gesendet, nachdem vorher mehrere
 * {@link PlayerCommandResponse} gesendet wurden.
 */
public class PlayerExecuteTurnResponse implements Message {

    /** Die Strafe, die der Spieler nach der Runde erhält */
    public final Penalty penalty;

    /**
     * @param penalty Die Strafe, die der Spieler nach der Runde erhält
     */
    public PlayerExecuteTurnResponse(Penalty penalty) {
        this.penalty = penalty;
    }

    @Override
    public Type getType() {
        return Type.PlayerExecuteTurnResponse;
    }
}
