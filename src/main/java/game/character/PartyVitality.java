package game.character;

/**
 * Per-character lives and exit-gate flags for the two-character party.
 * Package-private helper for {@link Player}.
 */
final class PartyVitality {

    private int emberLives;
    private int aquaLives;
    private final int maxLives;
    private boolean emberAtGate;
    private boolean aquaAtGate;

    PartyVitality(int startingLives) {
        maxLives = startingLives;
        emberLives = startingLives;
        aquaLives = startingLives;
        emberAtGate = false;
        aquaAtGate = false;
    }

    void takeDamageForActiveCharacter(boolean emberIsActive) {
        if (emberIsActive) {
            emberLives--;
        } else {
            aquaLives--;
        }
    }

    void takeEmberDamage() {
        emberLives--;
    }

    void takeAquaDamage() {
        aquaLives--;
    }

    boolean isAlive() {
        return emberLives > 0 && aquaLives > 0;
    }

    void setEmberAtGate(boolean atGate) {
        emberAtGate = atGate;
    }

    void setAquaAtGate(boolean atGate) {
        aquaAtGate = atGate;
    }

    boolean isEmberAtGate() {
        return emberAtGate;
    }

    boolean isAquaAtGate() {
        return aquaAtGate;
    }

    boolean bothAtGates() {
        return emberAtGate && aquaAtGate;
    }

    void reset() {
        emberLives = maxLives;
        aquaLives = maxLives;
        emberAtGate = false;
        aquaAtGate = false;
    }

    int getLivesForActiveCharacter(boolean emberIsActive) {
        return emberIsActive ? emberLives : aquaLives;
    }

    int getEmberLives() {
        return emberLives;
    }

    int getAquaLives() {
        return aquaLives;
    }

    int getMaxLives() {
        return maxLives;
    }
}
