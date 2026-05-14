package game.character;

import game.level.Board;
import game.util.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Same package as {@link CharacterRoster} so we can test switching and movement delegation.
 */
class CharacterRosterTest {

    /** Verifies roster starts with {@link Ember} active. */
    @Test
    void startsWithEmberActive() {
        CharacterRoster roster = new CharacterRoster(0, 0, 1, 1);
        assertTrue(roster.isEmberActive());
        assertTrue(roster.getActiveCharacter() instanceof Ember);
    }

    /** Verifies {@link CharacterRoster#switchCharacter()} toggles between {@link Ember} and {@link Aqua}. */
    @Test
    void switchCharacter_togglesActive() {
        CharacterRoster roster = new CharacterRoster(0, 0, 1, 1);
        roster.switchCharacter();
        assertFalse(roster.isEmberActive());
        assertTrue(roster.getActiveCharacter() instanceof Aqua);
        roster.switchCharacter();
        assertTrue(roster.isEmberActive());
    }

    /** Verifies {@link CharacterRoster#move(Board, Direction)} moves the active character on the board. */
    @Test
    void move_updatesActiveCharacterPosition() {
        CharacterRoster roster = new CharacterRoster(2, 2, 3, 3);
        Board board = new Board(6, 6);
        assertTrue(roster.move(board, Direction.RIGHT));
        assertEquals(3, roster.getEmber().getX());
        assertEquals(2, roster.getEmber().getY());
    }

    /** Verifies {@link CharacterRoster#resetPositions(int, int, int, int)} restores both spawns and Ember active. */
    @Test
    void resetPositions_restoresCoordinatesAndEmberActive() {
        CharacterRoster roster = new CharacterRoster(0, 0, 1, 1);
        roster.switchCharacter();
        Board board = new Board(5, 5);
        roster.move(board, Direction.DOWN);
        roster.resetPositions(4, 4, 5, 5);
        assertEquals(4, roster.getEmber().getX());
        assertEquals(4, roster.getEmber().getY());
        assertEquals(5, roster.getAqua().getX());
        assertEquals(5, roster.getAqua().getY());
        assertTrue(roster.isEmberActive());
    }

    /** Verifies move into a wall returns false and leaves position unchanged. */
    @Test
    void move_blockedByWall_doesNotChangePosition() {
        CharacterRoster roster = new CharacterRoster(1, 1, 2, 2);
        Board board = new Board(4, 4);
        board.setWall(2, 1);
        assertFalse(roster.move(board, Direction.RIGHT));
        assertEquals(1, roster.getEmber().getX());
    }

    /** Verifies odd number of switches leaves {@link Aqua} active; one more switch restores {@link Ember}. */
    @Test
    void switchCharacter_manyTimes_parityPredictable() {
        CharacterRoster roster = new CharacterRoster(0, 0, 1, 1);
        for (int i = 0; i < 101; i++) {
            roster.switchCharacter();
        }
        assertFalse(roster.isEmberActive());
        assertTrue(roster.getActiveCharacter() instanceof Aqua);
        roster.switchCharacter();
        assertTrue(roster.isEmberActive());
    }

    /** Verifies when {@link Aqua} is active, move updates Aqua coordinates only. */
    @Test
    void move_whenActiveIsAqua_movesAquaNotEmber() {
        CharacterRoster roster = new CharacterRoster(2, 2, 4, 4);
        roster.switchCharacter();
        Board board = new Board(8, 8);
        assertTrue(roster.move(board, Direction.LEFT));
        assertEquals(3, roster.getAqua().getX());
        assertEquals(4, roster.getAqua().getY());
        assertEquals(2, roster.getEmber().getX());
    }

    /** Verifies reset with identical Ember and Aqua coordinates keeps both aligned. */
    @Test
    void resetPositions_whenCoordinatesEqual_stillConsistent() {
        CharacterRoster roster = new CharacterRoster(0, 0, 1, 1);
        roster.resetPositions(5, 5, 5, 5);
        assertEquals(5, roster.getEmber().getX());
        assertEquals(5, roster.getEmber().getY());
        assertEquals(5, roster.getAqua().getX());
        assertEquals(5, roster.getAqua().getY());
    }
}
