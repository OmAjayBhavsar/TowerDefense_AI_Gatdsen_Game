package com.gatdsen.manager.player;

import com.gatdsen.manager.Controller;
import com.gatdsen.manager.state;
import com.gatdsen.simulation.*;
import com.gatdsen.simulation.Tower.TowerType;

/**
 * DISCLAIMER:
 * <p>
 * Bist du dir sicher, dass du diesen Code anschauen solltest? <p>
 *   1. Der Bot ist echt nicht besonders gut und hat keine Chance gegen eine von dir gut durchdachte Lösung... <p>
 *   2. Falls du dir erhoffst einen taktischen Vorteil zu erlangen, indem du dir den Code anschaust: Im Wettbewerb gegen
 *      andere Studierende hast du auch nicht die Möglichkeit vorher deren Code zu sehen. <p>
 *   3. Falls du unseren Code kopieren möchtest: Dies kann zur Disqualifikation am Wettbewerb und damit auch zum nicht
 *      Erreichen der Klausurzulassung führen! <p>
 */
public final class ExamAdmissionBot extends Bot {

    @Override
    public String getStudentName() {
        return "Gadsen: Tower Defense";
    }

    @Override
    public int getMatrikel() {
        return 42069;
    }

    @Override
    public String getName() {
        return "ExamBot";
    }

    private PathTile tmpTile;

    /**
     * Wird vor Beginn des Spiels aufgerufen. Die erlaubte Berechnungszeit für diese Methode beträgt 1 Sekunde.
     * Diese Methode kann daher verwendet werden, um Variablen zu initialisieren und einmalig, sehr rechenaufwändige
     * Operationen durchzuführen.
     *
     * @param state Der {@link state Spielzustand} zu Beginn des Spiels
     */
    @Override
    public void init(state state) {
        PathTile startTile = state.getMyPlayerState().getCheeseTile();
        while (startTile.getPrev() != null) {
            startTile = startTile.getPrev();
        }
        tmpTile = startTile.getNext().getNext();
    }

    int counter = 2;

    /**
     * Wird aufgerufen, wenn der Spieler seinen Zug für die aktuelle Runde durchführen soll. Die erlaubte
     * Berechnungszeit für diese Methode beträgt 0,5 Sekunden bzw. 500 Millisekunden.
     * <p>
     * Der {@link state Spielzustand} reflektiert dabei den Zustand des Spiels vor dem Zug des Spielers. Der
     * Zustand ist statisch, das heißt bei Aufrufen des {@link Controller Controllers} werden diese Änderungen nicht im
     * Spielzustand in dieser Runde reflektiert, sondern erst in der nächsten Runde, wenn man den neuen Spielzustand erhält.
     * <p>
     * Der Controller ermöglicht dir die Steuerung, um Aktionen, wie bspw. das Platzieren von Türmen, auszuführen. Die
     * übergebene Controller-Instanz deaktiviert sich nach Ende des Zuges permanent.
     *
     * @param state      Der {@link state Spielzustand} vor der Ausführung des aktuellen Zuges
     * @param controller Der {@link Controller Controller}, um Aktionen auszuführen
     */
    @Override
    public void executeTurn(state state, Controller controller) {
        TowerType type = TowerType.values()[counter % TowerType.values().length];
        if (state.getMyPlayerState().getMoney() >= Tower.getTowerPrice(type) && tmpTile != null) {
            IntVector2 pos = tmpTile.getPosition();
            if (pos.x + 1 >= 0 && pos.y - 1 >= 0 && pos.x + 1 < state.getBoardSizeX() && pos.y - 1 < state.getBoardSizeY()) {
                controller.placeTower(pos.x + 1, pos.y - 1, type);
                ++counter;
            }
            tmpTile = tmpTile.getNext();
        }
    }
}