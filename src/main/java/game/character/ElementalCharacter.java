package game.character;

import game.enemy.ShadowSentinel;
import game.level.Board;
import game.level.Cell;
import game.level.Entity;
import game.util.Direction;
import game.util.ElementType;

/**
 * Base class for Ember and Aqua.
 * Handles movement, terrain interaction, and ability charge tracking.
 * Subclasses implement their own special ability logic.
 */
public abstract class ElementalCharacter implements Entity {

    protected int x;
    protected int y;
    protected ElementType elementType;
    protected boolean isActive;
    protected int abilityCharges;
    protected int maxAbilityCharges;

    /**
     * Initialises the character at the given tile with three ability charges
     * and the {@code isActive} flag set to {@code false} — the caller should
     * call {@link #setActive(boolean)} on the character that starts first.
     *
     * @param x           starting column
     * @param y           starting row
     * @param elementType FIRE or WATER elemental affinity
     */
    public ElementalCharacter(int x, int y, ElementType elementType) {
        this.x = x;
        this.y = y;
        this.elementType      = elementType;
        this.isActive         = false;
        this.abilityCharges   = 3;
        this.maxAbilityCharges = 3;
    }

    /** Special ability — each subclass defines what it does. */
    public abstract void useAbility(Board board, Direction direction);

    /**
     * Returns true if this character can pass through the given cell without damage.
     * Fire can walk on lava, water can walk on water.
     */
    public boolean canInteractWith(Cell cell) {
        if (elementType == ElementType.FIRE  && cell.isLava())  return true;
        if (elementType == ElementType.WATER && cell.isWater()) return true;
        return false;
    }

    /**
     * Returns true if stepping on this cell would hurt the character.
     * Traps hurt everyone; the opposing element hurts the matching character.
     */
    public boolean takesHazardDamage(Cell cell) {
        if (cell.isTrap()) return true;
        if (elementType == ElementType.FIRE  && cell.isWater()) return true;
        if (elementType == ElementType.WATER && cell.isLava())  return true;
        return false;
    }

    /** Returns true if the only entity on this cell is a Shadow Sentinel. */
    protected boolean isSentinelOnly(Cell cell) {
        return cell.getEntity() instanceof ShadowSentinel;
    }

    /** Attempts to move one tile in the given direction. Returns true on success. */
    public boolean move(Board board, Direction direction) {
        int newX = x + direction.getDx();
        int newY = y + direction.getDy();
        if (board.isValidMove(newX, newY)) {
            x = newX;
            y = newY;
            return true;
        }
        return false;
    }

    /** Current column on the board. */
    public int  getX()                       { return x; }

    /** Current row on the board. */
    public int  getY()                       { return y; }

    /** Teleports the character to a specific tile — bypasses collision checks. */
    public void setPosition(int x, int y)    { this.x = x; this.y = y; }

    /** Returns FIRE or WATER — used to decide damage/immunity rules. */
    public ElementType getElementType()      { return elementType; }

    /** Returns {@code true} if this character is currently accepting player input. */
    public boolean isActive()                { return isActive; }

    /** Marks whether this character should respond to input (only one active at a time in 1P). */
    public void    setActive(boolean active) { isActive = active; }

    /** How many special ability uses the character has left (0–3). */
    public int getAbilityCharges()           { return abilityCharges; }

    /** Restores one ability charge, up to the maximum. */
    public void rechargeAbility() {
        if (abilityCharges < maxAbilityCharges)
            abilityCharges++;
    }
}
