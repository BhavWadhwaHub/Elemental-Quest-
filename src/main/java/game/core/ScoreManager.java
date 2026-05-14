package game.core;

/**
 * Tracks the player's score during a run.
 * Has separate helpers for each scoring event so the caller code reads naturally.
 * Score can go negative (wrong crystal penalty); use {@link #isNegative()} to check.
 */
public class ScoreManager {

    private int score;
    private int highScore;
    private int multiplier;
    private int gemsCollected;

    /**
     * Creates a fresh ScoreManager with everything zeroed out and
     * the multiplier set to 1. Call {@link #reset()} between levels
     * to preserve the high score while clearing the current run's tally.
     */
    public ScoreManager() {
        this.score         = 0;
        this.highScore     = 0;
        this.multiplier    = 1;
        this.gemsCollected = 0;
    }

    /** Generic add — applies the current multiplier and updates the high score. */
    public void addPoints(int points) {
        score += points * multiplier;
        if (score > highScore) highScore = score;
    }

    /** Deducts points (e.g. wrong crystal penalty). Score can go negative. */
    public void deductPoints(int points) {
        score -= points;
    }

    /** Returns true if the score has dropped below zero. */
    public boolean isNegative() { return score < 0; }

    /** Awarded when an elemental crystal is collected. */
    public void addCrystalPoints(int points) {
        addPoints(points);
    }

    /** Awarded when a bonus gem is collected. Also increments the gem counter. */
    public void addGemPoints(int points) {
        gemsCollected++;
        addPoints(points);
    }

    /** Awarded when a Shadow Sentinel is killed. */
    public void addKillPoints(int points) {
        addPoints(points);
    }

    /** Awarded when a spike trap is destroyed. */
    public void addTrapPoints(int points) {
        addPoints(points);
    }

    /** Bonus points for finishing a level before the timer runs out. */
    public void addTimeBonus(int secondsLeft) {
        addPoints(secondsLeft * 10);
    }

    /** Resets the current score and gem count (high score is kept). */
    public void reset() {
        score         = 0;
        multiplier    = 1;
        gemsCollected = 0;
    }

    /** Current score for this run (can be negative after a penalty). */
    public int getScore()         { return score; }

    /** All-time best score seen since the ScoreManager was created. */
    public int getHighScore()     { return highScore; }

    /** Current point multiplier — raised by {@link #setMultiplier(int)}. */
    public int getMultiplier()    { return multiplier; }

    /** How many bonus gems have been collected this run. */
    public int getGemsCollected() { return gemsCollected; }

    /** Stacking multipliers make late-game crystals worth more. */
    public void setMultiplier(int multiplier) {
        this.multiplier = Math.max(1, multiplier);
    }
}
