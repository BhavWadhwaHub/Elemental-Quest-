package game.level;

/**
 * One tile on the game board. A cell can be a wall, a hazard (lava, water, trap),
 * and can hold an entity (character, enemy, collectible, gate).
 * Hazard flags are mutually exclusive — only one hazard per tile.
 */
public class Cell {

    private int x;
    private int y;
    private boolean wall;
    private boolean lava;
    private boolean water;
    private boolean trap;
    private Entity entity;

    /**
     * Creates a blank, passable floor tile at the given grid coordinates.
     * All hazard flags start as {@code false} and no entity is placed here yet.
     *
     * @param x column index of this tile on the board
     * @param y row index of this tile on the board
     */
    public Cell(int x, int y) {
        this.x      = x;
        this.y      = y;
        this.wall   = false;
        this.lava   = false;
        this.water  = false;
        this.trap   = false;
        this.entity = null;
    }

    // ── State queries ─────────────────────────────────────────────────────────

    public boolean isWall()  { return wall; }
    public boolean isLava()  { return lava; }
    public boolean isWater() { return water; }
    public boolean isTrap()  { return trap; }

    /** Returns true if there is no hazard and no entity on this tile. */
    public boolean isEmpty() { return !wall && !lava && !water && !trap && entity == null; }

    /** Returns true if the tile can be walked on (no wall). */
    public boolean isWalkable() { return !wall; }

    public int    getX()       { return x; }
    public int    getY()       { return y; }
    public Entity getEntity()  { return entity; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** Marks or un-marks this tile as a solid wall (impassable by all). */
    public void setWall (boolean wall)   { this.wall  = wall; }

    /** Marks or un-marks this tile as a lava pool (damages Aqua, safe for Ember). */
    public void setLava (boolean lava)   { this.lava  = lava; }

    /** Marks or un-marks this tile as a water pool (damages Ember, safe for Aqua). */
    public void setWater(boolean water)  { this.water = water; }

    /** Marks or un-marks this tile as a spike trap (damages everyone on contact). */
    public void setTrap (boolean trap)   { this.trap  = trap; }

    /** Places an entity on this tile, replacing any previous occupant reference. */
    public void setEntity(Entity entity) { this.entity = entity; }

    /** Removes any entity from this tile. */
    public void clear() { entity = null; }
}
