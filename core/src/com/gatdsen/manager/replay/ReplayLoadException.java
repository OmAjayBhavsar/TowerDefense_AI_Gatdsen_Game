package com.gatdsen.manager.replay;

/**
 * Diese Exception wird geworfen, wenn beim Laden eines Replays ein Fehler aufgetreten ist.
 */
public final class ReplayLoadException extends ReplayException {

    /**
     * @param message Die Meldung, die den Grund für den Fehler beim Laden des Replays beschreibt
     */
    ReplayLoadException(String message) {
        super(message);
    }

    /**
     * @param message Die Meldung, die den Grund für den Fehler beim Laden des Replays beschreibt
     * @param cause Der Grund für den Fehler
     */
    ReplayLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
