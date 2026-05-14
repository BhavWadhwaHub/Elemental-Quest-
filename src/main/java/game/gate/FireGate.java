package game.gate;

import game.character.Player;
import game.level.Entity;
import game.util.ElementType;

/**
 * The exit gate for Ember. Becomes passable once enough fire crystals are collected.
 */
public class FireGate implements Gate, Entity {

    private int x, y;
    private boolean unlocked;

    /**
     * Places the fire gate at the given tile, initially locked.
     *
     * @param x column on the board
     * @param y row on the board
     */
    public FireGate(int x, int y) {
        this.x = x;
        this.y = y;
        this.unlocked = false;
    }

    @Override
    public boolean isUnlocked() {
        return unlocked;
    }

    @Override
    public void unlock() {
        unlocked = true;
    }

    @Override
    public void interact(Player player) {
        if (unlocked) {
            player.setEmberAtGate(true);
        }
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
        return "GATE_FIRE";
    }

    public ElementType getElementType() {
        return ElementType.FIRE;
    }
}
