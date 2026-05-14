package game.level;

import game.util.ElementType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Level} constructors for two-player and single-player modes, completion, and time limits.
 */
class LevelTest {

    /** Verifies two-player level 1 number, crystals, board, and spawn arrays. */
    @Test
    void testTwoPlayerLevel1Initialization() {
        Level level = new Level(1);
        assertEquals(1, level.getLevelNumber());
        assertEquals(6, level.getRequiredCrystals());
        assertNotNull(level.getBoard());
        assertArrayEquals(new int[]{1, 1}, level.getEmberStart());
        assertArrayEquals(new int[]{1, 2}, level.getAquaStart());
        assertEquals(0, level.getCollectedCrystals());
    }

    /** Verifies single-player Ember level 1 parameters and Ember start. */
    @Test
    void testSinglePlayerEmberLevel() {
        Level level = new Level(1, "EMBER");
        assertEquals(1, level.getLevelNumber());
        assertEquals(5, level.getRequiredCrystals());
        assertArrayEquals(new int[]{1, 1}, level.getEmberStart());
    }

    /** Verifies single-player Aqua level 1 parameters and Aqua start. */
    @Test
    void testSinglePlayerAquaLevel() {
        Level level = new Level(1, "AQUA");
        assertEquals(1, level.getLevelNumber());
        assertEquals(5, level.getRequiredCrystals());
        assertArrayEquals(new int[]{1, 1}, level.getAquaStart());
    }

    /** Verifies collecting required crystals triggers {@link Level#checkCompletion()} with zero remaining. */
    @Test
    void testCrystalCollectionAndCompletion() {
        Level level = new Level(1);
        int totalCrystals = level.getRequiredCrystals();

        for (int i = 0; i < totalCrystals - 1; i++) {
            level.collectCrystal();
            assertFalse(level.checkCompletion());
        }

        level.collectCrystal();
        assertTrue(level.checkCompletion());
        assertEquals(0, level.getRemainingCrystals());
    }

    /** Verifies {@link Level#setCompleted(boolean)} toggles {@link Level#isCompleted()}. */
    @Test
    void testSetCompletedFlag() {
        Level level = new Level(1);
        assertFalse(level.isCompleted());

        level.setCompleted(true);
        assertTrue(level.isCompleted());

        level.setCompleted(false);
        assertFalse(level.isCompleted());
    }

    /** Verifies two-player level 2 number, required crystals, and board. */
    @Test
    void testTwoPlayerLevel2Initialization() {
        Level level = new Level(2);
        assertEquals(2, level.getLevelNumber());
        assertEquals(8, level.getRequiredCrystals());
        assertNotNull(level.getBoard());
    }

    /** Verifies two-player level 3 number, required crystals, and board. */
    @Test
    void testTwoPlayerLevel3Initialization() {
        Level level = new Level(3);
        assertEquals(3, level.getLevelNumber());
        assertEquals(10, level.getRequiredCrystals());
        assertNotNull(level.getBoard());
    }

    /** Verifies very large two-player level id keeps stored number but uses level-one crystal requirement. */
    @Test
    void testInvalidLevelDefaultsToLevel1() {
        Level level = new Level(999);
        assertEquals(999, level.getLevelNumber()); 
        assertEquals(6, level.getRequiredCrystals());
    }

    /** Verifies unknown single-player Ember level id preserves number and default layout metrics. */
    @Test
    void singlePlayerEmber_unknownLevelNumber_usesDefaultLayout() {
        Level level = new Level(99, "EMBER");
        assertEquals(99, level.getLevelNumber());
        assertEquals(5, level.getRequiredCrystals());
        assertNotNull(level.getBoard());
    }

    /** Verifies unknown single-player Aqua level id preserves number and default layout metrics. */
    @Test
    void singlePlayerAqua_unknownLevelNumber_usesDefaultLayout() {
        Level level = new Level(99, "AQUA");
        assertEquals(99, level.getLevelNumber());
        assertEquals(5, level.getRequiredCrystals());
        assertNotNull(level.getBoard());
    }

    /** Verifies single-player Ember level 2 crystal count and board. */
    @Test
    void testEmberLevel2() {
        Level level = new Level(2, "EMBER");
        assertEquals(7, level.getRequiredCrystals());
        assertNotNull(level.getBoard());
    }

    /** Verifies single-player Ember level 3 crystal count and board. */
    @Test
    void testEmberLevel3() {
        Level level = new Level(3, "EMBER");
        assertEquals(8, level.getRequiredCrystals());
        assertNotNull(level.getBoard());
    }

    /** Verifies single-player Aqua level 2 required crystals. */
    @Test
    void testAquaLevel2() {
        Level level = new Level(2, "AQUA");
        assertEquals(7, level.getRequiredCrystals());
    }

    /** Verifies single-player Aqua level 3 required crystals. */
    @Test
    void testAquaLevel3() {
        Level level = new Level(3, "AQUA");
        assertEquals(8, level.getRequiredCrystals());
    }

    /** Verifies {@link Level#getTimeLimit()} for levels 1–3 two-player constructors. */
    @Test
    void testTimeLimitValues() {
        assertEquals(120, new Level(1).getTimeLimit());
        assertEquals(150, new Level(2).getTimeLimit());
        assertEquals(180, new Level(3).getTimeLimit());
    }

    /** Verifies heavy over-collection keeps {@link Level#getRemainingCrystals()} at or below zero. */
    @Test
    void testRemainingCrystalsNeverNegative() {
        Level level = new Level(1);

        for (int i = 0; i < 20; i++) {
            level.collectCrystal();
        }

        assertTrue(level.getRemainingCrystals() <= 0);
    }
}
