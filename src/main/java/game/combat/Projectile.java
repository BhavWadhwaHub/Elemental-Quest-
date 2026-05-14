package game.combat;

import game.util.Direction;
import game.util.ElementType;

/**
 * A fireball or waterball shot by a character.
 * Advances one tile every few game ticks until it hits something.
 */
public class Projectile {

    // how many ticks between each tile advance
    private static final int MOVE_INTERVAL = 6;

    private int x, y;
    private final Direction direction;
    private final ElementType elementType;
    private boolean active;
    private int tickCounter;

    public Projectile(int x, int y, Direction direction, ElementType elementType) {
        this.x           = x;
        this.y           = y;
        this.direction   = direction;
        this.elementType = elementType;
        this.active      = true;
        this.tickCounter = 0;
    }

    /**
     * Counts down to the next movement step.
     * @return true when it's time to advance this tick
     */
    public boolean tick() {
        if (!active) return false;
        tickCounter++;
        if (tickCounter >= MOVE_INTERVAL) {
            tickCounter = 0;
            return true;
        }
        return false;
    }

    /** Moves one tile forward in the projectile's direction. */
    public void advance() {
        x += direction.getDx();
        y += direction.getDy();
    }

    /** Marks the projectile as inactive so it gets cleaned up next frame. */
    public void deactivate() { active = false; }

    public int         getX()           { return x; }
    public int         getY()           { return y; }
    public Direction   getDirection()   { return direction; }
    public ElementType getElementType() { return elementType; }
    public boolean     isFireball()     { return elementType == ElementType.FIRE; }
    public boolean     isActive()       { return active; }
}
