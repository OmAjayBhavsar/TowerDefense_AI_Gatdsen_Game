package com.gatdsen.simulation;

import com.gatdsen.manager.command.Command;
import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ActionLog;

/**
 * Diese Klasse repräsentiert die Schnittstelle, um von {@link Command}s aus die vom Spieler durchgeführten
 * Spielentscheidungen an die Simulation zu übergeben, indem sie in {link Action}s umgewandelt werden.
 */
public class PlayerController {
    private final int playerIndex;
    private final GameState state;
    private final PlayerState playerState;

    /**
     * Disqualifiziert den Spieler
     */
    public void disqualify() {
        playerState.disqualify();
    }

    /**
     * @return die root Action des ActionLogs, welches die Simulation aktuell aufzeichnet
     */
    private Action getRoot() {
        return state.getSim().getActionLog().getRootAction();
    }

    /**
     * Signalisiere das aktuelle Kommando als abgeschlossen und setze den ActionLog für das nächste Kommando zurück
     *
     * @return Der ActionLog, der durch das zuvor ausgeführte Kommando erzeugt wurde
     */
    private ActionLog endCommand() {
        return state.getSim().clearAndReturnActionLog();
    }

    /**
     * Erstellt einen neuen PlayerController
     *
     * @param playerIndex Index des Spielers
     * @param state       der aktuelle GameState
     */
    protected PlayerController(int playerIndex, GameState state) {
        this.playerIndex = playerIndex;
        this.state = state;
        this.playerState = state.getPlayerStates()[playerIndex];
    }

    /**
     * @return Index des Spielers
     */
    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * Platziert einen Turm auf dem Spielfeld
     *
     * @param x    x-Koordinate
     * @param y    y-Koordinate
     * @param type Turm-Typ als Enum
     * @return Der ActionLog der durch das Ausführen des Befehls entstanden ist
     */
    public ActionLog placeTower(int x, int y, Tower.TowerType type) {
        playerState.placeTower(x, y, type, getRoot());
        return endCommand();
    }

    /**
     * Upgraded einen Turm auf dem Spielfeld
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return Der ActionLog der durch das Ausführen des Befehls entstanden ist
     */
    public ActionLog upgradeTower(int x, int y) {
        playerState.upgradeTower(x, y, getRoot());
        return endCommand();
    }

    /**
     * Verkauft einen Turm auf dem Spielfeld
     *
     * @param x x-Koordinate
     * @param y y-Koordinate
     * @return Der ActionLog der durch das Ausführen des Befehls entstanden ist
     */
    public ActionLog sellTower(int x, int y) {
        playerState.sellTower(x, y, getRoot());
        return endCommand();
    }

    /**
     * Setzt das Ziel eines Turms
     *
     * @param x            x-Koordinate
     * @param y            y-Koordinate
     * @param targetOption Zieloption als Enum
     * @return Der ActionLog der durch das Ausführen des Befehls entstanden ist
     */
    public ActionLog setTarget(int x, int y, Tower.TargetOption targetOption) {
        playerState.setTarget(x, y, targetOption, getRoot());
        return endCommand();
    }

    /**
     * Beleidigt den Gegner
     *
     * @return Der ActionLog der durch das Ausführen des Befehls entstanden ist
     */
    public ActionLog insultEnemy() {
        // ToDo: "du Hund"
        return endCommand();
    }
}
