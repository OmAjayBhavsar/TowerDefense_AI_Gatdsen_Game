package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.command.Command;

/**
 * Diese Nachricht wird von einem Spielerprozess an den Spielprozess gesendet, um diesen über einen vom Spieler
 * ausgeführten Befehl zu informieren.
 * Mehrere dieser Nachrichten werden als Antwort auf eine {@link PlayerExecuteTurnRequest} gesendet.
 */
public final class PlayerCommandResponse implements Message {

    /** Der Befehl, den der Spieler ausgeführt hat */
    public final Command command;

    /**
     * @param command Der Befehl, den der Spieler ausgeführt hat
     */
    public PlayerCommandResponse(Command command) {
        this.command = command;
    }

    @Override
    public Type getType() {
        return Type.PlayerCommandResponse;
    }
}
