package com.gatdsen.simulation;

import com.gatdsen.simulation.action.Action;
import com.gatdsen.simulation.action.ProjectileAction;
import com.gatdsen.simulation.action.TowerAttackAction;
import com.gatdsen.simulation.tower.BasicTower;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

/**
 * Speichert einen Tower. Beinhalet Methoden zum ausführen von Tower-Aktionen.
 */
public abstract class Tower {

    // ToDo: lookup table for upgraded values

    /**
     * Speichert die verschiedenen Typen von Türmen
     */
    public enum TowerType {
        BASIC_TOWER,
        AOE_TOWER,
        SNIPER_TOWER
    }

    static final int MAX_LEVEL = 3;

    private final PlayerState playerState;
    private final TowerType type;
    private final IntVector2 pos;

    protected static int idCounter = 0;
    protected final int id;
    private final List<PathTile> pathInRange = new ArrayList<>();
    private List<Tile> inRange;
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
        this.cooldown = getRechargeTime();
        this.inRange = getNeighbours(getRange(), board);
        setPathList();
        pathInRange.sort(Comparator.comparingInt(PathTile::getIndex));
    }

    /**
     * Erstellt eine Kopie eines Towers.
     *
     * @param original der zu kopierende Tower
     */
    protected Tower(Tower original) {
        this(original.playerState, original.type, original.pos.x, original.pos.y, null);
        this.level = original.level;
        this.cooldown = original.cooldown;
        this.inRange = original.inRange;
    }

    /**
     * Erstellt eine Kopie des Towers.
     *
     * @return eine Kopie des Towers
     */
    protected abstract Tower copy();

    /**
     * Gibt die umliegenden Tiles in einer bestimmten Reichweite zurück
     *
     * @param range Reichweite um das Tile herum
     * @param board Map auf der nachgeschaut wird
     * @return Liste der umliegenden Tiles
     */
    private List<Tile> getNeighbours(int range, Tile[][] board) {
        int diameter = (range * 2) + 1;
        List<Tile> neighbours = new ArrayList<>(diameter * diameter - 1);
        IntRectangle rec = new IntRectangle(0,0, board.length-1, board[0].length-1);
        for (int i = 0; i < diameter; i++) {
            for (int j = 0; j < diameter; j++) {
                if (rec.contains(pos.x - range + i, pos.y -range + j)) {
                    neighbours.add(board[pos.x - range + i][pos.y - range + j]);
                }

            }
        }
        return neighbours;
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

    public int getId() {
        return id;
    }


    /**
     * Gibt den Preis für ein Upgrade des Towers zurück
     *
     * @return Preis für ein Upgrade des Towers
     */
    public int getUpgradePrice() {
        return getPrice();
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
     * Gibt den Preis des Towers zurück
     *
     * @return Preis des Towers
     */
    public abstract int getPrice();

    /**
     * Upgraded den Tower
     */
    void upgrade() {
        // ToDo: implement upgrade after christmas task
        ++level;
    }

    /**
     * Führt einen Angriff aus, wenn möglich.
     *
     * @param head Kopf der Action-Liste
     * @return neuer Kopf der Action-Liste
     */
    public Action attack(Action head) {
        if (pathInRange.isEmpty()) {
            return head;
        }

        if (getRechargeTime() > 0) {
            --cooldown;
            return head;
        }

        int lastIndex = pathInRange.size() - 1;

        Enemy target = null;

        for (int i = lastIndex; i >= 0; i--) {
            if (!pathInRange.get(i).getEnemies().isEmpty()) {
                target = pathInRange.get(i).getEnemies().get(0);
                break;
            }
        }
        if (target != null) {
            head.addChild(new TowerAttackAction(0, pos, target.getPosition(), type.ordinal(), playerState.getIndex(), id));
            Path path = new LinearPath(pos.toFloat(), target.getPosition().toFloat(), 1);
            path.setDuration(0);
            head.addChild(new ProjectileAction(0, ProjectileAction.ProjectileType.STANDARD_TYPE, path, playerState.getIndex()));

            head = target.updateHealth(getDamage(), head);
            cooldown = getRechargeTime();
        }
        return head;
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
