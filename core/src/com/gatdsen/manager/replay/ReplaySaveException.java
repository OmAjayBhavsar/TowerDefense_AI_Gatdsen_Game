package com.gatdsen.manager.replay;

/**
 * Diese Exception wird geworfen, wenn beim Speichern eines Replays ein Fehler aufgetreten ist.
 */
public final class ReplaySaveException extends ReplayException {

    /** Das Replay, das gespeichert werden sollte */
    private final Replay replay;

    /**
     * @param replay Das Replay, das gespeichert werden sollte
     * @param message Die Fehlermeldung
     */
    ReplaySaveException(Replay replay, String message) {
        super(message);
        this.replay = replay;
    }

    /**
     * @param replay Das Replay, das gespeichert werden sollte
     * @param message Die Fehlermeldung
     * @param cause Der Grund für den Fehler
     */
    ReplaySaveException(Replay replay, String message, Throwable cause) {
        super(message, cause);
        this.replay = replay;
    }

    /**
     * @return Das Replay, das gespeichert werden sollte
     */
    public Replay getReplay() {
        return replay;
    }
}
