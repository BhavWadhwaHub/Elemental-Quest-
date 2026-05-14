package game.character;

import game.level.Board;
import game.util.Direction;

/**
 * Ember and Aqua instances, which one is active, and input delegation.
 * Package-private helper for {@link Player}.
 */
final class CharacterRoster {

    private final Ember ember;
    private final Aqua aqua;
    private ElementalCharacter activeCharacter;

    CharacterRoster(int emberX, int emberY, int aquaX, int aquaY) {
        ember = new Ember(emberX, emberY);
        aqua = new Aqua(aquaX, aquaY);
        activeCharacter = ember;
        ember.setActive(true);
    }

    boolean isEmberActive() {
        return activeCharacter == ember;
    }

    void switchCharacter() {
        if (activeCharacter == ember) {
            ember.setActive(false);
            aqua.setActive(true);
            activeCharacter = aqua;
        } else {
            aqua.setActive(false);
            ember.setActive(true);
            activeCharacter = ember;
        }
    }

    boolean move(Board board, Direction direction) {
        return activeCharacter.move(board, direction);
    }

    void useAbility(Board board, Direction direction) {
        activeCharacter.useAbility(board, direction);
    }

    void resetPositions(int emberX, int emberY, int aquaX, int aquaY) {
        ember.setPosition(emberX, emberY);
        aqua.setPosition(aquaX, aquaY);
        ember.setActive(true);
        aqua.setActive(false);
        activeCharacter = ember;
    }

    Ember getEmber() {
        return ember;
    }

    Aqua getAqua() {
        return aqua;
    }

    ElementalCharacter getActiveCharacter() {
        return activeCharacter;
    }
}
