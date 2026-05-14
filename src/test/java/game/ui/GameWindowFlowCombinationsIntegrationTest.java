package game.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Additional {@link GameWindow} transition combinations (menu ↔ character select ↔ game).
 * Lives in {@code game.ui} to access package-private {@link GameWindow#getActiveGamePanelForTests()}.
 */
class GameWindowFlowCombinationsIntegrationTest {

    /** Menu → game → stop → menu → new game on another level and mode. */
    @Test
    void menu_startGame_showMenu_startGame_otherLevel() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "EMBER");
            assertTrue(w.hasActiveGamePanel());
            w.getActiveGamePanelForTests().stopGame();
            w.showMenu();
            assertFalse(w.hasActiveGamePanel());
            w.startGame(3, true, null);
            assertEquals(3, w.getActiveGamePanelForTests().getRunningLevel().getLevelNumber());
            w.getActiveGamePanelForTests().stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Character select → game → menu → character select → game chain. */
    @Test
    void characterSelect_startGame_showMenu_showCharacterSelect_startGame() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.showCharacterSelect();
            w.startGame(2, false, "AQUA");
            w.getActiveGamePanelForTests().stopGame();
            w.showMenu();
            w.showCharacterSelect();
            w.startGame(1, false, "EMBER");
            assertTrue(w.hasActiveGamePanel());
            w.getActiveGamePanelForTests().stopGame();
        } finally {
            w.dispose();
        }
    }

    /** {@link GameWindow#showCharacterSelect()} does not remove an active {@link GamePanel} until the next {@code startGame}. */
    @Test
    void startGame_showCharacterSelect_panelStillPresentUntilNextStartGame() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, true, null);
            assertTrue(w.hasActiveGamePanel());
            w.showCharacterSelect();
            assertTrue(w.hasActiveGamePanel());
            w.startGame(1, false, "EMBER");
            assertTrue(w.hasActiveGamePanel());
            w.getActiveGamePanelForTests().stopGame();
        } finally {
            w.dispose();
        }
    }
}
