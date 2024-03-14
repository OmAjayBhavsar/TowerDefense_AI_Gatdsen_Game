package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.player.data.penalty.Penalty;

/**
 * Diese Nachricht wird von einem Spielerprozess an den Spielprozess gesendet, um diesen über die Initialisierung des
 * Spielers, sowie über die daraus resultierende Strafe zu informieren.
 * Diese Nachricht wird als Antwort auf eine {@link PlayerInitRequest} gesendet.
 */
public final class PlayerInitResponse implements Message {

    /** Die Strafe, die der Spieler nach der Initialisierung erhält */
    public final Penalty penalty;

    /**
     * @param penalty Die Strafe, die der Spieler nach der Initialisierung erhält
     */
    public PlayerInitResponse(Penalty penalty) {
        this.penalty = penalty;
    }

    @Override
    public Type getType() {
        return Type.PlayerInitResponse;
    }
}
