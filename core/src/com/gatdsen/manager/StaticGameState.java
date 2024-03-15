package com.gatdsen.manager;

import com.gatdsen.simulation.GameState;
import com.gatdsen.simulation.PlayerState;

/**
 * Diese Klasse repräsentiert den aktuellen Zustand des Spiels, dementsprechend auch den aktuellen Zustand aller
 * Spieler (siehe {@link StaticPlayerState} bzw. {@link #getMyPlayerState()}).
 * <p>
 * Wie es der Name bereits andeutet, ist diese Klasse statisch. Das bedeutet, dass sie sich nicht bspw. über die
 * Dauer eines Zuges verändert.
 */
public final class StaticGameState {

    private final GameState state;
    private final int playerIndex;
    private final StaticPlayerState[] staticPlayerStates;
    private final Timer timer;

    StaticGameState(GameState state, int playerIndex, Timer timer) {
        this.state = state;
        this.playerIndex = playerIndex;
        staticPlayerStates = new StaticPlayerState[state.getPlayerCount()];
        PlayerState[] playerStates = state.getPlayerStates();
        for (int i = 0; i < state.getPlayerCount(); i++) {
            staticPlayerStates[i] = new StaticPlayerState(playerStates[i]);
        }
        this.timer = timer;
    }

    /**
     * @return Gibt den PlayerState des aktuellen Spielers zurück
     */
    public StaticPlayerState getMyPlayerState() {
        return staticPlayerStates[playerIndex];
    }

    /**
     * @return Gibt den PlayerState des Gegners zurück
     */
    public StaticPlayerState getEnemyPlayerState() {
        return staticPlayerStates[(playerIndex + 1) % state.getPlayerCount()];
    }

    /**
     * Gibt die aktuelle Runde zurück. <br>
     * Dieser Wert ist nicht nur nützlich, um zu überprüfen, wie viele Runden das Spiel bereits andauert, sondern
     * ebenfalls um zu überprüfen, ob man eine Runde aussetzen musste, da in diesem Fall die {@code executeTurn()} Methode des
     * Spielers nicht aufgerufen wird.
     * @return Die aktuelle Runde
     */
    public int getTurn() {
        return state.getTurn();
    }

    /**
     * Gibt die Anzahl der Spieler zurück, die am Spiel teilnehmen. <br>
     * Im Normalfall sind dies 2 Spieler.
     * @return Die Anzahl der Spieler
     */
    public int getPlayerCount() {
        return state.getPlayerCount();
    }

    /**
     * Gibt die Breite des Spielfeldes zurück. <br>
     * Diese Methode kann bspw. in Verbindung mit {@link StaticPlayerState#getBoard()} verwendet werden.
     * @return Die Breite des Spielfeldes
     */
    public int getBoardSizeX() {
        return state.getBoardSizeX();
    }

    /**
     * Gibt die Höhe des Spielfeldes zurück. <br>
     * Diese Methode kann bspw. in Verbindung mit {@link StaticPlayerState#getBoard()} verwendet werden.
     * @return Die Höhe des Spielfeldes
     */
    public int getBoardSizeY() {
        return state.getBoardSizeY();
    }

    /**
     * Gibt ein {@link Timer} Objekt zurück, welches verwendet werden kann, um die vergangene Zeit seit Rundenbeginn
     * und die verbleibende Zeit bis zum Rundenende zu erhalten.
     * @return Das {@link Timer} Objekt der aktuellen Runde
     */
    public Timer getTimer() {
        return timer;
    }
}
