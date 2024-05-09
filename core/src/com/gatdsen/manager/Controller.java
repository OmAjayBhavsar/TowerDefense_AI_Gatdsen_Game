package com.gatdsen.manager;

import com.gatdsen.manager.command.*;
import com.gatdsen.simulation.Enemy;
import com.gatdsen.simulation.Tower;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Ermöglicht die Kontrolle eines bestimmten Charakters.
 * Ist nur für einen einzelnen Zug gültig und deaktiviert sich nach Ende des aktuellen Zuges.
 */
public final class Controller {

    final BlockingQueue<Command> commands = new ArrayBlockingQueue<>(256);
    private int uses;

    Controller(int uses) {
        this.uses = uses;
    }

    /**
     * Die Zahl an Nutzungen ist für Bots auf 200 beschränkt.
     * Die maximale Zahl sinnvoller Züge beträgt ca. 70
     *
     * @return Die Menge an Befehlen, die dieser Controller noch ausführen kann
     */
    public int getRemainingUses() {
        return uses;
    }

    /**
     * Platziert einen neuen Turm auf dem Spielfeld.
     * @param x x-Koordinate, an der der Turm platziert werden soll
     * @param y y-Koordinate, an der der Turm platziert werden soll
     * @param type Typ des Turms, der platziert werden soll
     */
    public void placeTower(int x, int y, Tower.TowerType type) {
        queue(new PlaceTowerCommand(x, y, type));
    }

    /**
     * Verbessert einen Turm auf dem Spielfeld.
     * @param x x-Koordinate, an der sich der Turm befindet
     * @param y y-Koordinate, an der sich der Turm befindet
     */
    public void upgradeTower(int x, int y) {
        queue(new UpgradeTowerCommand(x, y));
    }

    /**
     * Verkauft einen Turm auf dem Spielfeld.
     * @param x x-Koordinate, an der sich der Turm befindet
     * @param y y-Koordinate, an der sich der Turm befindet
     */
    public void sellTower(int x, int y) {
        queue(new SellTowerCommand(x, y));
    }

    /**
     * Ändert die Zieloption, nach welcher Priorität auf Gegner gezielt werden soll (bspw. erster, stärkster, ...),
     * eines Turms, der sich auf dem Spielfeld befindet.
     * @param x x-Koordinate, an der sich der Turm befindet
     * @param y y-Koordinate, an der sich der Turm befindet
     * @param targetOption Zieloption, nach der der Turm zielen soll
     */
    public void setTowerTarget(int x, int y, Tower.TargetOption targetOption) {
        queue(new SetTowerTargetCommand(x, y, targetOption));
    }

    /**
     * Sendet einen Gegner zu einem gegnerischen Spieler auf das Spielfeld.
     * @param enemyType Der Typ des Gegners, der gesendet werden soll
     */
    public void sendEnemyToPlayer(Enemy.EnemyType enemyType) {
        queue(new SendEnemyToPlayerCommand(enemyType));
    }

    /**
     * Interne Methode: <br>
     * Kontrolliert die verbleibenden Nutzungen und fügt die {@link Command}s der Warteschlange zur Weiterverarbeitung
     * hinzu
     * @param command Der Befehl, der zur Warteschlange hinzugefügt werden soll
     */
    private void queue(Command command) {
        if (isActive()) {
            commands.add(command);
            uses--;
        }
    }

    /**
     * Interne Methode: <br>
     * Markiert das Ende des aktuellen Zuges für diesen Controller und deaktiviert diesen, sodass keine weiteren
     * {@link Command}s mehr ausgeführt werden können.
     */
    void endTurn() {
        if (isActive()) {
            commands.add(new EndTurnCommand());
            deactivate();
        }
    }

    /**
     * Interne Methode: <br>
     * Gibt an, ob dieser Controller noch aktiv ist.
     * @return true, wenn dieser Controller aktiv ist, sonst false
     */
    private boolean isActive() {
        return uses != -1;
    }

    /**
     * Interne Methode: <br>
     * Deaktiviert diesen Controller, sodass keine weiteren {@link Command}s mehr ausgeführt werden können.
     */
    private void deactivate() {
        uses = -1;
    }
}
