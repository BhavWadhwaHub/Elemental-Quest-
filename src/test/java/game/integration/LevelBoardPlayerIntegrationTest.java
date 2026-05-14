package game.integration;

import game.character.Player;
import game.collectible.ElementalCrystal;
import game.level.Board;
import game.level.Level;
import game.util.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p><b>Integration focus — level data ↔ board ↔ player movement</b></p>
 * <p>{@link Level} builds an in-memory {@link Board} (walls, hazards, collectibles, gates, enemies).
 * {@link Player} must spawn at the level’s start coordinates and move using the same {@link Board}
 * the level exposes. This stack is what {@link game.ui.GamePanel} uses internally; here we test it
 * without the UI to show the domain layer works as one unit.</p>
 */
class LevelBoardPlayerIntegrationTest {

    /** Verifies level 1 board elemental crystal count matches {@link Level#getRequiredCrystals()} and gates exist. */
    @Test
    void twoPlayerLevel_one_crystalCountMatchesRequirement() {
        Level level = new Level(1);
        Board board = level.getBoard();
        long elementalCrystals = board.getCollectibles().stream()
                .filter(c -> c instanceof ElementalCrystal)
                .count();
        assertEquals(level.getRequiredCrystals(), elementalCrystals);
        assertNotNull(board.getFireGate());
        assertNotNull(board.getWaterGate());
    }

    /** Verifies {@link Player} at level spawns can move right on the level's {@link Board}. */
    @Test
    void playerSpawnedAtLevelStarts_canMoveOnThatBoard() {
        Level level = new Level(1);
        Board board = level.getBoard();
        Player player = new Player(
                level.getEmberStart()[0], level.getEmberStart()[1],
                level.getAquaStart()[0], level.getAquaStart()[1]
        );
        assertTrue(player.move(board, Direction.RIGHT));
        assertEquals(level.getEmberStart()[0] + 1, player.getEmber().getX());
    }

    /** Verifies single-player Ember spawn is valid and one step right succeeds. */
    @Test
    void singlePlayerEmber_playerCanStepRightFromSpawn() {
        Level level = new Level(1, "EMBER");
        Board board = level.getBoard();
        Player player = new Player(
                level.getEmberStart()[0], level.getEmberStart()[1],
                level.getAquaStart()[0], level.getAquaStart()[1]
        );
        assertTrue(board.isValidMove(player.getEmber().getX(), player.getEmber().getY()));
        assertTrue(player.move(board, Direction.RIGHT));
        assertEquals(level.getEmberStart()[0] + 1, player.getEmber().getX());
        assertEquals(level.getEmberStart()[1], player.getEmber().getY());
    }

    /** Verifies two-player level 2 number, walkable spawn, and fire gate presence. */
    @Test
    void levelTwo_twoPlayer_boardAndSpawnsConsistent() {
        Level level = new Level(2);
        Board board = level.getBoard();
        Player player = new Player(
                level.getEmberStart()[0], level.getEmberStart()[1],
                level.getAquaStart()[0], level.getAquaStart()[1]
        );
        assertEquals(2, level.getLevelNumber());
        assertTrue(board.isValidMove(player.getEmber().getX(), player.getEmber().getY()));
        assertNotNull(board.getFireGate());
        assertNotNull(board.getWaterGate());
    }

    /** Verifies single-player Ember level 3 reports correct number and walkable Ember spawn. */
    @Test
    void levelThree_singlePlayerEmber_spawnIsWalkable() {
        Level level = new Level(3, "EMBER");
        Board board = level.getBoard();
        int ex = level.getEmberStart()[0];
        int ey = level.getEmberStart()[1];
        assertEquals(3, level.getLevelNumber());
        assertTrue(board.isValidMove(ex, ey), "Ember spawn should be on a walkable tile");
    }

    /** Verifies crystal collection reaches {@link Level#checkCompletion()} exactly when required count is met. */
    @Test
    void collectCrystals_untilCompletion_twoPlayerLevel() {
        Level level = new Level(1);
        int required = level.getRequiredCrystals();
        for (int i = 0; i < required - 1; i++) {
            level.collectCrystal();
            assertFalse(level.checkCompletion());
        }
        level.collectCrystal();
        assertTrue(level.checkCompletion());
        assertEquals(0, level.getRemainingCrystals());
    }
}
