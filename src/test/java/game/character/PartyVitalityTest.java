package game.character;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests lives and gate flags directly. Same package as {@link PartyVitality} so we can construct it.
 */
class PartyVitalityTest {

    /** Verifies new {@link PartyVitality} starts at max lives for both characters and {@link PartyVitality#isAlive()}. */
    @Test
    void startsFullAndAlive() {
        PartyVitality v = new PartyVitality(5);
        assertEquals(5, v.getEmberLives());
        assertEquals(5, v.getAquaLives());
        assertEquals(5, v.getMaxLives());
        assertTrue(v.isAlive());
    }

    /** Verifies {@link PartyVitality#takeDamageForActiveCharacter(boolean)} decrements Ember or Aqua pool. */
    @Test
    void takeDamageForActiveCharacter_hitsCorrectPool() {
        PartyVitality v = new PartyVitality(3);
        v.takeDamageForActiveCharacter(true);
        assertEquals(2, v.getEmberLives());
        assertEquals(3, v.getAquaLives());

        v.takeDamageForActiveCharacter(false);
        assertEquals(2, v.getEmberLives());
        assertEquals(2, v.getAquaLives());
    }

    /** Verifies party is not alive when Ember lives reach zero. */
    @Test
    void isAlive_falseIfEitherCharacterDies() {
        PartyVitality v = new PartyVitality(1);
        v.takeEmberDamage();
        assertFalse(v.isAlive());
    }

    /** Verifies per-character gate flags and {@link PartyVitality#bothAtGates()}. */
    @Test
    void gateFlags_and_bothAtGates() {
        PartyVitality v = new PartyVitality(5);
        v.setEmberAtGate(true);
        assertTrue(v.isEmberAtGate());
        assertFalse(v.bothAtGates());
        v.setAquaAtGate(true);
        assertTrue(v.bothAtGates());
    }

    /** Verifies {@link PartyVitality#reset()} restores lives and clears gate flags. */
    @Test
    void reset_restoresLivesAndGates() {
        PartyVitality v = new PartyVitality(4);
        v.takeEmberDamage();
        v.takeAquaDamage();
        v.setEmberAtGate(true);
        v.reset();
        assertEquals(4, v.getEmberLives());
        assertEquals(4, v.getAquaLives());
        assertFalse(v.isEmberAtGate());
        assertFalse(v.isAquaAtGate());
    }

    /** Verifies {@link PartyVitality#getLivesForActiveCharacter(boolean)} tracks per-character pools. */
    @Test
    void getLivesForActiveCharacter() {
        PartyVitality v = new PartyVitality(5);
        assertEquals(5, v.getLivesForActiveCharacter(true));
        assertEquals(5, v.getLivesForActiveCharacter(false));
        v.takeEmberDamage();
        assertEquals(4, v.getLivesForActiveCharacter(true));
    }

    /** Verifies party dies when Ember pool hits zero even if Aqua has lives. */
    @Test
    void isAlive_falseWhenEitherPoolHitsZero() {
        PartyVitality v = new PartyVitality(2);
        v.takeEmberDamage();
        v.takeEmberDamage();
        assertEquals(0, v.getEmberLives());
        assertTrue(v.getAquaLives() > 0);
        assertFalse(v.isAlive());
    }

    /** Verifies repeated damage can drive Ember lives below zero. */
    @Test
    void livesCanGoNegativeIfOverDamaged() {
        PartyVitality v = new PartyVitality(1);
        v.takeEmberDamage();
        v.takeEmberDamage();
        assertTrue(v.getEmberLives() < 0);
    }

    /** Second operand of {@code emberLives > 0 && aquaLives > 0} when ember still has lives. */
    @Test
    void isAlive_falseWhenAquaDepletedButEmberAlive() {
        PartyVitality v = new PartyVitality(2);
        v.takeAquaDamage();
        v.takeAquaDamage();
        assertTrue(v.getEmberLives() > 0);
        assertEquals(0, v.getAquaLives());
        assertFalse(v.isAlive());
    }
}
