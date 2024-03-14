package com.gatdsen.manager.player.data;

import com.gatdsen.manager.player.Bot;
import com.gatdsen.manager.player.Player;

/**
 * Dieses Enum repräsentiert die verschiedenen Typen von Spielern.
 * <p>
 * Ursprünglich war dieser Enum, sowie die fromPlayer() Methode direkt Teil der {@link Player} Klassenhierarchie und mit
 * "Interne Methode" Kommentaren markiert. Um diese Logik aber besser verstecken zu können und die {@link Player}
 * Klassen nicht unnötig zu verkomplizieren, wurde dieser Enum und die Methode ausgelagert.
 */
public enum PlayerType {

    /** Ein Spieler, der von einem Menschen gesteuert wird */
    HUMAN,
    /** Ein Spieler, der von einem Bot gesteuert wird */
    BOT;

    /**
     * Gibt den {@link PlayerType} des gegebenen Spielers zurück.
     * @param player Der Spieler
     * @return Der {@link PlayerType} des Spielers
     */
    public static PlayerType fromPlayer(Player player) {
        if (player instanceof Bot) {
            return BOT;
        }
        return HUMAN;
    }
}
