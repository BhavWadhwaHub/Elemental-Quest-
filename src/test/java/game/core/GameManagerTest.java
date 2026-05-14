package game.core;

import game.ui.GameWindow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises {@link GameManager} startup helpers without {@code SwingUtilities.invokeAndWait} / {@code invokeLater}.
 */
class GameManagerTest {

    /** {@link GameManager#initLookAndFeel()} completes without throwing. */
    @Test
    void initLookAndFeel_doesNotThrow() {
        assertDoesNotThrow(GameManager::initLookAndFeel);
    }

    /** Opens a non-visible {@link GameWindow} for disposal-based cleanup in tests. */
    @Test
    void openGameWindow_notVisible_canDispose() {
        GameWindow w = GameManager.openGameWindow(false);
        assertNotNull(w);
        assertFalse(w.isVisible());
        w.dispose();
    }
}
