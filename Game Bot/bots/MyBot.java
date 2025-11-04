package bots;

import com.gatdsen.manager.player.Bot;
import com.gatdsen.manager.Controller;
import com.gatdsen.manager.StaticGameState;
import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.Tower;

import java.util.Random;


/**
 * In dieser Klasse implementiert ihr euren Bot.
 */
public class MyBot extends Bot {

    /**
     * Hier müsst ihr euren vollständigen Namen angeben
     *
     * @return Euer vollständiger Name im Format: "Vorname(n) Nachname"
     */
    @Override
    public String getStudentName() {
        return "Om Ajay Bhavsar";
    }

    /**
     * Hier müsst ihr eure Matrikelnummer angeben
     *
     * @return Eure Matrikelnummer
     */
    @Override
    public int getMatrikel() {
        return 231781;
    }

    /**
     * Hier könnt ihr eurem Bot einen (kreativen) Namen geben
     *
     * @return Der Name eures Bots
     */
    @Override
    public String getName() {
        return "The_Predator";
    }

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
    }

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

    private Position firstTowerPosition;

    public MyBot() {
        this.firstTowerPosition = null;
    }

    @Override
    public void executeTurn(StaticGameState state, Controller controller) {
        System.out.println("Der Bot \"" + getName() + "\" ist am Zug in Runde " + state.getTurn() + "!");

        if (firstTowerPosition == null) {
            int atCenterX = state.getBoardSizeX() / 2;
            int atCenterY = state.getBoardSizeY() / 2;
            controller.placeTower(atCenterX, atCenterY, Tower.TowerType.MAGE_CAT);
            firstTowerPosition = new Position(atCenterX, atCenterY);

        } else {
            Position atNewPosition = getNewPosition(state, firstTowerPosition);
            Tower.TowerType towerType = chooseTowerType(state.getTurn());
            controller.placeTower(atNewPosition.x, atNewPosition.y, towerType);
        }

        if (state.getTurn() > 5 && state.getTurn() % 3 == 0) {
            controller.sendEnemyToPlayer(Enemy.EnemyType.EMP_ENEMY);
        } else if (state.getTurn() > 10 && state.getTurn() % 2 == 0) {
            controller.sendEnemyToPlayer(Enemy.EnemyType.EMP_ENEMY);
        } else {
            controller.sendEnemyToPlayer(Enemy.EnemyType.EMP_ENEMY);
        }

        if (state.getTurn() % 4 == 0) {
            for (int x = 0; x < state.getBoardSizeX(); x++) {
                for (int y = 0; y < state.getBoardSizeY(); y++) {
                    controller.upgradeTower(x, y);
                }
            }
        }
    }

    private Tower.TowerType chooseTowerType(int turn) {
        if (turn % 3 == 0) {
            return Tower.TowerType.MAGE_CAT;
        } else if (turn % 2 == 0) {
            return Tower.TowerType.CATANA_CAT;
        } else {
            return Tower.TowerType.MINIGUN_CAT;
        }
    }

    private static class Position {
        int x, y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private Position getNewPosition(StaticGameState state, Position baseTowerPosition) {
        int newX, newY;
        do {
            newX = baseTowerPosition.x + random.nextInt(5) - 2;
            newY = baseTowerPosition.y + random.nextInt(5) - 2;
        } while (newX < 0 || newX >= state.getBoardSizeX() || newY < 0 || newY >= state.getBoardSizeY());
        return new Position(newX, newY);
    }

}
