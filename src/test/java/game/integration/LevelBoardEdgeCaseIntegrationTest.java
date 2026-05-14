package game.integration;

import game.character.Player;
import game.level.Board;
import game.level.Level;
import game.util.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style edge cases for {@link Level}/{@link Board}/{@link Player} together.
 */
class LevelBoardEdgeCaseIntegrationTest {

    /** Verifies Ember cannot move north off spawn due to border wall on level 1. */
    @Test
    void twoPlayerLevel_spawnBlockedByNorthBorderWall() {
        Level level = new Level(1);
        Board board = level.getBoard();
        int ex = level.getEmberStart()[0];
        int ey = level.getEmberStart()[1];
        assertFalse(board.isValidMove(ex, ey - 1));
        Player player = new Player(ex, ey, level.getAquaStart()[0], level.getAquaStart()[1]);
        assertFalse(player.move(board, Direction.UP));
        assertEquals(ex, player.getEmber().getX());
        assertEquals(ey, player.getEmber().getY());
    }

    /** Verifies {@link Player#move(Board, Direction)} false for moves off a minimal open board. */
    @Test
    void tinyOpenBoard_moveOffGrid_returnsFalse() {
        Board board = new Board(3, 3);
        Player player = new Player(0, 0, 1, 1);
        assertFalse(player.move(board, Direction.LEFT));
        assertFalse(player.move(board, Direction.UP));
        assertEquals(0, player.getEmber().getX());
        assertEquals(0, player.getEmber().getY());
    }

    /** Verifies over-collecting on level 2 completes level with negative remaining crystal count. */
    @Test
    void level2_twoPlayer_negativeRemainingAfterOverCollect() {
        Level level = new Level(2);
        int n = level.getRequiredCrystals() + 10;
        for (int i = 0; i < n; i++) {
            level.collectCrystal();
        }
        assertTrue(level.checkCompletion());
        assertTrue(level.getRemainingCrystals() < 0);
    }
}
