package com.gatdsen.simulation;

import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ProjectileAction;
import com.gatdsen.simulation.action.TowerAttackAction;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Speichert einen Tower. Beinhalet Methoden zum ausführen von Tower-Aktionen.
 */
public abstract class Tower implements Serializable {

    // ToDo: lookup table for upgraded values

    /**
     * Speichert die verschiedenen Typen von Türmen
     */
    public enum TowerType {
        MINIGUN_CAT,
        CATANA_CAT,
        MAGE_CAT
    }

    /**
     * Speichert die verschiedenen Optionen, wie ein Tower sein Ziel auswählen kann
     */
    public enum TargetOption {
        FIRST,
        LAST,
        STRONGEST,
        WEAKEST
    }

    static final int MAX_LEVEL = 3;

    protected final PlayerState playerState;
    protected final TowerType type;
    protected final IntVector2 pos;

    protected TargetOption targetOption = TargetOption.FIRST;

    protected static int idCounter = 0;
    protected final int id;
    protected final List<PathTile> pathInRange = new ArrayList<>();
    protected List<Tile> inRange;
    protected int level;
    protected int cooldown;

    /**
     * Erstellt einen Tower an der angegebenen Position.
     *
     * @param playerState der PlayerState, zu dem der Tower gehört
     * @param type        der Typ des Towers
     * @param x           x-Koordinate
     * @param y           y-Koordinate
     * @param board       die Map, auf der der Tower steht
     */
    protected Tower(PlayerState playerState, TowerType type, int x, int y, Tile[][] board) {
        pos = new IntVector2(x, y);
        id = idCounter++;
        this.playerState = playerState;
        this.type = type;
        this.level = 1;
        this.cooldown = 0;
        this.inRange = getNeighbours(getRange(), board);
        setPathList();
        pathInRange.sort(Comparator.comparingInt(PathTile::getIndex));
    }

    /**
     * Erstellt eine Kopie eines Towers.
     *
     * @param original der zu kopierende Tower
     */
    protected Tower(Tower original, PlayerState playerState) {
        this.playerState = playerState;
        this.type = original.type;
        this.pos = original.pos;
        this.id = original.getId();
        this.level = original.level;
        this.cooldown = original.cooldown;
        this.inRange = original.inRange;
        this.targetOption = original.targetOption;
    }

    /**
     * Erstellt eine Kopie des Towers.
     *
     * @return eine Kopie des Towers
     */
    protected abstract Tower copy(PlayerState NewPlayerstate);

    /**
     * Gibt die umliegenden Tiles in einer bestimmten Reichweite zurück
     *
     * @param range Reichweite um das Tile herum
     * @param board Map auf der nachgeschaut wird
     * @return Liste der umliegenden Tiles
     */
    protected List<Tile> getNeighbours(int range, Tile[][] board) {
        return Tile.getNeighbours(range, pos, board);
    }

    /**
     * Setzt die Liste der Tiles, die in Reichweite des Towers sind.
     */
    private void setPathList() {
        for (Tile tile : inRange) {
            if (tile instanceof PathTile) {
                PathTile pathTile = (PathTile) tile;
                pathInRange.add(pathTile);
            }
        }
    }

    /**
     * Gibt die Liste der Tiles, die in Reichweite des Towers sind, zurück.
     *
     * @return Liste der Tiles, die in Reichweite des Towers sind
     */
    private List<PathTile> getPathInRange() {
        return pathInRange;
    }

    /**
     * Gibt den maximalen Level des Towers zurück
     *
     * @return maximaler Level des Towers
     */
    public static int getMaxLevel() {
        return MAX_LEVEL;
    }

    /**
     * Gibt den Typ des Towers zurück
     *
     * @return Typ des Towers
     */
    public TowerType getType() {
        return type;
    }

    /**
     * @return die ID des Towers
     */
    public int getId() {
        return id;
    }



    /**
     * Gibt den Level des Towers zurück
     *
     * @return Level des Towers
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gibt den Damage-Wert des Towers zurück
     *
     * @return Damage-Wert des Towers
     */
    public abstract int getDamage();

    /**
     * Gibt den Range-Wert des Towers zurück
     *
     * @return Range-Wert des Towers
     */
    public abstract int getRange();

    /**
     * Gibt den RechargeTime-Wert des Towers zurück
     *
     * @return RechargeTime-Wert des Towers
     */
    public abstract int getRechargeTime();

    /**
     * Erhöht die RechargeTime des Towers
     */
    public abstract void incrementRechargeTime();

    /**
     * Gibt den Preis des Towers zurück
     *
     * @param type Typ des Towers
     * @return Preis des Towers
     */
    public static int getTowerPrice(TowerType type) {
        switch (type) {
            case MINIGUN_CAT:
                return 80;
            case CATANA_CAT:
                return 100;
            case MAGE_CAT:
               return 100;
            default:
                return 0;
        }
    }

    /**
     * Gibt die Reichweite des Towers zurück
     *
     * @param type Typ des Towers
     * @return Reichweite des Towers
     */
    public static int getTowerRange(TowerType type) {
        switch (type) {
            case MINIGUN_CAT:
                return 2;
            case CATANA_CAT:
                return 1;
            case MAGE_CAT:
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Gibt den Preis des Towers zurück
     * @return Preis des Towers
     */
    public abstract int getUpgradePrice();

    /**
     * Upgraded den Tower
     */
    void upgrade() {
        ++level;
    }

    /**
     * @return den ersten Gegner in Reichweite
     */
    private Enemy getFirstEnemy() {
        for (int i = pathInRange.size() - 1; i >= 0; i--) {
            if (!pathInRange.get(i).getEnemies().isEmpty()) {
                return pathInRange.get(i).getEnemies().get(0);
            }
        }
        return null;
    }

    /**
     * @return den letzten Gegner in Reichweite
     */
    private Enemy getLastEnemy() {
        for (PathTile pathTile : pathInRange) {
            if (!pathTile.getEnemies().isEmpty()) {
                List<Enemy> enemies = pathTile.getEnemies();
                return enemies.get(enemies.size() - 1);
            }
        }
        return null;
    }

    /**
     * @return den stärksten Gegner in Reichweite
     */
    private Enemy getStrongestEnemy() {
        Enemy strongest = getFirstEnemy();
        if (strongest == null) {
            return null;
        }
        for (PathTile pathTile : pathInRange) {
            if (!pathTile.getEnemies().isEmpty()) {
                List<Enemy> enemies = pathTile.getEnemies();
                for (Enemy enemy : enemies) {
                    strongest = strongest.getHealth() < enemy.getHealth() ? enemy : strongest;
                }
            }
        }
        return strongest;
    }

    /**
     * @return den schwächsten Gegner in Reichweite
     */
    private Enemy getWeakestEnemy() {
        Enemy weakest = getFirstEnemy();
        if (weakest == null) {
            return null;
        }
        for (int i = pathInRange.size() - 1; i >= 0; i--) {
            if (!pathInRange.get(i).getEnemies().isEmpty()) {
                List<Enemy> enemies = pathInRange.get(i).getEnemies();
                for (Enemy enemy : enemies) {
                    weakest = weakest.getHealth() > enemy.getHealth() ? enemy : weakest;
                }
            }
        }
        return weakest;
    }

    /**
     * @return den Gegner, der vom Tower angegriffen werden soll
     */
    protected Enemy getTarget() {
        Enemy target = null;
        switch (targetOption) {
            case FIRST:
                target = getFirstEnemy();
                break;
            case LAST:
                target = getLastEnemy();
                break;
            case STRONGEST:
                target = getStrongestEnemy();
                break;
            case WEAKEST:
                target = getWeakestEnemy();
                break;
        }
        return target;
    }

    protected Action updateEnemyHealth(Enemy enemy, Action head) {
        return enemy.updateHealth(getDamage(), head);
    }

    /**
     * Führt einen Angriff aus, wenn möglich.
     *
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    protected Action attack(Action head) {
        if (pathInRange.isEmpty()) {
            return head;
        }

        if (cooldown > 0) {
            --cooldown;
            return head;
        }

        Enemy target = getTarget();

        if (target != null) {
            head.addChild(new TowerAttackAction(0, pos, target.getPosition(), type.ordinal(), playerState.getIndex(), id));
            if (type == TowerType.MAGE_CAT) {
                Path path = new LinearPath(pos.toFloat(), target.getPosition().toFloat(), 0.1f);
                path.setDuration(0.5f);
                head.addChild(new ProjectileAction(0, ProjectileAction.ProjectileType.STANDARD_TYPE, path, playerState.getIndex()));
            }
            head = updateEnemyHealth(target, head);
            cooldown = getRechargeTime();
        }
        return head;
    }

    public void setTargetOption(TargetOption targetOption) {
        this.targetOption = targetOption;
    }

    /**
     * Führt einen Tower-Tick aus.
     *
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    Action tick(Action head) {
        return attack(head);
    }
}
