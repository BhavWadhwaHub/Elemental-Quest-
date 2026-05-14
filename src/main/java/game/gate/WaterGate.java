package game.gate;

import game.character.Player;
import game.level.Entity;
import game.util.ElementType;

/**
 * The exit gate for Aqua. Becomes passable once enough water crystals are collected.
 */
public class WaterGate implements Gate, Entity {

    private int x, y;
    private boolean unlocked;

    /**
     * Places the water gate at the given tile, initially locked.
     *
     * @param x column on the board
     * @param y row on the board
     */
    public WaterGate(int x, int y) {
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
            player.setAquaAtGate(true);
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
        return "GATE_WATER";
    }

    public ElementType getElementType() {
        return ElementType.WATER;
    }
}
