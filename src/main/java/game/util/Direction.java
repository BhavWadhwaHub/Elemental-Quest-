package game.util;

/**
 * The four cardinal directions a character or projectile can face or move.
 * Each constant carries the tile-offset vector so callers can compute
 * a new position without a switch statement.
 */
public enum Direction {

    /** One tile upward — decreases the Y coordinate. */
    UP(0, -1),

    /** One tile downward — increases the Y coordinate. */
    DOWN(0, 1),

    /** One tile to the left — decreases the X coordinate. */
    LEFT(-1, 0),

    /** One tile to the right — increases the X coordinate. */
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /** Horizontal component of this direction (+1, 0, or -1). */
    public int getDx() { return dx; }

    /** Vertical component of this direction (+1, 0, or -1). */
    public int getDy() { return dy; }
}
