package game.collectible;

import game.character.Player;

/**
 * Anything the player can pick up (elemental crystals, bonus crystals).
 * Concrete types: {@link ElementalCrystal}, {@link BonusCrystal}.
 */
public interface Collectible {

    /**
     * Called when a player steps onto this collectible.
     * Marks it as collected and may trigger score or gate-unlock logic
     * depending on the subtype.
     *
     * @param player the player who picked this up
     */
    void collect(Player player);

    /** How many points this collectible awards when picked up. */
    int getPointValue();

    /** Returns {@code true} if this collectible has already been picked up. */
    boolean isCollected();

    /** Column position on the board. */
    int getX();

    /** Row position on the board. */
    int getY();
}
