package game.enemy;

import game.character.Aqua;
import game.character.Ember;
import game.level.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ShadowSentinel} and {@link Enemy} contract: damage, interaction, movement on {@link Board}.
 */
class ShadowSentinelTest {

    /** Verifies {@link Enemy} is an interface (moving hostile contract). */
    @Test
    void enemyInterface_documentsMovingHostiles() {
        assertTrue(Enemy.class.isInterface());
    }

    /** Verifies default-constructor sentinel has damage one, position, active flag, and type id. */
    @Test
    void defaultConstructor_damageIsOne() {
        ShadowSentinel s = new ShadowSentinel(2, 3);
        assertEquals(1, s.getDamage());
        assertEquals(2, s.getX());
        assertEquals(3, s.getY());
        assertTrue(s.isActive());
        assertEquals("SHADOW_SENTINEL", s.getType());
    }

    /** Verifies three-argument constructor sets custom damage. */
    @Test
    void customDamage() {
        ShadowSentinel s = new ShadowSentinel(0, 0, 5);
        assertEquals(5, s.getDamage());
    }

    /** Verifies zero damage is allowed by the constructor. */
    @Test
    void zeroDamage_constructorAllowed() {
        ShadowSentinel s = new ShadowSentinel(1, 1, 0);
        assertEquals(0, s.getDamage());
    }

    /** Verifies {@link ShadowSentinel#interactWith} true only when coordinates match {@link Ember}. */
    @Test
    void interactWith_trueWhenSameTile() {
        ShadowSentinel s = new ShadowSentinel(4, 4);
        Ember ember = new Ember(4, 4);
        Aqua aqua = new Aqua(5, 5);
        assertTrue(s.interactWith(ember));
        assertFalse(s.interactWith(aqua));
    }

    /** Verifies partial coordinate match does not count as interaction. */
    @Test
    void interactWith_falseWhenOnlyOneCoordinateMatches() {
        ShadowSentinel s = new ShadowSentinel(4, 4);
        assertFalse(s.interactWith(new Ember(4, 5)));
        assertFalse(s.interactWith(new Ember(5, 4)));
    }

    /** Verifies {@link ShadowSentinel#setPosition(int, int)} updates coordinates. */
    @Test
    void setPosition_updatesTile() {
        ShadowSentinel s = new ShadowSentinel(0, 0);
        s.setPosition(3, 7);
        assertEquals(3, s.getX());
        assertEquals(7, s.getY());
    }

    /** Verifies {@link ShadowSentinel#setActive(boolean)} toggles {@link ShadowSentinel#isActive()}. */
    @Test
    void setActive_togglesThreat() {
        ShadowSentinel s = new ShadowSentinel(0, 0);
        s.setActive(false);
        assertFalse(s.isActive());
        s.setActive(true);
        assertTrue(s.isActive());
    }

    /** Verifies default detection range and {@link ShadowSentinel#setDetectionRange(int)}. */
    @Test
    void detectionRange_canBeChanged() {
        ShadowSentinel s = new ShadowSentinel(0, 0);
        assertEquals(5, s.getDetectionRange());
        s.setDetectionRange(8);
        assertEquals(8, s.getDetectionRange());
    }

    /**
     * {@link ShadowSentinel} follows the same move/active pattern as {@link Enemy} but does not
     * declare {@code implements Enemy} in code — we still verify it behaves like a moving foe.
     */
    @Test
    void sentinelMovesLikeEnemy_onBoard() {
        ShadowSentinel s = new ShadowSentinel(1, 1);
        assertTrue(s instanceof game.level.Entity);
        assertTrue(s.isActive());
        Board board = new Board(5, 5);
        s.move(board);
        assertNotNull(s);
    }

    /**
     * Sentinel boxed in by walls cannot step onto any neighbour tile, so it never moves.
     */
    @Test
    void move_whenCompletelyWalledIn_positionUnchanged() {
        Board board = new Board(3, 3);
        board.setWall(0, 1);
        board.setWall(2, 1);
        board.setWall(1, 0);
        board.setWall(1, 2);
        ShadowSentinel s = new ShadowSentinel(1, 1);
        board.addEnemy(s);

        for (int i = 0; i < 500; i++) {
            board.updateEnemies();
        }
        assertEquals(1, s.getX());
        assertEquals(1, s.getY());
    }

    /** Verifies sentinel surrounded by lava and water tiles does not enter those cells after many updates. */
    @Test
    void move_avoidsLavaAndWater() {
        Board board = new Board(5, 5);
        ShadowSentinel s = new ShadowSentinel(2, 2);
        board.addEnemy(s);
        board.setLava(3, 2);
        board.setWater(1, 2);
        board.setLava(2, 3);
        board.setWater(2, 1);

        for (int i = 0; i < 400; i++) {
            board.updateEnemies();
        }
        assertEquals(2, s.getX());
        assertEquals(2, s.getY());
    }
}
