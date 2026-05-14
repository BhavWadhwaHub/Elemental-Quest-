package game.level;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Boundary and unusual inputs for {@link Level} (invalid ids, null/unknown 1P character string).
 */
class LevelEdgeCaseTest {

    /** Verifies single-player level with null character string uses Ember layout defaults. */
    @Test
    void singlePlayer_nullCharacter_defaultsToEmberLayout() {
        Level level = new Level(1, null);
        assertNotNull(level.getBoard());
        assertEquals(5, level.getRequiredCrystals());
    }

    /** Verifies empty character name falls back to Ember layout. */
    @Test
    void singlePlayer_emptyString_defaultsToEmberLayout() {
        Level level = new Level(1, "");
        assertEquals(5, level.getRequiredCrystals());
    }

    /** Verifies unknown character name falls back to Ember layout. */
    @Test
    void singlePlayer_unknownString_defaultsToEmberLayout() {
        Level level = new Level(1, "NOT_A_CHARACTER");
        assertEquals(5, level.getRequiredCrystals());
    }

    /** Verifies {@code "aqua"} is not treated as {@code AQUA}; lowercase uses Ember start layout. */
    @Test
    void singlePlayer_caseSensitive_lowercaseUsesEmberStartLayout() {
        Level exactAqua = new Level(1, "AQUA");
        Level lowercase = new Level(1, "aqua");
        assertArrayEquals(new int[]{1, 1}, exactAqua.getAquaStart());
        assertArrayEquals(new int[]{1, 1}, lowercase.getEmberStart());
    }

    /** Verifies two-player level id zero uses level-one crystal count and time limit. */
    @Test
    void twoPlayer_levelZero_fallsBackToLevelOneLayout() {
        Level level = new Level(0);
        assertEquals(6, level.getRequiredCrystals());
        assertEquals(120, level.getTimeLimit());
    }

    /** Verifies negative two-player level id uses level-one required crystal count. */
    @Test
    void twoPlayer_negativeLevel_fallsBackToLevelOneLayout() {
        Level level = new Level(-5);
        assertEquals(6, level.getRequiredCrystals());
    }

    /** Verifies excess {@link Level#collectCrystal()} calls complete level without breaking invariants. */
    @Test
    void collectCrystal_beforeRequired_neverNegativeRemaining() {
        Level level = new Level(1);
        int req = level.getRequiredCrystals();
        for (int i = 0; i < req + 50; i++) {
            level.collectCrystal();
        }
        assertTrue(level.getRemainingCrystals() <= 0);
        assertTrue(level.checkCompletion());
    }

    /** Verifies two-player level id above three falls back to level-one layout metrics. */
    @Test
    void twoPlayer_levelBeyondThree_defaultsToLevelOneLayout() {
        Level level = new Level(4);
        assertEquals(6, level.getRequiredCrystals());
        assertEquals(120, level.getTimeLimit());
        assertEquals(16, level.getBoard().getWidth());
        assertEquals(12, level.getBoard().getHeight());
    }

    /** Verifies single-player Ember with level zero uses Ember level-one parameters. */
    @Test
    void singlePlayerEmber_levelZero_defaultsToEmberLevelOne() {
        Level level = new Level(0, "EMBER");
        assertEquals(5, level.getRequiredCrystals());
        assertEquals(16, level.getBoard().getWidth());
    }

    /** Verifies invalid high level with {@code AQUA} falls back to Aqua level-one board features. */
    @Test
    void singlePlayerAqua_largeInvalidLevel_defaultsToAquaLevelOne() {
        Level level = new Level(99, "AQUA");
        assertEquals(5, level.getRequiredCrystals());
        assertNotNull(level.getBoard().getWaterGate());
    }

    /** Verifies over-collection drives remaining crystals negative while completion stays true. */
    @Test
    void getRemainingCrystals_goesNegativeWhenOverCollected() {
        Level level = new Level(1, "EMBER");
        int req = level.getRequiredCrystals();
        for (int i = 0; i < req + 3; i++) {
            level.collectCrystal();
        }
        assertTrue(level.getRemainingCrystals() < 0);
        assertTrue(level.checkCompletion());
    }

    /** Verifies {@link Level#setCompleted(boolean)} and {@link Level#isCompleted()} round-trip. */
    @Test
    void completedFlag_canBeToggled() {
        Level level = new Level(1);
        assertFalse(level.isCompleted());
        level.setCompleted(true);
        assertTrue(level.isCompleted());
        level.setCompleted(false);
        assertFalse(level.isCompleted());
    }

    /** Verifies whitespace-only character name does not select Aqua; Ember layout is used. */
    @Test
    void singlePlayer_whitespaceOnly_notEqualToAqua_usesEmberLayout() {
        Level level = new Level(1, "   ");
        assertEquals(5, level.getRequiredCrystals());
    }
}
