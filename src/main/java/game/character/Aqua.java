package game.character;

import game.level.Board;
import game.level.Cell;
import game.util.Direction;
import game.util.ElementType;

/**
 * The water character. Aqua is immune to water but takes damage from lava.
 * Ability: destroys traps or lava tiles in front of her, or places water.
 */
public class Aqua extends ElementalCharacter {

    private boolean waterShieldActive;

    /**
     * Places Aqua at the given starting tile with her water shield inactive.
     *
     * @param x starting column
     * @param y starting row
     */
    public Aqua(int x, int y) {
        super(x, y, ElementType.WATER);
        this.waterShieldActive = false;
    }

    /**
     * Aqua's special ability — targets the tile directly in front of her:
     * <ul>
     *   <li>Destroys a spike trap (washes it away)</li>
     *   <li>Converts a lava tile to normal floor (quenches it)</li>
     *   <li>Floods an empty or sentinel-occupied tile with water</li>
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
            // Washes away a spike trap
            targetCell.setTrap(false);
            abilityCharges--;
        } else if (targetCell.isLava()) {
            // Quenches lava
            targetCell.setLava(false);
            abilityCharges--;
        } else if (!targetCell.isWall() && (targetCell.isEmpty() || isSentinelOnly(targetCell))) {
            // Floods an empty or sentinel-occupied tile with water
            targetCell.setWater(true);
            abilityCharges--;
        }
    }

    /** Flips the water shield on or off (reserved for future mechanics). */
    public void    toggleWaterShield()   { waterShieldActive = !waterShieldActive; }

    /** Returns {@code true} if Aqua's water shield is currently active. */
    public boolean isWaterShieldActive() { return waterShieldActive; }

    /** Returns the type identifier {@code "AQUA"} used by the renderer. */
    public String  getType()             { return "AQUA"; }
}
