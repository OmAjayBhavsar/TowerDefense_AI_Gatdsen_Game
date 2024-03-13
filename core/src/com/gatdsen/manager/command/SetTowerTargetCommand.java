package com.gatdsen.manager.command;

import com.gatdsen.manager.player.handler.PlayerHandler;
import com.gatdsen.simulation.Tower;
import com.gatdsen.simulation.action.ActionLog;

/**
 * Dieser Befehl ändert die Zieloption, nach welcher Priorität auf Gegner gezielt
 * werden soll (bspw. erster, stärkster, ...), eines Turms, der sich auf dem Spielfeld befindet.
 */
public class SetTowerTargetCommand extends TowerCommand {

    protected final Tower.TargetOption targetOption;

    /**
     * Erstellt einen neuen Befehl, der die Zieloption eines Turms ändert.
     * @param x x-Koordinate, an der der Turm platziert werden soll
     * @param y y-Koordinate, an der der Turm platziert werden soll
     * @param targetOption Zieloption, nach der der Turm zielen soll
     */
    public SetTowerTargetCommand(int x, int y, Tower.TargetOption targetOption) {
        super(x, y);
        this.targetOption = targetOption;
    }

    @Override
    protected ActionLog onExecute(PlayerHandler playerHandler) {
        return playerHandler.getPlayerController().setTarget(x, y, targetOption);
    }
}
