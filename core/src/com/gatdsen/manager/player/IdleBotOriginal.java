package com.gatdsen.manager.player;

import com.gatdsen.manager.Controller;
import com.gatdsen.manager.StaticGameState;
import com.gatdsen.simulation.*;
import com.gatdsen.simulation.Tower.TowerType;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * In dieser Klasse implementiert ihr euren Bot.
 */
public class IdleBotOriginal extends Bot {

    /**
     * Hier müsst ihr euren vollständigen Namen angeben
     *
     * @return Euer vollständiger Name im Format: "Vorname(n) Nachname"
     */
    @Override
    public String getStudentName() {
        return "Max Musterstudent";
    }

    /**
     * Hier müsst ihr eure Matrikelnummer angeben
     *
     * @return Eure Matrikelnummer
     */
    @Override
    public int getMatrikel() {
        return 42069;
    }

    /**
     * Hier könnt ihr eurem Bot einen (kreativen) Namen geben
     *
     * @return Der Name eures Bots
     */
    @Override
    public String getName() {
        return "ExamBot";
    }

    PathTile startTile;
    PathTile tmpTile;
    /**
     * Wird vor Beginn des Spiels aufgerufen. Die erlaubte Berechnungszeit für diese Methode beträgt 1 Sekunde.
     * Diese Methode kann daher verwendet werden, um Variablen zu initialisieren und einmalig, sehr rechenaufwändige
     * Operationen durchzuführen.
     *
     * @param state Der {@link StaticGameState Spielzustand} zu Beginn des Spiels
     */
    @Override
    public void init(StaticGameState state) {
        System.out.println("Der Bot \"" + getName() + "\" wurde initialisiert!");
        startTile = state.getMyPlayerState().getCheeseTile();
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
     * Der {@link StaticGameState Spielzustand} reflektiert dabei den Zustand des Spiels vor dem Zug des Spielers. Der
     * Zustand ist statisch, das heißt bei Aufrufen des {@link Controller Controllers} werden diese Änderungen nicht im
     * Spielzustand in dieser Runde reflektiert, sondern erst in der nächsten Runde, wenn man den neuen Spielzustand erhält.
     * <p>
     * Der Controller ermöglicht dir die Steuerung, um Aktionen, wie bspw. das Platzieren von Türmen, auszuführen. Die
     * übergebene Controller-Instanz deaktiviert sich nach Ende des Zuges permanent.
     *
     * @param state      Der {@link StaticGameState Spielzustand} vor der Ausführung des aktuellen Zuges
     * @param controller Der {@link Controller Controller}, um Aktionen auszuführen
     */
    @Override
    public void executeTurn(StaticGameState state, Controller controller) {
        TowerType type = TowerType.values()[counter % TowerType.values().length];
        if (state.getMyPlayerState().getMoney() >= Tower.getTowerPrice(type)) {
            IntVector2 pos = tmpTile.getPosition();
            controller.placeTower(pos.x + 1, pos.y - 1, type);
            ++counter;
            tmpTile = tmpTile.getNext();
        }
    }
}