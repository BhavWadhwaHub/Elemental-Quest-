package game.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lightweight construction tests for menu screens (no EDT helpers, no reflection, no paint-to-image).
 */
class MainMenuCharacterSelectSmokeTest {

    /** {@link MainMenu} can be constructed when exit action is stubbed for tests. */
    @Test
    void mainMenu_constructibleWithWindow() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        MainMenu.setMenuExitActionForTest(() -> { });
        try {
            MainMenu menu = new MainMenu(w);
            assertNotNull(menu);
        } finally {
            MainMenu.setMenuExitActionForTest(null);
            w.dispose();
        }
    }

    /** {@link CharacterSelectScreen} constructs with a parent {@link GameWindow}. */
    @Test
    void characterSelect_constructibleWithWindow() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            CharacterSelectScreen screen = new CharacterSelectScreen(w);
            assertNotNull(screen);
        } finally {
            w.dispose();
        }
    }
}
