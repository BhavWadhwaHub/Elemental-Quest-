package game.level;

import game.character.Player;
import game.collectible.BonusCrystal;
import game.collectible.Collectible;
import game.enemy.ShadowSentinel;
import game.gate.FireGate;
import game.gate.WaterGate;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Board} grid construction, terrain, entities, collectibles, gates, and enemy updates.
 */
class BoardTest {

    /** Verifies constructor dimensions, cell access, and null for out-of-range coordinates. */
    @Test
    void constructor_fillsGridWithCells() {
        Board board = new Board(4, 5);
        assertEquals(4, board.getWidth());
        assertEquals(5, board.getHeight());
        assertNotNull(board.getCell(0, 0));
        assertNotNull(board.getCell(3, 4));
        assertNull(board.getCell(-1, 0));
        assertNull(board.getCell(4, 0));
    }

    /** Verifies {@link Board#isValidPosition(int, int)} and {@link Board#isValidMove(int, int)} including walls. */
    @Test
    void isValidPosition_and_isValidMove() {
        Board board = new Board(3, 3);
        assertTrue(board.isValidPosition(0, 0));
        assertFalse(board.isValidPosition(3, 0));
        assertTrue(board.isValidMove(1, 1));

        board.setWall(1, 1);
        assertFalse(board.isValidMove(1, 1));
    }

    /** Verifies terrain setters ignore out-of-bounds coordinates without mutating in-bounds cells. */
    @Test
    void setTerrainHelpers_ignoreOutOfBounds() {
        Board board = new Board(2, 2);
        board.setWall(-1, 0);
        board.setLava(99, 99);
        board.setWater(3, 0);
        board.setTrap(0, 5);
        assertTrue(board.getCell(0, 0).isEmpty());
    }

    /** Verifies {@link Board#addEntity} and {@link Board#removeEntity} keep cell and list consistent. */
    @Test
    void addAndRemoveEntity_updatesCell() {
        Board board = new Board(5, 5);
        BonusCrystal gem = new BonusCrystal(2, 2);
        board.addEntity(gem);
        assertSame(gem, board.getEntityAt(2, 2));
        assertTrue(board.getEntities().contains(gem));

        board.removeEntity(gem);
        assertNull(board.getEntityAt(2, 2));
        assertFalse(board.getEntities().contains(gem));
    }

    /** Verifies {@link Board#addEnemy} registers enemy and occupies the cell. */
    @Test
    void addEnemy_registersSentinel() {
        Board board = new Board(5, 5);
        ShadowSentinel s = new ShadowSentinel(1, 1);
        board.addEnemy(s);
        assertEquals(1, board.getEnemies().size());
        assertSame(s, board.getEntityAt(1, 1));
    }

    /** Verifies {@link Board#removeEnemy} clears enemy list and cell occupant. */
    @Test
    void removeEnemy_clearsBoard() {
        Board board = new Board(5, 5);
        ShadowSentinel s = new ShadowSentinel(1, 1);
        board.addEnemy(s);
        board.removeEnemy(s);
        assertTrue(board.getEnemies().isEmpty());
        assertNull(board.getEntityAt(1, 1));
    }

    /** Verifies {@link Board#updateEnemies()} eventually moves an open-board sentinel. */
    @Test
    void updateEnemies_callsMoveOnEach() {
        Board board = new Board(20, 20);
        ShadowSentinel s = new ShadowSentinel(10, 10);
        board.addEnemy(s);
        int x0 = s.getX();
        int y0 = s.getY();
        for (int i = 0; i < 50; i++) {
            board.updateEnemies();
        }
        assertTrue(s.getX() != x0 || s.getY() != y0, "Sentinel should move on an open board");
    }

    /** Verifies collectible add, detach from cell, and remove from list behaviour. */
    @Test
    void collectibles_addRemoveDetach() {
        Board board = new Board(4, 4);
        BonusCrystal b = new BonusCrystal(1, 1);
        board.addCollectible(b);
        assertEquals(1, board.getCollectibles().size());
        assertSame(b, board.getEntityAt(1, 1));

        board.detachCollectibleEntity(b);
        assertNull(board.getEntityAt(1, 1));
        assertEquals(1, board.getCollectibles().size());

        board.removeCollectible(b);
        assertTrue(board.getCollectibles().isEmpty());
    }

    /** Verifies fire and water gate setters, getters, and removal. */
    @Test
    void fireAndWaterGates() {
        Board board = new Board(5, 5);
        FireGate fg = new FireGate(0, 0);
        WaterGate wg = new WaterGate(1, 0);
        board.setFireGate(fg);
        board.setWaterGate(wg);
        assertSame(fg, board.getFireGate());
        assertSame(wg, board.getWaterGate());

        board.removeFireGate();
        board.removeWaterGate();
        assertNull(board.getFireGate());
        assertNull(board.getWaterGate());
    }

    /** Verifies removing absent gates does not throw. */
    @Test
    void removeGates_whenAbsent_isSafe() {
        Board board = new Board(2, 2);
        assertDoesNotThrow(() -> {
            board.removeFireGate();
            board.removeWaterGate();
        });
    }

    /** Verifies out-of-bounds entity is listed but does not occupy a grid cell. */
    @Test
    void addEntity_outOfBounds_stillInListButNoCellOccupant() {
        Board board = new Board(3, 3);
        ShadowSentinel s = new ShadowSentinel(-1, -1);
        board.addEntity(s);
        assertTrue(board.getEntities().contains(s));
        assertNull(board.getEntityAt(-1, -1));
        assertNull(board.getCell(1, 1).getEntity());
    }

    /** Verifies double {@link Board#removeEntity} on the same entity is safe. */
    @Test
    void removeEntity_twice_secondCallSafe() {
        Board board = new Board(4, 4);
        BonusCrystal g = new BonusCrystal(1, 1);
        board.addEntity(g);
        board.removeEntity(g);
        assertDoesNotThrow(() -> board.removeEntity(g));
        assertNull(board.getEntityAt(1, 1));
    }

    /** Verifies invalid move and position checks for coordinates outside the board. */
    @Test
    void isValidMove_outOfBounds_shortCircuits() {
        Board board = new Board(2, 2);
        assertFalse(board.isValidMove(-1, 0));
        assertFalse(board.isValidMove(0, 2));
        assertFalse(board.isValidPosition(2, 0));
        assertFalse(board.isValidPosition(0, -1));
    }

    /** Verifies removing an entity that was only listed off-grid clears the list safely. */
    @Test
    void removeEntity_outOfBounds_doesNotTouchGrid() {
        Board board = new Board(3, 3);
        ShadowSentinel s = new ShadowSentinel(50, 50);
        board.addEntity(s);
        assertDoesNotThrow(() -> board.removeEntity(s));
        assertFalse(board.getEntities().contains(s));
    }

    /** Verifies non-{@link game.level.Entity} collectible is only on collectible list. */
    @Test
    void addCollectible_nonEntityImplementation_onlyListNotEntities() {
        Board board = new Board(4, 4);
        CollectibleOnly c = new CollectibleOnly();
        board.addCollectible(c);
        assertEquals(1, board.getCollectibles().size());
        assertTrue(board.getEntities().isEmpty());
    }

    /** Verifies removing non-entity collectible clears list without entity removal path. */
    @Test
    void removeCollectible_nonEntity_skipsRemoveEntity() {
        Board board = new Board(4, 4);
        CollectibleOnly c = new CollectibleOnly();
        board.addCollectible(c);
        board.removeCollectible(c);
        assertTrue(board.getCollectibles().isEmpty());
        assertTrue(board.getEntities().isEmpty());
    }

    /** Verifies detaching a non-matching non-entity collectible leaves on-board gem untouched. */
    @Test
    void detachCollectibleEntity_nonEntity_noOp() {
        Board board = new Board(4, 4);
        BonusCrystal onBoard = new BonusCrystal(1, 1);
        board.addCollectible(onBoard);
        board.detachCollectibleEntity(new CollectibleOnly());
        assertSame(onBoard, board.getEntityAt(1, 1));
        assertTrue(board.getEntities().contains(onBoard));
    }

    /** Verifies detaching collectible entity off-grid removes it from entity list only. */
    @Test
    void detachCollectibleEntity_entityOffBoard_removesFromEntitiesWithoutCellClear() {
        Board board = new Board(3, 3);
        BonusCrystal offGrid = new BonusCrystal(10, 10);
        board.addCollectible(offGrid);
        assertTrue(board.getEntities().contains(offGrid));
        assertNull(board.getEntityAt(10, 10));
        board.detachCollectibleEntity(offGrid);
        assertFalse(board.getEntities().contains(offGrid));
    }

    /** Collectible that is not an {@link Entity} — covers {@code instanceof Entity} false paths. */
    private static final class CollectibleOnly implements Collectible {
        @Override
        public void collect(Player player) { }

        @Override
        public int getPointValue() {
            return 0;
        }

        @Override
        public boolean isCollected() {
            return false;
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }
    }
}
