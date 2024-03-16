package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.command.Command;

import java.util.List;

/**
 * Diese Nachricht wird von einem Spielerprozess an den Spielprozess gesendet, um diesen über einen vom Spieler
 * ausgeführten Befehl zu informieren.
 * Mehrere dieser Nachrichten werden als Antwort auf eine {@link PlayerExecuteTurnRequest} gesendet, sowie anschließend
 * eine finale {@link PlayerExecuteTurnResponse}, nachdem alle Befehle dieses Spielzugs übermittelt wurden.
 */
public final class PlayerCommandResponse implements Message {

    /** Der Befehl, den der Spieler ausgeführt hat */
    public final List<Command> commands;

    /**
     * @param commands Der Befehl, den der Spieler ausgeführt hat
     */
    public PlayerCommandResponse(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public Type getType() {
        return Type.PlayerCommandResponse;
    }
}
