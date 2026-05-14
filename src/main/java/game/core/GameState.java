package game.core;

/**
 * All the high-level states the game can be in at any given moment.
 * The game loop and rendering code branch on this value to decide what
 * to update and what to draw.
 */
public enum GameState {

    /** The main menu is being shown — no game is running yet. */
    MENU,

    /** A level is actively running: timer ticking, enemies moving, input live. */
    PLAYING,

    /** The player hit ESC — timer frozen, pause overlay visible. */
    PAUSED,

    /** All required crystals collected and both characters reached their gates. */
    LEVEL_COMPLETE,

    /** The player ran out of lives or time — game over screen showing. */
    GAME_OVER
}
