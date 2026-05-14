package game.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link GameWindow} screen transitions without Swing EDT helpers.
 */
class GameWindowNavigationIntegrationTest {

    /** Verifies {@link GameWindow#showMenu()} clears an active game panel after {@link GameWindow#startGame}. */
    @Test
    void startGame_thenShowMenu_clearsGamePanel() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "EMBER");
            assertTrue(w.hasActiveGamePanel());
            w.showMenu();
            assertFalse(w.hasActiveGamePanel());
        } finally {
            w.dispose();
        }
    }

    /** Verifies {@link GameWindow#showCharacterSelect()} runs without an active game panel. */
    @Test
    void showCharacterSelect_reachableFromWindow() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.showCharacterSelect();
            assertFalse(w.hasActiveGamePanel());
        } finally {
            w.dispose();
        }
    }

    /** Verifies second {@link GameWindow#startGame} replaces the previous {@link GamePanel} instance. */
    @Test
    void startGame_twice_replacesPanel() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "EMBER");
            GamePanel first = w.getActiveGamePanelForTests();
            w.startGame(2, false, "AQUA");
            GamePanel second = w.getActiveGamePanelForTests();
            assertNotNull(second);
            assertNotSame(first, second);
            second.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies menu and character-select navigation never leaves a game panel attached. */
    @Test
    void menu_thenCharacterSelect_thenMenu_noActiveGamePanel() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            assertFalse(w.hasActiveGamePanel());
            w.showCharacterSelect();
            assertFalse(w.hasActiveGamePanel());
            w.showMenu();
            assertFalse(w.hasActiveGamePanel());
        } finally {
            w.dispose();
        }
    }

    /** Verifies flow from character select into {@link GameWindow#startGame} attaches a game panel. */
    @Test
    void characterSelect_thenStartGame_attachesPanel() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.showCharacterSelect();
            w.startGame(1, true, null);
            assertTrue(w.hasActiveGamePanel());
            w.getActiveGamePanelForTests().stopGame();
        } finally {
            w.dispose();
        }
    }
}
