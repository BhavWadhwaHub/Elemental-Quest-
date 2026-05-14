package game.enemy;

import game.character.ElementalCharacter;

/**
 * Represents any environmental or enemy hazard capable of harming or interacting
 * with an {@link ElementalCharacter}. Hazards define how they respond when a
 * character comes into contact with them and how much damage they inflict.
 * <p>
 * Implementations may include traps, elemental obstacles, enemy attacks, or
 * environmental dangers that apply damage or trigger special effects.
 */
public interface Hazard {

    /**
     * Handles the interaction between this hazard and the given character.
     * <p>
     * Implementations decide whether the hazard activates, whether the character
     * takes damage, and whether the hazard persists afterward.
     *
     * @param character the character interacting with the hazard
     * @return {@code true} if the interaction successfully triggered the hazard's
     *         effect; {@code false} if nothing happened
     */
    boolean interactWith(ElementalCharacter character);

    /**
     * Returns the amount of damage this hazard inflicts when triggered.
     *
     * @return the damage value applied to the character
     */
    int getDamage();
}