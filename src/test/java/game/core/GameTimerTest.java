package game.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GameTimer}: countdown, start/stop, reset, progress, and edge cases.
 */
class GameTimerTest {

    /** Initial state reflects total seconds, progress 1.0, and not running or expired. */
    @Test
    void testInitialState() {
        GameTimer timer = new GameTimer(10, 2);
        assertFalse(timer.isRunning());
        assertEquals(10, timer.getTimeRemaining());
        assertEquals(10, timer.getTotal());
        assertEquals(1.0, timer.getProgress(), 0.0001);
        assertFalse(timer.hasExpired());
    }

    /** {@link GameTimer#start()} and {@link GameTimer#stop()} toggle the running flag. */
    @Test
    void testStartAndStop() {
        GameTimer timer = new GameTimer(5, 1);
        timer.start();
        assertTrue(timer.isRunning());
        timer.stop();
        assertFalse(timer.isRunning());
    }

    /** Updates decrease remaining time according to ticks-per-second until expiry. */
    @Test
    void testUpdateCountsDown() {
        GameTimer timer = new GameTimer(3, 2);
        timer.start();

        // Tick once: not enough to decrease seconds
        timer.update();
        assertEquals(3, timer.getTimeRemaining());

        // Tick second time: should decrease by 1 second
        timer.update();
        assertEquals(2, timer.getTimeRemaining());

        // Tick 4 more times: should reach 0
        timer.update();
        timer.update();
        timer.update();
        timer.update();
        assertEquals(0, timer.getTimeRemaining());
        assertTrue(timer.hasExpired());
    }

    /** {@link GameTimer#reset()} restores full time and clears expired state. */
    @Test
    void testReset() {
        GameTimer timer = new GameTimer(5);
        timer.start();
        timer.update();
        timer.update();
        timer.reset();
        assertEquals(5, timer.getTimeRemaining());
        assertFalse(timer.hasExpired());
    }

    /** Progress reflects fraction of time remaining vs total. */
    @Test
    void testProgress() {
        GameTimer timer = new GameTimer(4, 1);
        timer.start();

        timer.update(); // 1 tick -> decrease by 1 sec
        timer.update();
        assertEquals(0.5, timer.getProgress(), 0.0001);
    }

    /** Updates while stopped do not change remaining time. */
    @Test
    void testUpdateDoesNothingIfStopped() {
        GameTimer timer = new GameTimer(2);
        timer.update();
        assertEquals(2, timer.getTimeRemaining());
    }

    /** After expiry, further updates leave time at zero. */
    @Test
    void testUpdateDoesNothingAfterExpiration() {
        GameTimer timer = new GameTimer(1, 1);
        timer.start();
        timer.update(); // 1 tick -> expires
        timer.update(); // should do nothing
        assertEquals(0, timer.getTimeRemaining());
        assertTrue(timer.hasExpired());
    }

    /** Zero-second timer is already expired once started. */
    @Test
    void zeroSecondTimer_startsExpired() {
        GameTimer timer = new GameTimer(0, 1);
        timer.start();
        assertTrue(timer.hasExpired());
        assertEquals(0, timer.getTimeRemaining());
        assertEquals(0.0, timer.getProgress(), 0.0001);
    }

    /** Many updates while never started never change remaining time. */
    @Test
    void updateWhileStopped_neverChangesTime() {
        GameTimer timer = new GameTimer(10, 1);
        for (int i = 0; i < 100; i++) {
            timer.update();
        }
        assertEquals(10, timer.getTimeRemaining());
    }

    /** Negative total seconds is treated as expired; updates do not throw. */
    @Test
    void negativeTotalSeconds_treatedAsAlreadyExpired() {
        GameTimer timer = new GameTimer(-1, 1);
        assertTrue(timer.hasExpired());
        assertEquals(0.0, timer.getProgress(), 0.0001);
        timer.start();
        assertDoesNotThrow(() -> timer.update());
        assertTrue(timer.hasExpired());
    }

    /** High ticks-per-second requires many updates before a whole second elapses. */
    @Test
    void veryHighTicksPerSecond_singleUpdateDoesNotDecrementSecond() {
        GameTimer timer = new GameTimer(5, 10_000);
        timer.start();
        for (int i = 0; i < 9999; i++) {
            timer.update();
        }
        assertEquals(5, timer.getTimeRemaining());
        timer.update();
        assertEquals(4, timer.getTimeRemaining());
    }

    /** Reset after expiry restores running eligibility and full duration. */
    @Test
    void reset_afterExpiry_restoresFullTime() {
        GameTimer timer = new GameTimer(2, 1);
        timer.start();
        timer.update();
        timer.update();
        assertTrue(timer.hasExpired());
        timer.reset();
        assertEquals(2, timer.getTimeRemaining());
        assertFalse(timer.hasExpired());
    }

    /** Calling {@link GameTimer#start()} twice leaves the timer running. */
    @Test
    void start_calledTwice_stillRuns() {
        GameTimer timer = new GameTimer(3, 1);
        timer.start();
        timer.start();
        assertTrue(timer.isRunning());
    }
}
