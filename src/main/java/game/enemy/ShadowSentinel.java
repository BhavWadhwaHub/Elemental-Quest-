package game.enemy;

import game.character.ElementalCharacter;
import game.level.Board;
import game.level.Cell;
import game.level.Entity;
import game.util.Direction;
import java.util.Random;

/**
 * A patrolling enemy that moves around the board and damages
 * any character it runs into. Changes direction randomly when blocked.
 */
public class ShadowSentinel implements Entity, Hazard {

    private int x;
    private int y;
    private int damage;
    private Direction currentDirection;
    private int moveCounter;
    private int moveDelay;
    private Random random;
    private boolean active;
    private int detectionRange;

    /**
     * Creates a sentinel at the given position with the specified damage.
     *
     * @param x starting x tile
     * @param y starting y tile
     * @param damage how much damage it deals on contact
     */
    public ShadowSentinel(int x, int y, int damage) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.random = new Random();
        this.currentDirection = Direction.values()[random.nextInt(4)];
        this.moveCounter = 0;
        this.moveDelay = 20; // 20 ticks × 16 ms ≈ 320 ms per step
        this.active = true;
        this.detectionRange = 5;
    }

    /** Creates a sentinel with the default damage of 1. */
    public ShadowSentinel(int x, int y) {
        this(x, y, 1);
    }

    /**
     * Tries to move one step forward each time the delay ticks over.
     * Picks a new random direction if the current path is blocked.
     */
    public void move(Board board) {
        moveCounter++;
        if (moveCounter < moveDelay) return;
        moveCounter = 0;

        int newX = x + currentDirection.getDx();
        int newY = y + currentDirection.getDy();
        Cell cell = board.getCell(newX, newY);

        if (board.isValidMove(newX, newY)
                && cell != null
                && !cell.isLava()
                && !cell.isWater()) {
            // Move to the new tile and update the cell references
            Cell oldCell = board.getCell(x, y);
            if (oldCell != null) oldCell.clear();
            x = newX;
            y = newY;
            cell.setEntity(this);
        } else {
            // Blocked — pick a new direction next time
            currentDirection = Direction.values()[random.nextInt(4)];
        }
    }

    /**
     * Returns true if this sentinel is on the same tile as the given character.
     */
    @Override
    public boolean interactWith(ElementalCharacter character) {
        return character.getX() == x && character.getY() == y;
    }

    @Override public int    getDamage()                  { return damage; }
    @Override public int    getX()                       { return x; }
    @Override public int    getY()                       { return y; }
    @Override public void   setPosition(int x, int y)   { this.x = x; this.y = y; }
    @Override public String getType()                    { return "SHADOW_SENTINEL"; }

    /** Returns {@code true} while this sentinel is alive and should be updated. */
    public boolean isActive()                            { return active; }

    /** Marks the sentinel as active or inactive (e.g. when killed by a projectile). */
    public void    setActive(boolean active)             { this.active = active; }

    /** How many tiles away this sentinel can "sense" a player (currently informational only). */
    public int     getDetectionRange()                   { return detectionRange; }

    /** Adjusts the detection range — higher values could enable chase AI in the future. */
    public void    setDetectionRange(int range)          { this.detectionRange = range; }
}
