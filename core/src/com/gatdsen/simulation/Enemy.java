package com.gatdsen.simulation;

import com.gatdsen.simulation.action.*;
import com.gatdsen.simulation.enemy.*;

import java.io.Serializable;
import java.util.List;

/**
 * Die Klasse Enemy repräsentiert einen Gegner im Spiel.
 */
public abstract class Enemy implements Serializable {

    protected final int team;

    public enum EnemyType {
        BASIC_ENEMY,
        EMP_ENEMY,
        SHIELD_ENEMY,
        SHIELD_MOUSE, ARMOR_ENEMY
    }

    protected final PlayerState playerState;

    private static int idCounter = 0;

    protected final int id;
    protected int health;
    protected int level;
    protected int damage;
    protected PathTile posTile;
    protected EnemyType enemyType;

    /**
     * Erstellt einen neuen Gegner.
     *
     * @param level   Die Stufe des Gegners.
     * @param posTile Die Position des Gegners.
     */
    public Enemy(PlayerState playerState, int level, PathTile posTile) {
        id = idCounter++;
        this.playerState = playerState;
        this.team = playerState.getIndex();
        this.posTile = posTile;
        this.level = level;
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

        if (enemyType == EnemyType.ARMOR_ENEMY) {
            damage = (int) (damage * 0.5);
        }
        if (health - damage <= 0) {
            health = 0;
            posTile.getEnemies().remove(this);

            /*
            chaining:
            | -> head -> update health -> enemy defeat -> |
             */
            Action updateHealthAction = new EnemyUpdateHealthAction(0, posTile.getPosition(), 0, level, team, enemyType, id);
            head.addChild(updateHealthAction);
            updateHealthAction.addChild(new EnemyDefeatAction(0, posTile.getPosition(), level, team, enemyType, id));
            head = playerState.updateMoney(30 + (level-1) * 5, updateHealthAction);
        } else {
            health -= damage;
            head.addChild(new EnemyUpdateHealthAction(0, posTile.getPosition(), health, level, team, enemyType, id));
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
            head.addChild(new EnemyMoveAction(0, posTile.getPrev().getPosition(), posTile.getPosition(), level, team, enemyType, id));
        } else {
            posTile.getEnemies().remove(this);
            head = playerState.setHealth(damage, head);
            head.addChild(new EnemyDefeatAction(0, posTile.getPosition(), level, team, enemyType, id));
        }
        if (enemyType == EnemyType.EMP_ENEMY) {
            head = ((EmpEnemy) this).EMP(head);
        }

        return head;
    }

    protected static int getEnemyTypePrice(EnemyType enemyType, int level) {
        switch (enemyType) {
            case BASIC_ENEMY:
                return BasicEnemy.getPrice(level);
            case EMP_ENEMY:
                return EmpEnemy.getPrice(level);
            case SHIELD_ENEMY:
                return ShieldEnemy.getPrice(level);
            case ARMOR_ENEMY:
                return ArmorEnemy.getPrice(level);
            default:
                return 0;
        }
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

    /**
     * Gibt die umliegenden Tiles in einer bestimmten Reichweite zurück
     *
     * @param range Reichweite um das Tile herum
     * @param board Map auf der nachgeschaut wird
     * @return Liste der umliegenden Tiles
     */
    protected List<Tile> getNeighbours(int range, Tile[][] board) {
        return Tile.getNeighbours(range, posTile.getPosition(), board);
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }
}
