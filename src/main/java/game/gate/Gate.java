package game.gate;

import game.character.Player;

/**
 * The level exit. Stays locked until the required crystals are collected.
 * Concrete types: {@link FireGate}, {@link WaterGate}.
 */
public interface Gate {

    /** Returns {@code true} once enough crystals have been collected to open this gate. */
    boolean isUnlocked();

    /** Opens the gate — called by the game loop when the crystal requirement is met. */
    void unlock();

    /**
     * Called when a player steps onto this gate's tile.
     * If the gate is unlocked, marks the appropriate character as having reached the exit.
     *
     * @param player the player who interacted with the gate
     */
    void interact(Player player);

    /** Column of the gate tile on the board. */
    int getX();

    /** Row of the gate tile on the board. */
    int getY();
}
