package com.gatdsen.manager.command;

import com.gatdsen.manager.player.handler.PlayerHandler;
import com.gatdsen.simulation.action.ActionLog;

import java.io.Serializable;
import java.util.List;

/**
 * Die Basisklasse für alle Befehle.
 * Ein Befehl repräsentiert eine Aktion, die von einem Spieler ausgeführt wird.
 */
public abstract class Command implements Serializable {

    protected abstract ActionLog onExecute(PlayerHandler playerHandler);

    public final ActionLog run(PlayerHandler playerHandler) {
        return onExecute(playerHandler);
    }

    public boolean endsTurn() {
        return false;
    }

    public interface CommandHandler {

        default void handle(Command command) {
            handle(List.of(command));
        }

        void handle(List<Command> commands);
    }
}
