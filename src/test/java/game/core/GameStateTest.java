package game.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Documents every state the game loop can be in (used by {@code GamePanel} and rendering).
 */
class GameStateTest {

    /** All five game-loop states are present in the enum. */
    @Test
    void allStatesExist() {
        GameState[] values = GameState.values();
        assertEquals(5, values.length);
    }

    /** {@link GameState#valueOf(String)} resolves each constant name. */
    @Test
    void valueOf_roundTrip() {
        assertSame(GameState.MENU, GameState.valueOf("MENU"));
        assertSame(GameState.PLAYING, GameState.valueOf("PLAYING"));
        assertSame(GameState.PAUSED, GameState.valueOf("PAUSED"));
        assertSame(GameState.LEVEL_COMPLETE, GameState.valueOf("LEVEL_COMPLETE"));
        assertSame(GameState.GAME_OVER, GameState.valueOf("GAME_OVER"));
    }

    /** Invalid enum name throws {@link IllegalArgumentException}. */
    @Test
    void valueOf_invalidName_throws() {
        assertThrows(IllegalArgumentException.class, () -> GameState.valueOf("NOT_A_STATE"));
    }
}
