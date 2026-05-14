package game.character;

import game.level.Board;
import game.util.Direction;

/**
 * Facade for the two-character party: delegates character control to {@link CharacterRoster}
 * and lives / gate progress to {@link PartyVitality}.
 */
public class Player {

    private static final int STARTING_LIVES = 5;

    private final CharacterRoster roster;
    private final PartyVitality vitality;

    /**
     * Creates a fresh player with both characters placed at their starting tiles,
     * Ember set as the active character, and 5 lives on the clock.
     *
     * @param emberX Ember's starting column
     * @param emberY Ember's starting row
     * @param aquaX  Aqua's starting column
     * @param aquaY  Aqua's starting row
     */
    public Player(int emberX, int emberY, int aquaX, int aquaY) {
        roster = new CharacterRoster(emberX, emberY, aquaX, aquaY);
        vitality = new PartyVitality(STARTING_LIVES);
    }

    /** Switches between Ember and Aqua (single-player only). */
    public void switchCharacter() {
        roster.switchCharacter();
    }

    /** Delegates movement to the currently active character. */
    public boolean move(Board board, Direction direction) {
        return roster.move(board, direction);
    }

    /** Delegates ability use to the currently active character. */
    public void useAbility(Board board, Direction direction) {
        roster.useAbility(board, direction);
    }

    /** Loses one life. */
    public void takeDamage() {
        vitality.takeDamageForActiveCharacter(roster.isEmberActive());
    }

    public void takeEmberDamage() {
        vitality.takeEmberDamage();
    }

    public void takeAquaDamage() {
        vitality.takeAquaDamage();
    }

    /** Returns true while both characters are alive. */
    public boolean isAlive() {
        return vitality.isAlive();
    }

    public void setEmberAtGate(boolean atGate) {
        vitality.setEmberAtGate(atGate);
    }

    public void setAquaAtGate(boolean atGate) {
        vitality.setAquaAtGate(atGate);
    }

    public boolean isEmberAtGate() {
        return vitality.isEmberAtGate();
    }

    public boolean isAquaAtGate() {
        return vitality.isAquaAtGate();
    }

    /** Returns true when both characters are standing on their unlocked gates (2P win condition). */
    public boolean bothAtGates() {
        return vitality.bothAtGates();
    }

    /** Resets position and state — used when restarting a level. */
    public void reset(int emberX, int emberY, int aquaX, int aquaY) {
        roster.resetPositions(emberX, emberY, aquaX, aquaY);
        vitality.reset();
    }

    /** Returns the Ember character object (always exists, even in Aqua-only mode). */
    public Ember getEmber() {
        return roster.getEmber();
    }

    /** Returns the Aqua character object (always exists, even in Ember-only mode). */
    public Aqua getAqua() {
        return roster.getAqua();
    }

    /** Returns whichever character is currently taking player input. */
    public ElementalCharacter getActiveCharacter() {
        return roster.getActiveCharacter();
    }

    /** Current number of lives remaining for the active character. */
    public int getLives() {
        return vitality.getLivesForActiveCharacter(roster.isEmberActive());
    }

    public int getEmberLives() {
        return vitality.getEmberLives();
    }

    public int getAquaLives() {
        return vitality.getAquaLives();
    }

    /** The starting life count — also the maximum after a reset. */
    public int getMaxLives() {
        return vitality.getMaxLives();
    }
}
