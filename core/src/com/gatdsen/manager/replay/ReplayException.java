package com.gatdsen.manager.replay;

/**
 * Basisklasse für alle Exceptions, die im Zusammenhang mit {@link Replay}s auftreten.
 */
public abstract class ReplayException extends Exception {

    /**
     * @param message Die Fehlermeldung
     */
    ReplayException(String message) {
        super(message);
    }

    /**
     * @param message Die Fehlermeldung
     * @param cause Der Grund für die Exception
     */
    ReplayException(String message, Throwable cause) {
        super(message, cause);
    }
}
