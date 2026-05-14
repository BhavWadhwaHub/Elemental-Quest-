package game.collectible;

import game.character.Player;
import game.util.ElementType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link BonusCrystal} and {@link ElementalCrystal} placement, collection, and type metadata.
 */
class CollectibleTest {

    // ---- BonusCrystal tests ----
    /** Verifies new {@link BonusCrystal} position, uncollected state, points, and type id. */
    @Test
    void testBonusCrystalInitialState() {
        BonusCrystal crystal = new BonusCrystal(3, 5);
        assertEquals(3, crystal.getX());
        assertEquals(5, crystal.getY());
        assertFalse(crystal.isCollected());
        assertEquals(100, crystal.getPointValue());
        assertEquals("CRYSTAL_BONUS", crystal.getType());
    }

    /** Verifies {@link BonusCrystal#collect(Player)} marks collected and idempotency on second collect. */
    @Test
    void testBonusCrystalCollect() {
        BonusCrystal crystal = new BonusCrystal(0, 0);
        Player player = new Player(0, 0, 1, 1);

        crystal.collect(player);
        assertTrue(crystal.isCollected());
        crystal.collect(player);
        assertTrue(crystal.isCollected());
    }

    /** Verifies {@link BonusCrystal#setPosition(int, int)} updates coordinates. */
    @Test
    void testBonusCrystalSetPosition() {
        BonusCrystal crystal = new BonusCrystal(1, 2);
        crystal.setPosition(5, 6);
        assertEquals(5, crystal.getX());
        assertEquals(6, crystal.getY());
    }

    // ---- ElementalCrystal tests ----
    /** Verifies {@link ElementalCrystal} position, element, points, type id for fire and water. */
    @Test
    void testElementalCrystalInitialState() {
        ElementalCrystal fireCrystal = new ElementalCrystal(2, 4, ElementType.FIRE);
        assertEquals(2, fireCrystal.getX());
        assertEquals(4, fireCrystal.getY());
        assertEquals(ElementType.FIRE, fireCrystal.getElementType());
        assertFalse(fireCrystal.isCollected());
        assertEquals(100, fireCrystal.getPointValue());
        assertEquals("CRYSTAL_FIRE", fireCrystal.getType());

        ElementalCrystal waterCrystal = new ElementalCrystal(1, 3, ElementType.WATER);
        assertEquals("CRYSTAL_WATER", waterCrystal.getType());
    }

    /** Verifies elemental collect marks collected once and ignores duplicate collects. */
    @Test
    void testElementalCrystalCollect() {
        ElementalCrystal crystal = new ElementalCrystal(0, 0, ElementType.FIRE);
        Player player = new Player(0, 0, 1, 1);

        assertFalse(crystal.isCollected());
        crystal.collect(player);
        assertTrue(crystal.isCollected());

        // Collecting again shouldn't change state
        crystal.collect(player);
        assertTrue(crystal.isCollected());
    }

    /** Verifies {@link ElementalCrystal#setPosition(int, int)} updates coordinates. */
    @Test
    void testElementalCrystalSetPosition() {
        ElementalCrystal crystal = new ElementalCrystal(1, 2, ElementType.WATER);
        crystal.setPosition(7, 8);
        assertEquals(7, crystal.getX());
        assertEquals(8, crystal.getY());
    }

    /** Verifies bonus collect with null player still marks collected without throwing. */
    @Test
    void bonusCrystal_collectWithNullPlayer_stillMarksCollected() {
        BonusCrystal crystal = new BonusCrystal(0, 0);
        assertDoesNotThrow(() -> crystal.collect(null));
        assertTrue(crystal.isCollected());
    }

    /** Verifies elemental collect with null player still marks collected without throwing. */
    @Test
    void elementalCrystal_collectWithNullPlayer_stillMarksCollected() {
        ElementalCrystal crystal = new ElementalCrystal(1, 1, ElementType.FIRE);
        assertDoesNotThrow(() -> crystal.collect(null));
        assertTrue(crystal.isCollected());
    }

    /** Verifies negative coordinates are stored on {@link BonusCrystal}. */
    @Test
    void bonusCrystal_negativeCoordinates_allowed() {
        BonusCrystal c = new BonusCrystal(-1, -5);
        assertEquals(-1, c.getX());
        assertEquals(-5, c.getY());
    }
}
