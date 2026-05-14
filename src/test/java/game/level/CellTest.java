package game.level;

import game.collectible.BonusCrystal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Cell}: walkability, terrain flags, entities, and clear/toggle behaviour.
 */
class CellTest {

    /** A new cell defaults to empty floor with correct coordinates. */
    @Test
    void newCell_isEmptyFloor() {
        Cell cell = new Cell(3, 4);
        assertEquals(3, cell.getX());
        assertEquals(4, cell.getY());
        assertTrue(cell.isWalkable());
        assertTrue(cell.isEmpty());
        assertFalse(cell.isWall());
        assertFalse(cell.isLava());
        assertFalse(cell.isWater());
        assertFalse(cell.isTrap());
        assertNull(cell.getEntity());
    }

    /** A wall cell is not walkable and not empty. */
    @Test
    void wall_blocksWalkingAndIsNotEmpty() {
        Cell cell = new Cell(0, 0);
        cell.setWall(true);
        assertFalse(cell.isWalkable());
        assertFalse(cell.isEmpty());
        assertTrue(cell.isWall());
    }

    /** Lava, water, and trap tiles are not considered empty. */
    @Test
    void hazardsAreNotEmpty() {
        Cell cell = new Cell(0, 0);
        cell.setLava(true);
        assertFalse(cell.isEmpty());
        assertTrue(cell.isLava());

        Cell water = new Cell(1, 1);
        water.setWater(true);
        assertFalse(water.isEmpty());
        assertTrue(water.isWater());

        Cell trap = new Cell(2, 2);
        trap.setTrap(true);
        assertFalse(trap.isEmpty());
        assertTrue(trap.isTrap());
    }

    /** A cell holding an entity is not empty. */
    @Test
    void entityOccupiedCell_isNotEmpty() {
        Cell cell = new Cell(0, 0);
        cell.setEntity(new BonusCrystal(0, 0));
        assertFalse(cell.isEmpty());
        assertNotNull(cell.getEntity());
    }

    /** {@link Cell#clear()} removes the entity but leaves terrain flags intact. */
    @Test
    void clear_removesEntityOnly() {
        Cell cell = new Cell(0, 0);
        cell.setTrap(true);
        cell.setEntity(new BonusCrystal(0, 0));
        cell.clear();
        assertNull(cell.getEntity());
        assertTrue(cell.isTrap());
    }

    /** Wall flag can be turned off to restore walkable empty floor. */
    @Test
    void wallCanBeToggledOff() {
        Cell cell = new Cell(0, 0);
        cell.setWall(true);
        assertFalse(cell.isWalkable());
        cell.setWall(false);
        assertTrue(cell.isWalkable());
        assertTrue(cell.isEmpty());
    }

    /** Hazard flags can be cleared independently of each other. */
    @Test
    void hazardFlags_canBeClearedIndependently() {
        Cell cell = new Cell(0, 0);
        cell.setLava(true);
        cell.setLava(false);
        assertFalse(cell.isLava());
        cell.setWater(true);
        cell.setWater(false);
        assertFalse(cell.isWater());
        cell.setTrap(true);
        cell.setTrap(false);
        assertFalse(cell.isTrap());
    }
}
