package game.util;

/**
 * The elemental affinity of a character, crystal, or projectile.
 * Used throughout the game to decide immunity/damage interactions:
 * FIRE is safe on lava but hurt by water, and vice versa for WATER.
 * EARTH and AIR are defined for extensibility but not used in the current levels.
 */
public enum ElementType {
    /** Fire element — immune to lava, damaged by water tiles. */
    FIRE,

    /** Water element — immune to water, damaged by lava tiles. */
    WATER,

    /** Earth element — reserved for future use. */
    EARTH,

    /** Air element — reserved for future use. */
    AIR
}
