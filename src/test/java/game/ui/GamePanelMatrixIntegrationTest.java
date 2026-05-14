package game.ui;

import game.core.GameState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Exhaustive {@link GamePanel} × {@link GameWindow#startGame} combinations:
 * levels 1–3 × (two-player / 1P EMBER / 1P AQUA), crossed with hook-driven states.
 */
class GamePanelMatrixIntegrationTest {

    private static final String TWO_PLAYER = "__2P__";

    /** Supplies {@link org.junit.jupiter.params.provider.Arguments} for levels 1–3 and each start mode (2P, EMBER, AQUA). */
    static Stream<Arguments> startModes() {
        Stream.Builder<Arguments> b = Stream.builder();
        for (int level = 1; level <= 3; level++) {
            b.add(Arguments.of(level, true, TWO_PLAYER));
            b.add(Arguments.of(level, false, "EMBER"));
            b.add(Arguments.of(level, false, "AQUA"));
        }
        return b.build();
    }

    private static String characterOrNull(boolean twoPlayer, String mode) {
        return twoPlayer ? null : mode;
    }

    /** For each level (1–3) and mode (2P / EMBER / AQUA), {@code startGame} yields {@link GameState#PLAYING}. */
    @ParameterizedTest
    @MethodSource("startModes")
    void startGame_allModeLevelCombos_reachesPlaying(int level, boolean twoPlayer, String mode) {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(level, twoPlayer, characterOrNull(twoPlayer, mode));
            GamePanel p = w.getActiveGamePanelForTests();
            assertNotNull(p);
            assertEquals(GameState.PLAYING, p.getRunningGameState());
            assertEquals(level, p.getRunningLevel().getLevelNumber());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Test hook {@link GamePanel#triggerTestGameOver(String)} sets {@link GameState#GAME_OVER} for all mode/level combos. */
    @ParameterizedTest
    @MethodSource("startModes")
    void triggerTestGameOver_allModeLevelCombos(int level, boolean twoPlayer, String mode) {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(level, twoPlayer, characterOrNull(twoPlayer, mode));
            GamePanel p = w.getActiveGamePanelForTests();
            p.triggerTestGameOver("matrix");
            assertEquals(GameState.GAME_OVER, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Test hook {@link GamePanel#triggerTestLevelComplete()} sets {@link GameState#LEVEL_COMPLETE} for all combos. */
    @ParameterizedTest
    @MethodSource("startModes")
    void triggerTestLevelComplete_allModeLevelCombos(int level, boolean twoPlayer, String mode) {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(level, twoPlayer, characterOrNull(twoPlayer, mode));
            GamePanel p = w.getActiveGamePanelForTests();
            p.triggerTestLevelComplete();
            assertEquals(GameState.LEVEL_COMPLETE, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }

    /** Test hook {@link GamePanel#enterTestPausedState()} sets {@link GameState#PAUSED} for all combos. */
    @ParameterizedTest
    @MethodSource("startModes")
    void enterTestPausedState_allModeLevelCombos(int level, boolean twoPlayer, String mode) {
        GameWindow w = new GameWindow();
        w.setVisible(false);
        try {
            w.startGame(level, twoPlayer, characterOrNull(twoPlayer, mode));
            GamePanel p = w.getActiveGamePanelForTests();
            p.enterTestPausedState();
            assertEquals(GameState.PAUSED, p.getRunningGameState());
            p.stopGame();
        } finally {
            w.dispose();
        }
    }
}
