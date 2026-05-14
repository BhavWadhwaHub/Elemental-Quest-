package game.collectible;

import game.character.Player;
import game.level.Entity;
import game.util.ElementType;

/**
 * A fire or water elemental crystal. Collecting the required number unlocks the exit gates.
 * Worth 100 points each.
 */
public class ElementalCrystal implements Collectible, Entity {

    private int x, y;
    private ElementType elementType;
    private boolean collected;
    private int pointValue;

    /**
     * Places an elemental crystal at the given tile.
     *
     * @param x           column on the board
     * @param y           row on the board
     * @param elementType FIRE or WATER — determines which character should collect it
     */
    public ElementalCrystal(int x, int y, ElementType elementType) {
        this.x = x;
        this.y = y;
        this.elementType = elementType;
        this.collected = false;
        this.pointValue = 100;
    }

    @Override
    public void collect(Player player) {
        if (!collected) {
            collected = true;
        }
    }

    @Override
    public int getPointValue() {
        return pointValue;
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
        return "CRYSTAL_" + elementType.name();
    }

    public ElementType getElementType() {
        return elementType;
    }
}
