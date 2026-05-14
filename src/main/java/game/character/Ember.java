package game.character;

import game.level.Board;
import game.level.Cell;
import game.util.Direction;
import game.util.ElementType;

/**
 * The fire character. Ember is immune to lava but takes damage from water.
 * Ability: destroys traps or water tiles in front of her, or places lava.
 */
public class Ember extends ElementalCharacter {

    private boolean fireTrailActive;

    /**
     * Places Ember at the given starting tile with her fire trail inactive.
     *
     * @param x starting column
     * @param y starting row
     */
    public Ember(int x, int y) {
        super(x, y, ElementType.FIRE);
        this.fireTrailActive = false;
    }

    /**
     * Ember's special ability — targets the tile directly in front of her:
     * <ul>
     *   <li>Burns away a spike trap</li>
     *   <li>Evaporates a water tile (makes it safe for Ember)</li>
     *   <li>Places lava on an empty or sentinel-occupied tile</li>
     * </ul>
     * Consumes one ability charge. Does nothing if charges are empty.
     */
    @Override
    public void useAbility(Board board, Direction direction) {
        if (abilityCharges <= 0) return;

        int targetX = x + direction.getDx();
        int targetY = y + direction.getDy();
        Cell targetCell = board.getCell(targetX, targetY);
        if (targetCell == null) return;

        if (targetCell.isTrap()) {
            // Burns away a spike trap
            targetCell.setTrap(false);
            abilityCharges--;
        } else if (targetCell.isWater()) {
            // Evaporates water
            targetCell.setWater(false);
            abilityCharges--;
        } else if (!targetCell.isWall() && (targetCell.isEmpty() || isSentinelOnly(targetCell))) {
            // Places lava on an empty or sentinel-occupied tile
            targetCell.setLava(true);
            abilityCharges--;
        }
    }

    /** Flips the fire trail on or off (reserved for future mechanics). */
    public void    toggleFireTrail()   { fireTrailActive = !fireTrailActive; }

    /** Returns {@code true} if Ember's fire trail is currently active. */
    public boolean isFireTrailActive() { return fireTrailActive; }

    /** Returns the type identifier {@code "EMBER"} used by the renderer. */
    public String  getType()           { return "EMBER"; }
}
