package game.level;

import game.collectible.Collectible;
import game.enemy.*;
import game.gate.FireGate;
import game.gate.WaterGate;
import java.util.ArrayList;
import java.util.List;

/**
 * The game board — a 2D grid of cells that holds everything:
 * terrain, entities, collectibles, enemies, and the exit gates.
 * Think of it as the single source of truth for what's at each tile.
 */
public class Board {

    private int width;
    private int height;
    private Cell[][] cells;
    private List<Entity> entities;
    private List<ShadowSentinel> enemies;
    private List<Collectible> collectibles;
    private FireGate fireGate;
    private WaterGate waterGate;

    /**
     * Creates an empty board of the given dimensions, filling every tile
     * with a blank passable floor cell.
     *
     * @param width  number of columns
     * @param height number of rows
     */
    public Board(int width, int height) {
        this.width  = width;
        this.height = height;
        this.cells        = new Cell[width][height];
        this.entities     = new ArrayList<>();
        this.enemies      = new ArrayList<>();
        this.collectibles = new ArrayList<>();

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                cells[x][y] = new Cell(x, y);
    }

    /** Adds an entity and places it in its cell. */
    public void addEntity(Entity entity) {
        entities.add(entity);
        if (isValidPosition(entity.getX(), entity.getY()))
            cells[entity.getX()][entity.getY()].setEntity(entity);
    }

    /** Removes an entity and clears its cell. */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
        if (isValidPosition(entity.getX(), entity.getY()))
            cells[entity.getX()][entity.getY()].clear();
    }

    /** Returns the entity at (x, y), or null if the tile is empty. */
    public Entity getEntityAt(int x, int y) {
        return isValidPosition(x, y) ? cells[x][y].getEntity() : null;
    }

    /** Returns true if (x, y) is in bounds and walkable. */
    public boolean isValidMove(int x, int y) {
        return isValidPosition(x, y) && cells[x][y].isWalkable();
    }

    /** Returns true if (x, y) is within the grid boundaries. */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /** Returns the cell at (x, y), or null if out of bounds. */
    public Cell getCell(int x, int y) {
        return isValidPosition(x, y) ? cells[x][y] : null;
    }

    /** Turns cell (x, y) into a solid wall. Out-of-bounds positions are silently ignored. */
    public void setWall(int x, int y)  { if (isValidPosition(x, y)) cells[x][y].setWall(true); }

    /** Places lava on cell (x, y). Out-of-bounds positions are silently ignored. */
    public void setLava(int x, int y)  { if (isValidPosition(x, y)) cells[x][y].setLava(true); }

    /** Floods cell (x, y) with water. Out-of-bounds positions are silently ignored. */
    public void setWater(int x, int y) { if (isValidPosition(x, y)) cells[x][y].setWater(true); }

    /** Places a spike trap on cell (x, y). Out-of-bounds positions are silently ignored. */
    public void setTrap(int x, int y)  { if (isValidPosition(x, y)) cells[x][y].setTrap(true); }

    /** Adds a sentinel to the enemy list and registers it as an entity. */
    public void addEnemy(ShadowSentinel enemy) {
        enemies.add(enemy);
        addEntity(enemy);
    }

    /** Removes a sentinel from the board entirely. */
    public void removeEnemy(ShadowSentinel enemy) {
        enemies.remove(enemy);
        removeEntity(enemy);
    }

    /** Ticks all enemies forward by one step. */
    public void updateEnemies() {
        for (ShadowSentinel enemy : enemies)
            enemy.move(this);
    }

    /** Adds a collectible and registers it as an entity if needed. */
    public void addCollectible(Collectible collectible) {
        collectibles.add(collectible);
        if (collectible instanceof Entity)
            addEntity((Entity) collectible);
    }

    /**
     * Detaches a collectible's entity reference without removing it from the list.
     * Used when iterating — the caller removes the item from the list themselves.
     */
    public void detachCollectibleEntity(Collectible c) {
        if (c instanceof Entity) {
            entities.remove((Entity) c);
            Cell cell = getCell(((Entity) c).getX(), ((Entity) c).getY());
            if (cell != null) cell.clear();
        }
    }

    /** Fully removes a collectible from the board. */
    public void removeCollectible(Collectible collectible) {
        collectibles.remove(collectible);
        if (collectible instanceof Entity)
            removeEntity((Entity) collectible);
    }

    /** Places the fire exit gate on the board and registers it as an entity. */
    public void setFireGate(FireGate gate)   { this.fireGate  = gate;  addEntity(gate); }

    /** Places the water exit gate on the board and registers it as an entity. */
    public void setWaterGate(WaterGate gate) { this.waterGate = gate;  addEntity(gate); }

    /** Removes the fire gate from the board entirely. Safe to call if the gate is already absent. */
    public void removeFireGate()  { if (fireGate  != null) { removeEntity(fireGate);  fireGate  = null; } }

    /** Removes the water gate from the board entirely. Safe to call if the gate is already absent. */
    public void removeWaterGate() { if (waterGate != null) { removeEntity(waterGate); waterGate = null; } }

    /** Returns the fire exit gate, or {@code null} if one hasn't been placed yet. */
    public FireGate             getFireGate()     { return fireGate; }

    /** Returns the water exit gate, or {@code null} if one hasn't been placed yet. */
    public WaterGate            getWaterGate()    { return waterGate; }

    /** Returns the live enemy list — modifying it directly affects game state. */
    public List<ShadowSentinel> getEnemies()      { return enemies; }

    /** Returns the live collectible list. */
    public List<Collectible>    getCollectibles() { return collectibles; }

    /** Returns all entities currently registered on the board. */
    public List<Entity>         getEntities()     { return entities; }

    /** Returns the raw 2D cell grid (column-major: {@code cells[x][y]}). */
    public Cell[][]             getCells()        { return cells; }

    /** Number of columns in the grid. */
    public int                  getWidth()        { return width; }

    /** Number of rows in the grid. */
    public int                  getHeight()       { return height; }
}
