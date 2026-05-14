package game.collectible;

import game.character.Player;
import game.level.Entity;

/**
 * An optional gold bonus crystal — either character can grab it.
 * Worth 100 points but doesn't count toward level completion.
 */
public class BonusCrystal implements Collectible, Entity {

    private int x, y;
    private boolean collected;

    /**
     * Places a bonus crystal at the given tile.
     * Either character can grab it for 100 points.
     *
     * @param x column on the board
     * @param y row on the board
     */
    public BonusCrystal(int x, int y) {
        this.x = x;
        this.y = y;
        this.collected = false;
    }

    @Override
    public void collect(Player player) {
        if (!collected) {
            collected = true;
        }
    }

    @Override
    public int getPointValue() {
        return 100;
    }

    @Override
    public boolean isCollected() {
        return collected;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String getType() {
        return "CRYSTAL_BONUS";
    }
}
