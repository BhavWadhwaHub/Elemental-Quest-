package game.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for small domain enums: {@link GemType}, {@link Direction}, {@link ElementType}.
 */
class UtilTest {

    /** Verifies all {@link GemType} constants exist and names match expectations. */
    @Test
    void gemTypeCoverage() {
        // Touch all enum values
        for (GemType gem : GemType.values()) {
            assertNotNull(gem);
        }

        assertEquals("RED", GemType.RED.name());
        assertEquals("BLUE", GemType.BLUE.name());
        assertEquals("GREEN", GemType.GREEN.name());
        assertEquals("YELLOW", GemType.YELLOW.name());
        assertEquals("WHITE", GemType.WHITE.name());
    }

    /** Verifies all {@link Direction} constants and their string names. */
    @Test
    void directionCoverage() {
        // Touch all enum values
        for (Direction dir : Direction.values()) {
            assertNotNull(dir);
        }
        assertEquals("UP", Direction.UP.name());
        assertEquals("DOWN", Direction.DOWN.name());
        assertEquals("LEFT", Direction.LEFT.name());
        assertEquals("RIGHT", Direction.RIGHT.name());
    }

    /** Verifies {@link Direction} delta vectors are unit steps on the grid. */
    @Test
    void direction_vectorsAreUnitSteps() {
        assertEquals(0, Direction.UP.getDx());
        assertEquals(-1, Direction.UP.getDy());
        assertEquals(0, Direction.DOWN.getDx());
        assertEquals(1, Direction.DOWN.getDy());
        assertEquals(-1, Direction.LEFT.getDx());
        assertEquals(0, Direction.LEFT.getDy());
        assertEquals(1, Direction.RIGHT.getDx());
        assertEquals(0, Direction.RIGHT.getDy());
    }

    /** Verifies all {@link ElementType} constants exist and names match expectations. */
    @Test
    void elementTypeCoverage() {
        // Touch all enum values
        for (ElementType type : ElementType.values()) {
            assertNotNull(type);
        }

        assertEquals("FIRE", ElementType.FIRE.name());
        assertEquals("WATER", ElementType.WATER.name());
        assertEquals("EARTH", ElementType.EARTH.name());
        assertEquals("AIR", ElementType.AIR.name());
    }
}
