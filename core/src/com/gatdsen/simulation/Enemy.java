package com.gatdsen.simulation;

import com.gatdsen.simulation.action.*;

import java.io.Serializable;

/**
 * Die Klasse Enemy repräsentiert einen Gegner im Spiel.
 */
public abstract class Enemy implements Serializable {
    protected final PlayerState playerState;

    private static int idCounter = 0;

    protected final int id = idCounter++;
    protected int health;
    protected int level;
    protected int damage;
    protected PathTile posTile;

    /**
     * Erstellt einen neuen Gegner.
     * @param level   Die Stufe des Gegners.
     * @param posTile Die Position des Gegners.
     */
    public Enemy(PlayerState playerState, int level, PathTile posTile) {
        this.playerState = playerState;
        // IdCounter wird zurückgesetzt, wenn Integer.MAX_VALUE erreicht wird.
        if (idCounter == Integer.MAX_VALUE) {
            idCounter = 0;
        }
    }

    /**
     * Erstellt eine Kopie des Gegners.
     *
     * @param posTile Die Position des Gegners.
     * @return Die Kopie des Gegners.
     */
    protected abstract Enemy copy(PathTile posTile);

    /**
     * Fügt dem Gegner Schaden zu.
     *
     * @param damage Der Schaden, der dem Gegner zugefügt wird.
     * @param head   Die vorrausgehende Action.
     * @return Die letzte Action.
     */
    protected Action updateHealth(int damage, Action head) {
        if (health - damage <= 0) {
            health = 0;
            posTile.getEnemies().remove(this);

            /*
            chaining:
            | -> head -> update health -> enemy defeat -> |
             */
            Action updateHealthAction = new EnemyUpdateHealthAction(0, posTile.getPosition(), 0, level, playerState.getIndex(), id);
            head.addChild(updateHealthAction);
            updateHealthAction.addChild(new EnemyDefeatAction(0, posTile.getPosition(), level, playerState.getIndex(), id));
            head = playerState.updateMoney(30 * level, updateHealthAction);
        } else {
            health -= damage;
            head.addChild(new EnemyUpdateHealthAction(0, posTile.getPosition(), health, level, playerState.getIndex(), id));
        }
        return head;
    }

    /**
     * Bewegt den Gegner auf dem Spielfeld. Falls der Gegner das Ende des Pfades erreicht,
     * verliert der Spieler Lebenspunkte.
     *
     * @param head Die vorrausgehende Action
     * @return Die letzte Action
     */
    protected Action move(Action head) {
        if (posTile.getNext() != null) {
            posTile.getEnemies().remove(this);
            posTile = posTile.getNext();
            posTile.getEnemies().add(this);
            head.addChild(new EnemyMoveAction(0, posTile.getPrev().getPosition(), posTile.getPosition(), level, playerState.getIndex(), id));
        } else {
            posTile.getEnemies().remove(this);
            head = playerState.setHealth(damage, head);
            head.addChild(new EnemyDefeatAction(0, posTile.getPosition(), level, playerState.getIndex(), id));
        }
        return head;
    }

    /**
     * @return Die Lebenspunkte des Gegners.
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return Die Stufe des Gegners.
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return Die Position des Gegners als IntVector2.
     */
    public IntVector2 getPosition() {
        return new IntVector2(posTile.getPosition());
    }

    public int getId() {
        return id;
    }
}
