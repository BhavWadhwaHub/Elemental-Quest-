package game.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ScoreManager}: scoring categories, multiplier, high score, reset, and edge cases.
 */
public class ScoreManagerTest {

    /** New manager has zero score, zero high score, multiplier 1, and no gems collected. */
    @Test
    void testInitialValues() {
        ScoreManager sm = new ScoreManager();

        assertEquals(0, sm.getScore());
        assertEquals(0, sm.getHighScore());
        assertEquals(1, sm.getMultiplier());
        assertEquals(0, sm.getGemsCollected());
    }

    /** {@link ScoreManager#addPoints(int)} updates score and high score. */
    @Test
    void testAddPoints() {
        ScoreManager sm = new ScoreManager();

        sm.addPoints(10);

        assertEquals(10, sm.getScore());
        assertEquals(10, sm.getHighScore());
    }

    /** Points are multiplied by the current multiplier before adding. */
    @Test
    void testAddPointsWithMultiplier() {
        ScoreManager sm = new ScoreManager();
        sm.setMultiplier(2);

        sm.addPoints(10);

        assertEquals(20, sm.getScore());
    }

    /** High score tracks the maximum score reached across multiple adds. */
    @Test
    void testHighScoreUpdatesCorrectly() {
        ScoreManager sm = new ScoreManager();

        sm.addPoints(10);
        sm.addPoints(5);

        assertEquals(15, sm.getHighScore());
    }

    /** Deductions can drive score negative and {@link ScoreManager#isNegative()} becomes true. */
    @Test
    void testDeductPointsMakesScoreNegative() {
        ScoreManager sm = new ScoreManager();

        sm.deductPoints(10);

        assertTrue(sm.isNegative());
        assertEquals(-10, sm.getScore());
    }

    /** Non-negative scores report {@code false} from {@link ScoreManager#isNegative()}. */
    @Test
    void isNegative_falseWhenScoreIsNotBelowZero() {
        ScoreManager sm = new ScoreManager();
        assertFalse(sm.isNegative());
        sm.addPoints(1);
        assertFalse(sm.isNegative());
    }

    /** Gem points add score and increment the gem counter. */
    @Test
    void testAddGemPoints() {
        ScoreManager sm = new ScoreManager();

        sm.addGemPoints(10);

        assertEquals(10, sm.getScore());
        assertEquals(1, sm.getGemsCollected());
    }

    /** Crystal scoring path adds points through the multiplier. */
    @Test
    void testAddCrystalPoints() {
        ScoreManager sm = new ScoreManager();

        sm.addCrystalPoints(15);

        assertEquals(15, sm.getScore());
    }

    /** Kill reward adds points. */
    @Test
    void testAddKillPoints() {
        ScoreManager sm = new ScoreManager();

        sm.addKillPoints(20);

        assertEquals(20, sm.getScore());
    }

    /** Trap destruction reward adds points. */
    @Test
    void testAddTrapPoints() {
        ScoreManager sm = new ScoreManager();

        sm.addTrapPoints(5);

        assertEquals(5, sm.getScore());
    }

    /** Time bonus adds {@code secondsLeft * 10} points. */
    @Test
    void testAddTimeBonus() {
        ScoreManager sm = new ScoreManager();

        sm.addTimeBonus(5);

        assertEquals(50, sm.getScore());
    }

    /** {@link ScoreManager#reset()} clears run score and gems but keeps high score and resets multiplier to 1. */
    @Test
    void testResetKeepsHighScore() {
        ScoreManager sm = new ScoreManager();

        sm.addPoints(50);
        sm.reset();

        assertEquals(0, sm.getScore());
        assertEquals(50, sm.getHighScore());
        assertEquals(1, sm.getMultiplier());
        assertEquals(0, sm.getGemsCollected());
    }

    /** {@link ScoreManager#setMultiplier(int)} clamps to at least 1. */
    @Test
    void testMultiplierCannotBeLessThanOne() {
        ScoreManager sm = new ScoreManager();

        sm.setMultiplier(0);

        assertEquals(1, sm.getMultiplier());
    }

    /** Zero-point add leaves score and high score unchanged. */
    @Test
    void addPoints_zero_doesNotChangeScoreOrHigh() {
        ScoreManager sm = new ScoreManager();
        sm.addPoints(0);
        assertEquals(0, sm.getScore());
        assertEquals(0, sm.getHighScore());
    }

    /** Zero-second time bonus adds nothing. */
    @Test
    void addTimeBonus_zero_addsNothing() {
        ScoreManager sm = new ScoreManager();
        sm.addTimeBonus(0);
        assertEquals(0, sm.getScore());
    }

    /** Zero deduction leaves score unchanged. */
    @Test
    void deductPoints_zero_unchanged() {
        ScoreManager sm = new ScoreManager();
        sm.addPoints(5);
        sm.deductPoints(0);
        assertEquals(5, sm.getScore());
    }

    /** High score is not reduced when deductions push current score below the peak. */
    @Test
    void highScore_notLoweredWhenScoreDropsViaDeduct() {
        ScoreManager sm = new ScoreManager();
        sm.addPoints(100);
        assertEquals(100, sm.getHighScore());
        sm.deductPoints(150);
        assertEquals(-50, sm.getScore());
        assertEquals(100, sm.getHighScore());
        assertTrue(sm.isNegative());
    }

    /** High score increases when cumulative score exceeds the previous peak. */
    @Test
    void highScore_updatesWhenTiedThenExceeded() {
        ScoreManager sm = new ScoreManager();
        sm.addPoints(10);
        assertEquals(10, sm.getHighScore());
        sm.addPoints(0);
        assertEquals(10, sm.getHighScore());
        sm.addPoints(5);
        assertEquals(15, sm.getHighScore());
    }

    /** Large multiplier scales generic {@link ScoreManager#addPoints(int)}. */
    @Test
    void largeMultiplier_appliesToAddPoints() {
        ScoreManager sm = new ScoreManager();
        sm.setMultiplier(50);
        sm.addPoints(2);
        assertEquals(100, sm.getScore());
        assertEquals(100, sm.getHighScore());
    }

    /** Each {@link ScoreManager#addGemPoints(int)} call increments gem count. */
    @Test
    void manyGemPoints_incrementsCounterEachTime() {
        ScoreManager sm = new ScoreManager();
        sm.addGemPoints(1);
        sm.addGemPoints(1);
        sm.addGemPoints(1);
        assertEquals(3, sm.getGemsCollected());
    }

    /** Covers {@code if (score > highScore)} false arm after a prior high score is established. */
    @Test
    void addPoints_belowExistingHighScore_doesNotUpdateHigh() {
        ScoreManager sm = new ScoreManager();
        sm.addPoints(200);
        assertEquals(200, sm.getHighScore());
        sm.reset();
        assertEquals(0, sm.getScore());
        assertEquals(200, sm.getHighScore());
        sm.addPoints(10);
        assertEquals(10, sm.getScore());
        assertEquals(200, sm.getHighScore());
    }

    /** {@code score > highScore} is false when score equals high (e.g. zero-point add). */
    @Test
    void addPoints_whenScoreEqualsHigh_doesNotChangeHigh() {
        ScoreManager sm = new ScoreManager();
        sm.addPoints(50);
        assertEquals(50, sm.getHighScore());
        sm.addPoints(0);
        assertEquals(50, sm.getScore());
        assertEquals(50, sm.getHighScore());
    }
}
