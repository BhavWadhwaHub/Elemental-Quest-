package game.enemy;

import game.level.Board;
import game.level.Entity;

/**
 * Common contract for all hostile entities on the board.
 * Enemies extend {@link Entity} and additionally know how to move
 * on their own and whether they're still alive and threatening.
 */
public interface Enemy extends Entity {

    /**
     * Returns {@code true} while this enemy is alive and should be
     * updated and rendered. Inactive enemies can safely be ignored
     * by the game loop.
     */
    boolean isActive();

    /**
     * Advances the enemy by one game tick — move logic, AI, whatever
     * the concrete class decides to do each step.
     *
     * @param board the current board, used for collision checks
     */
    void move(Board board);
}
