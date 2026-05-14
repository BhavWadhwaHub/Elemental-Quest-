package game.level;

/**
 * Anything that can occupy a tile on the game board: players, enemies,
 * collectibles, and gates all implement this interface so the board
 * can store and look them up in a uniform way.
 */
public interface Entity {

    /** Returns the entity's current column (x-axis tile coordinate). */
    int getX();

    /** Returns the entity's current row (y-axis tile coordinate). */
    int getY();

    /**
     * Teleports the entity to the given tile without any collision checks.
     * Use with care — most movement should go through {@link game.level.Board#isValidMove}.
     *
     * @param x new column
     * @param y new row
     */
    void setPosition(int x, int y);

    /**
     * A short identifier string used for debugging and sprite selection
     * (e.g. {@code "EMBER"}, {@code "SHADOW_SENTINEL"}, {@code "CRYSTAL_FIRE"}).
     */
    String getType();
}
