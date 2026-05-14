package game.util;

/**
 * Colour variants for bonus gems scattered around the board.
 * The type controls which tint/sprite is used when rendering the gem.
 * All types share the same point value — they're purely cosmetic distinctions.
 */
public enum GemType {
    /** Classic ruby-red gem. */
    RED,

    /** Cool sapphire-blue gem. */
    BLUE,

    /** Vibrant emerald-green gem. */
    GREEN,

    /** Shiny gold/topaz gem. */
    YELLOW,

    /** Pearlescent white/diamond gem. */
    WHITE
}
