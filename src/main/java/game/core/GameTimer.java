package game.core;

/**
 * Counts down the seconds remaining in a level.
 * Ticks are driven by the game loop — each call to {@link #update()} is one game-loop step.
 */
public class GameTimer {

    private int totalSeconds;
    private int remainingSeconds;
    private boolean running;
    private int tickCounter;
    private int ticksPerSecond;

    /**
     * Creates a timer with the default rate of 60 ticks per second.
     *
     * @param totalSeconds level time limit in seconds
     */
    public GameTimer(int totalSeconds) {
        this(totalSeconds, 60);
    }

    /**
     * @param totalSeconds   level time limit in seconds
     * @param ticksPerSecond how many game-loop ticks make one second
     */
    public GameTimer(int totalSeconds, int ticksPerSecond) {
        this.totalSeconds     = totalSeconds;
        this.remainingSeconds = totalSeconds;
        this.ticksPerSecond   = ticksPerSecond;
        this.running          = false;
        this.tickCounter      = 0;
    }

    /** Advances the clock by one game tick. Call this once per loop iteration. */
    public void update() {
        if (!running || remainingSeconds <= 0) return;
        tickCounter++;
        if (tickCounter >= ticksPerSecond) {
            tickCounter = 0;
            remainingSeconds--;
        }
    }

    /** Starts the countdown. Safe to call even if already running. */
    public void start() { running = true; }

    /** Pauses the countdown without resetting it. */
    public void stop()  { running = false; }

    /** Restores the remaining time to the full limit and clears the tick counter. */
    public void reset() { remainingSeconds = totalSeconds; tickCounter = 0; }

    /** Returns true once the countdown has hit zero. */
    public boolean hasExpired()       { return remainingSeconds <= 0; }

    /** Returns {@code true} while the timer is actively counting down. */
    public boolean isRunning()        { return running; }

    /** Seconds left on the clock. Decreases each second; stops at zero. */
    public int     getTimeRemaining() { return remainingSeconds; }

    /** The original time limit this timer was created with, in seconds. */
    public int     getTotal()         { return totalSeconds; }

    /** Time remaining as a fraction from 0.0 to 1.0 — useful for progress bars. */
    public double  getProgress()      { return totalSeconds > 0 ? (double) remainingSeconds / totalSeconds : 0; }
}
