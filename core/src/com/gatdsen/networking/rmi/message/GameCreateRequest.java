package com.gatdsen.networking.rmi.message;

import com.gatdsen.manager.player.Player;

/**
 * Diese Nachricht wird vom Spielprozess an einen Spielerprozess gesendet, um diesen mit den gegebenen Parametern auf
 * ein Spiel vorzubereiten, an dem er teilnehmen soll.
 * Als Antwort auf diese Nachricht wird eine {@link GameCreateResponse} erwartet.
 */
public final class GameCreateRequest implements Message {

    /** Ob sich das Spiel im Debug-Modus befindet */
    public final boolean isDebug;
    /** Die ID des Spiels */
    public final int gameId;
    /** Die ID des Spielers */
    public final int playerId;
    /** Die Klasse des Spielers */
    public final Class<? extends Player> playerClass;

    /**
     * Dieser Konstruktor wird verwendet, wenn es bei dem Spieler auf Seiten des Spielprozesses keinen Bezug zur
     * Klasse des Spielers gibt, bspw. bei Multiplayer-Spielen.
     * Der Wert von {@code playerClass} ist dann {@code null}.
     * @param isDebug Ob sich das Spiel im Debug-Modus befindet
     * @param gameId Die ID des Spiels
     * @param playerId Die ID des Spielers
     */
    public GameCreateRequest(boolean isDebug, int gameId, int playerId) {
        this(isDebug, gameId, playerId, null);
    }

    /**
     * @param isDebug Ob sich das Spiel im Debug-Modus befindet
     * @param gameId Die ID des Spiels
     * @param playerId Die ID des Spielers
     * @param playerClass Die Klasse des Spielers
     */
    public GameCreateRequest(boolean isDebug, int gameId, int playerId, Class<? extends Player> playerClass) {
        this.isDebug = isDebug;
        this.gameId = gameId;
        this.playerId = playerId;
        this.playerClass = playerClass;
    }

    @Override
    public Type getType() {
        return Type.GameCreateRequest;
    }
}
