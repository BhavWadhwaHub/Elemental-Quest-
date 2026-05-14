package game.ui;

import game.core.GameState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link GamePanel} using same-package hooks and {@link GameWindow#startGame}
 * — no EDT scheduling, no reflection, no {@code GamePanel} off-screen {@code BufferedImage} painting.
 */
class GamePanelHooksIntegrationTest {

    /** Verifies one-player Ember start wires level, player, and {@link GameState#PLAYING}. */
    @Test
    void startGame_onePlayerEmber_wiresLevelAndPlayer() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "EMBER");
            GamePanel p = w.getActiveGamePanelForTests();
            assertNotNull(p);
            assertNotNull(p.getRunningLevel());
            assertNotNull(p.getRunningPlayer());
            assertEquals(GameState.PLAYING, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies one-player Aqua start exposes a non-null running player. */
    @Test
    void startGame_onePlayerAqua_startsInAquaMode() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "AQUA");
            GamePanel p = w.getActiveGamePanelForTests();
            assertNotNull(p.getRunningPlayer());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies two-player start attaches an active {@link GamePanel}. */
    @Test
    void startGame_twoPlayer_attachesPanel() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, true, null);
            assertTrue(w.hasActiveGamePanel());
            GamePanel p = w.getActiveGamePanelForTests();
            assertNotNull(p);
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies {@link GamePanel#triggerTestGameOver(String)} sets {@link GameState#GAME_OVER}. */
    @Test
    void triggerTestGameOver_setsState() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "EMBER");
            GamePanel p = w.getActiveGamePanelForTests();
            p.triggerTestGameOver("JUnit");
            assertEquals(GameState.GAME_OVER, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies {@link GamePanel#triggerTestLevelComplete()} sets {@link GameState#LEVEL_COMPLETE}. */
    @Test
    void triggerTestLevelComplete_setsState() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "EMBER");
            GamePanel p = w.getActiveGamePanelForTests();
            p.triggerTestLevelComplete();
            assertEquals(GameState.LEVEL_COMPLETE, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies {@link GamePanel#enterTestPausedState()} sets {@link GameState#PAUSED}. */
    @Test
    void enterTestPausedState_setsPaused() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(1, false, "EMBER");
            GamePanel p = w.getActiveGamePanelForTests();
            p.enterTestPausedState();
            assertEquals(GameState.PAUSED, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies one-player start on level 2 loads running level number 2. */
    @Test
    void startGame_level2_onePlayer_loadsLevelTwo() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(2, false, "EMBER");
            GamePanel p = w.getActiveGamePanelForTests();
            assertEquals(2, p.getRunningLevel().getLevelNumber());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies one-player Aqua start on level 3 loads running level number 3. */
    @Test
    void startGame_level3_onePlayerAqua_loadsLevelThree() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(3, false, "AQUA");
            GamePanel p = w.getActiveGamePanelForTests();
            assertEquals(3, p.getRunningLevel().getLevelNumber());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies two-player start on level 2 loads running level number 2. */
    @Test
    void startGame_level2_twoPlayer_loadsLevelTwo() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(2, true, null);
            GamePanel p = w.getActiveGamePanelForTests();
            assertEquals(2, p.getRunningLevel().getLevelNumber());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Verifies two-player level 3 start still allows test level-complete hook to set state. */
    @Test
    void startGame_level3_twoPlayer_hooksStillWork() {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(3, true, null);
            GamePanel p = w.getActiveGamePanelForTests();
            p.triggerTestLevelComplete();
            assertEquals(GameState.LEVEL_COMPLETE, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }
}
