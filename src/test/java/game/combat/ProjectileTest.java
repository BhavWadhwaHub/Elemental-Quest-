package game.combat;

import game.util.Direction;
import game.util.ElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Projectile} construction, tick cadence, movement, activation, and element typing.
 */
class ProjectileTest {

    private Projectile fireball;
    private Projectile waterball;

    @BeforeEach
    void setUp() {
        fireball = new Projectile(0, 0, Direction.UP, ElementType.FIRE);
        waterball = new Projectile(5, 5, Direction.RIGHT, ElementType.WATER);
    }

    /** Verifies constructor sets position, direction, element type, fireball flag, and active state. */
    @Test
    void constructor_initializesFieldsCorrectly() {
        assertEquals(0, fireball.getX());
        assertEquals(0, fireball.getY());
        assertEquals(Direction.UP, fireball.getDirection());
        assertEquals(ElementType.FIRE, fireball.getElementType());
        assertTrue(fireball.isFireball());
        assertTrue(fireball.isActive());

        assertEquals(5, waterball.getX());
        assertEquals(5, waterball.getY());
        assertEquals(Direction.RIGHT, waterball.getDirection());
        assertEquals(ElementType.WATER, waterball.getElementType());
        assertFalse(waterball.isFireball());
        assertTrue(waterball.isActive());
    }

    /** Verifies {@link Projectile#tick()} returns true every move interval then resets counter. */
    @Test
    void tick_returnsFalseUntilInterval() {
        // tickCounter = 0 initially, MOVE_INTERVAL = 6
        for (int i = 0; i < 5; i++) {
            assertFalse(fireball.tick(), "Tick " + i + " should return false");
        }
        // 6th tick returns true
        assertTrue(fireball.tick(), "6th tick should return true");
        // After reset, first tick again returns false
        assertFalse(fireball.tick(), "Tick after reset should return false");
    }

    /** Verifies inactive projectile {@link Projectile#tick()} always returns false. */
    @Test
    void tick_returnsFalseIfInactive() {
        fireball.deactivate();
        for (int i = 0; i < 10; i++) {
            assertFalse(fireball.tick(), "Inactive projectile tick should always return false");
        }
    }

    /** Verifies {@link Projectile#advance()} moves coordinates along fire and water sample directions. */
    @Test
    void advance_movesProjectileCorrectly() {
        // Fireball starts at (0,0), moving UP
        fireball.advance();
        assertEquals(0, fireball.getX());
        assertEquals(-1, fireball.getY());

        // Waterball starts at (5,5), moving RIGHT
        waterball.advance();
        assertEquals(6, waterball.getX());
        assertEquals(5, waterball.getY());
    }

    /** Verifies {@link Projectile#deactivate()} clears {@link Projectile#isActive()}. */
    @Test
    void deactivate_marksProjectileInactive() {
        assertTrue(fireball.isActive());
        fireball.deactivate();
        assertFalse(fireball.isActive());
    }

    /** Verifies direction and element getters for sample projectiles. */
    @Test
    void getters_returnCorrectValues() {
        assertEquals(Direction.UP, fireball.getDirection());
        assertEquals(ElementType.FIRE, fireball.getElementType());
        assertEquals(Direction.RIGHT, waterball.getDirection());
        assertEquals(ElementType.WATER, waterball.getElementType());
    }

    /** Verifies {@link Projectile#isFireball()} matches element type. */
    @Test
    void isFireball_checksElementType() {
        assertTrue(fireball.isFireball());
        assertFalse(waterball.isFireball());
    }

    /** Verifies many {@link Projectile#advance()} steps accumulate position and stay active. */
    @Test
    void advance_manySteps_extremeCoordinatesStillConsistent() {
        Projectile p = new Projectile(0, 0, Direction.RIGHT, ElementType.FIRE);
        for (int i = 0; i < 1000; i++) {
            p.advance();
        }
        assertEquals(1000, p.getX());
        assertEquals(0, p.getY());
        assertTrue(p.isActive());
    }

    /** Verifies many tick cycles leave projectile active. */
    @Test
    void tick_afterManyTrueIntervals_keepsWorking() {
        Projectile p = new Projectile(0, 0, Direction.DOWN, ElementType.WATER);
        for (int i = 0; i < 60; i++) {
            p.tick();
        }
        assertTrue(p.isActive());
    }
}
