package game.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Animated border used on the main menu.
 * Draws wall tiles along the left and right edges, then bounces
 * Ember, Aqua, and a Shadow Sentinel up and down those rails.
 */
public class BorderAnimation {

    private BufferedImage borderTile;
    private BufferedImage sentinelPNG;

    private int animFrame = 0;
    private final int speed = 4; // pixels advanced per frame

    public BorderAnimation() {
        borderTile  = TilesetLoader.get("block");
        sentinelPNG = TilesetLoader.get("blob_1");
    }

    /** Advance one frame. Call once per timer tick. */
    public void update() {
        animFrame++;
    }

    /**
     * Draws the border tiles and animated characters onto the given graphics context.
     *
     * @param g the destination graphics
     * @param W panel width  in pixels
     * @param H panel height in pixels
     */
    public void draw(Graphics2D g, int W, int H) {
        int tile = PixelArtRenderer.TILE_SIZE;

        // Static wall tiles along both edges
        for (int y = 0; y < H; y += tile) {
            g.drawImage(borderTile, 0,        y, tile, tile, null);
            g.drawImage(borderTile, W - tile, y, tile, tile, null);
        }

        // Three sprites travelling up and down the two rails, offset from each other
        int rail  = H;
        int cycle = rail * 2;
        int p = (animFrame * speed) % cycle;

        Point sentPos  = traceTwoRailUpDown(p,              W, H); // leads
        Point emberPos = traceTwoRailUpDown((p + 200) % cycle, W, H); // chases sentinel
        Point aquaPos  = traceTwoRailUpDown((p + 400) % cycle, W, H); // chases ember

        PixelArtRenderer.drawEmber(g, emberPos.x, emberPos.y, true);
        PixelArtRenderer.drawAqua(g,  aquaPos.x,  aquaPos.y,  true);

        if (sentinelPNG != null)
            g.drawImage(sentinelPNG, sentPos.x, sentPos.y, tile, tile, null);
    }

    /**
     * Maps a position along the two-rail track to a screen coordinate.
     * Right side goes top→bottom; left side goes bottom→top.
     */
    private Point traceTwoRailUpDown(int p, int W, int H) {
        int tile = PixelArtRenderer.TILE_SIZE;
        int rail = H;

        if (p < rail) {
            // Right edge: travelling downward
            return new Point(W - tile, p);
        } else {
            // Left edge: travelling upward
            return new Point(0, H - (p - rail));
        }
    }
}
