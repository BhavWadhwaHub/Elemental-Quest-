package game.ui;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Draws everything on the board using a pixel-art style.
 * Falls back to procedural shapes when tileset images aren't available.
 */
public class PixelArtRenderer {

    public static final int TILE_SIZE = 32;

    // Color palette used across the whole game
    public static final Color NEON_PINK    = new Color(255,   0, 128);
    public static final Color NEON_CYAN    = new Color(  0, 255, 255);
    public static final Color NEON_PURPLE  = new Color(148,   0, 211);
    public static final Color NEON_ORANGE  = new Color(255, 102,   0);
    public static final Color NEON_GREEN   = new Color( 57, 255,  20);
    public static final Color DARK_BG      = new Color( 15,  15,  35);
    public static final Color DARK_PURPLE  = new Color( 30,  20,  50);
    public static final Color WALL_COLOR      = new Color(45,  35,  75);
    public static final Color WALL_HIGHLIGHT  = new Color(80,  60, 120);
    public static final Color LAVA_BASE    = new Color(255,  69,   0);
    public static final Color LAVA_GLOW    = new Color(255, 140,   0);
    public static final Color WATER_BASE   = new Color(  0, 120, 220);
    public static final Color WATER_GLOW   = new Color(100, 220, 255);
    public static final Color CRYSTAL_FIRE  = new Color(255, 100,  50);
    public static final Color CRYSTAL_WATER = new Color( 50, 150, 255);
    public static final Color GEM_RED    = new Color(255,  50, 100);
    public static final Color GEM_BLUE   = new Color( 50, 100, 255);
    public static final Color GEM_GREEN  = new Color( 50, 255, 100);
    public static final Color GEM_YELLOW = new Color(255, 255,  50);
    public static final Color GEM_WHITE  = new Color(230, 230, 255);
    public static final Color GATE_LOCKED  = new Color(100, 100, 100);
    public static final Color ENEMY_COLOR  = new Color(100,   0, 150);
    public static final Color ENEMY_GLOW   = new Color(180,  50, 220);

    // Alpha composites cached so we don't allocate them every frame
    private static final AlphaComposite AC_LAVA_IMG   = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f);
    private static final AlphaComposite AC_LAVA_TINT  = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.35f);
    private static final AlphaComposite AC_WATER_IMG  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f);
    private static final AlphaComposite AC_WATER_TINT = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.50f);
    private static final AlphaComposite AC_ENEMY_TINT = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.40f);
    private static final AlphaComposite AC_GEM_TINT   = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.55f);

    // Strokes cached for the same reason
    private static final BasicStroke STROKE_1   = new BasicStroke(1f);
    private static final BasicStroke STROKE_1_5 = new BasicStroke(1.5f);
    private static final BasicStroke STROKE_2   = new BasicStroke(2f);
    private static final BasicStroke STROKE_2_5 = new BasicStroke(2.5f);

    private static int animFrame = 0;

    /** Advances the animation counter. Called once per timer tick. */
    public static void updateAnimation() {
        animFrame = (animFrame + 1) % 120;
        if (animFrame == 1) TilesetLoader.initialize();
    }

    public static int getTileSize() { return TILE_SIZE; }

    /**
     * Tries to draw a named tileset image at (x, y).
     * @return true if the image was found and drawn
     */
    private static boolean drawTile(Graphics2D g, String name, int x, int y) {
        BufferedImage img = TilesetLoader.get(name);
        if (img == null) return false;
        g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
        return true;
    }

    /** Plain floor tile — dark background with faint grid lines. */
    public static void drawFloor(Graphics2D g, int x, int y) {
        if (drawTile(g, "blank", x, y)) return;
        g.setColor(DARK_BG);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(new Color(40, 30, 60));
        g.drawRect(x, y, TILE_SIZE - 1, TILE_SIZE - 1);
        g.setColor(new Color(50, 40, 70));
        g.drawLine(x + TILE_SIZE/2, y, x + TILE_SIZE/2, y + TILE_SIZE);
        g.drawLine(x, y + TILE_SIZE/2, x + TILE_SIZE, y + TILE_SIZE/2);
    }

    /** Wall tile — bevelled block with a subtle neon outline. */
    public static void drawWall(Graphics2D g, int x, int y) {
        BufferedImage wallImg = TilesetLoader.getWallTile(x, y, TILE_SIZE);
        if (wallImg != null) {
            g.drawImage(wallImg, x, y, TILE_SIZE, TILE_SIZE, null);
            return;
        }
        // Fallback bevel
        g.setColor(WALL_COLOR);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(WALL_HIGHLIGHT);
        g.fillRect(x, y, TILE_SIZE - 4, 4);
        g.fillRect(x, y, 4, TILE_SIZE - 4);
        g.setColor(new Color(20, 15, 35));
        g.fillRect(x + TILE_SIZE - 4, y + 4, 4, TILE_SIZE - 4);
        g.fillRect(x + 4, y + TILE_SIZE - 4, TILE_SIZE - 4, 4);
        g.setColor(new Color(NEON_PURPLE.getRed(), NEON_PURPLE.getGreen(), NEON_PURPLE.getBlue(), 100));
        g.drawRect(x + 1, y + 1, TILE_SIZE - 3, TILE_SIZE - 3);
    }

    /** Animated lava tile — pulsing orange base with spike sprite overlay. */
    public static void drawLava(Graphics2D g, int x, int y) {
        float pulse = (float)(Math.sin(animFrame * 0.2) * 0.3 + 0.7);
        g.setColor(DARK_BG);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(new Color(255, 50, 0, (int)(80 * pulse)));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(LAVA_BASE);
        g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);

        BufferedImage spike = TilesetLoader.getSpikeFrame(animFrame);
        if (spike != null) {
            Composite old = g.getComposite();
            g.setComposite(AC_LAVA_IMG);
            g.drawImage(spike, x, y, TILE_SIZE, TILE_SIZE, null);
            // Warm tint so it reads as lava rather than generic spikes
            g.setComposite(AC_LAVA_TINT);
            g.setColor(new Color(255, 100, 0));
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            g.setComposite(old);
        } else {
            // Fallback: simple bubbling animation
            g.setColor(LAVA_GLOW);
            int bubbleOffset = (animFrame / 10) % 3;
            g.fillOval(x + 8 + bubbleOffset * 2, y + 10, 6, 6);
            g.fillOval(x + 18 - bubbleOffset, y + 18, 5, 5);
            g.setColor(new Color(255, 200, 100, (int)(150 * pulse)));
            g.fillOval(x + 10, y + 10, 12, 12);
        }
    }

    /** Animated water tile — rippling blue surface with black-hole sprite overlay. */
    public static void drawWater(Graphics2D g, int x, int y) {
        float wave = (float)(Math.sin(animFrame * 0.15 + x * 0.1) * 0.3 + 0.7);
        g.setColor(new Color(0, 100, 220));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(new Color(0, 150, 255, (int)(140 * wave)));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(new Color(0, 170, 255));
        g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);

        BufferedImage bh = TilesetLoader.getBlackHoleFrame(animFrame);
        if (bh != null) {
            Composite old = g.getComposite();
            g.setComposite(AC_WATER_IMG);
            g.drawImage(bh, x, y, TILE_SIZE, TILE_SIZE, null);
            g.setComposite(AC_WATER_TINT);
            g.setColor(new Color(0, 180, 255));
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            g.setComposite(old);
        } else {
            g.setColor(WATER_GLOW);
            int rippleOffset = (animFrame / 8) % 4;
            g.drawArc(x + 6, y + 6 + rippleOffset, 8, 8, 0, 360);
            g.setColor(new Color(150, 220, 255, (int)(100 * wave)));
            g.fillOval(x + 8, y + 8, 8, 4);
        }
    }

    /**
     * Spike trap tile — animated spikes on a metal plate.
     * Damages any character that steps on it.
     */
    public static void drawTrap(Graphics2D g, int x, int y) {
        float pulse = (float)(Math.sin(animFrame * 0.25) * 0.3 + 0.7);
        int flicker = animFrame % 6;

        // Dark base with danger glow
        g.setColor(new Color(25, 10, 10));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(new Color(255, 40, 40, (int)(60 * pulse)));
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        // Metal plate
        g.setColor(new Color(60, 55, 50));
        g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        g.setColor(new Color(80, 75, 65));
        g.fillRect(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6);

        // Corner rivets
        g.setColor(new Color(120, 110, 90));
        g.fillOval(x + 3, y + 3, 3, 3);
        g.fillOval(x + TILE_SIZE - 6, y + 3, 3, 3);
        g.fillOval(x + 3, y + TILE_SIZE - 6, 3, 3);
        g.fillOval(x + TILE_SIZE - 6, y + TILE_SIZE - 6, 3, 3);

        // 3×3 grid of animated spikes
        int spikeH = flicker < 3 ? 8 : 6;
        Color spikeBase = new Color(160, 160, 170);
        Color spikeTip  = new Color(220, 220, 230);
        for (int sx = 0; sx < 3; sx++) {
            for (int sy = 0; sy < 3; sy++) {
                int bx = x + 6 + sx * 8;
                int by = y + TILE_SIZE - 6 - sy * 2;
                g.setColor(spikeBase);
                int[] xP = {bx, bx + 3, bx + 6};
                int[] yP = {by, by - spikeH, by};
                g.fillPolygon(xP, yP, 3);
                g.setColor(spikeTip);
                g.fillOval(bx + 1, by - spikeH - 1, 4, 3);
            }
        }

        // Diagonal danger stripes
        g.setColor(new Color(200, 180, 0, (int)(90 * pulse)));
        Stroke old = g.getStroke();
        g.setStroke(STROKE_1_5);
        g.drawLine(x + 2, y + TILE_SIZE - 2, x + 6, y + TILE_SIZE - 6);
        g.drawLine(x + TILE_SIZE - 6, y + 2, x + TILE_SIZE - 2, y + 6);
        g.setStroke(old);

        // Pulsing red dot centred on the middle spike
        g.setColor(new Color(255, 0, 0, (int)(200 * pulse)));
        g.fillOval(x + TILE_SIZE / 2 - 1, y + TILE_SIZE / 2 - 2, 4, 4);
    }

    // ── Player characters ─────────────────────────────────────────────────────

    /**
     * Ember — fiery spirit with animated flame mohawk, amber eyes,
     * and floating ember particles. Visually distinct from Aqua.
     */
    public static void drawEmber(Graphics2D g, int x, int y, boolean active) {
        float glow  = active ? (float)(Math.sin(animFrame * 0.30) * 0.2 + 0.8) : 0.45f;
        int   flick = animFrame % 8;

        // Outer aura
        if (active) {
            g.setColor(new Color(255, 80, 0, (int)(70 * glow)));
            g.fillOval(x - 4, y - 4, TILE_SIZE + 8, TILE_SIZE + 8);
        }

        // Body
        g.setColor(new Color(180, 50, 0));
        g.fillOval(x + 7, y + 15, 18, 15);
        g.setColor(new Color(240, 90, 10));
        g.fillOval(x + 8, y + 16, 16, 13);
        g.setColor(new Color(255, 170, 60, 110));
        g.fillOval(x + 10, y + 18, 7, 4);

        // Head
        g.setColor(new Color(210, 70, 0));
        g.fillOval(x + 8, y + 5, 16, 16);
        g.setColor(new Color(255, 125, 20));
        g.fillOval(x + 9, y + 6, 14, 14);
        g.setColor(new Color(255, 210, 80, 100));
        g.fillOval(x + 11, y + 8, 6, 4);

        // Eyes — dark sockets + bright amber pupils
        g.setColor(new Color(70, 10, 0));
        g.fillOval(x + 10, y + 11, 4, 4);
        g.fillOval(x + 18, y + 11, 4, 4);
        g.setColor(new Color(255, 225, 80));
        g.fillOval(x + 11, y + 12, 2, 2);
        g.fillOval(x + 19, y + 12, 2, 2);
        g.setColor(new Color(255, 255, 200));
        g.fillRect(x + 12, y + 12, 1, 1);
        g.fillRect(x + 20, y + 12, 1, 1);

        // Flame mohawk — 3 animated spikes
        Color fBase = new Color(255, 145, 0);
        Color fTip  = new Color(255, 240, 120, 200);
        int lFy = y + 7 - (flick < 4 ? 1 : 0);
        g.setColor(fBase);  g.fillOval(x + 9,  lFy - 3, 5, 7);
        g.setColor(fTip);   g.fillOval(x + 10, lFy - 5, 3, 4);
        int cFy = y + 4 - (flick % 4 < 2 ? 2 : 0);
        g.setColor(new Color(255, 160, 0)); g.fillOval(x + 13, cFy - 2, 7, 9);
        g.setColor(fTip);                   g.fillOval(x + 14, cFy - 5, 4, 5);
        int rFy = y + 7 - (flick >= 4 ? 1 : 0);
        g.setColor(fBase);  g.fillOval(x + 18, rFy - 3, 5, 7);
        g.setColor(fTip);   g.fillOval(x + 19, rFy - 5, 3, 4);

        // Floating ember particles
        g.setColor(new Color(255, 190, 50, 190));
        g.fillOval(x + 3  + (animFrame % 5), y + 9  + (animFrame % 3), 2, 2);
        g.fillOval(x + 25 - (animFrame % 4), y + 11 + ((animFrame + 2) % 4), 2, 2);
        g.setColor(new Color(255, 120, 0, 140));
        g.fillOval(x + 5  + (animFrame % 3), y + 20 - (animFrame % 3), 2, 2);

        // Selection border
        if (active) {
            g.setColor(NEON_PINK);
            g.setStroke(STROKE_2);
            g.drawRoundRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, 8, 8);
            g.setStroke(STROKE_1);
        }
    }

    /**
     * Aqua — water spirit with large glowing eyes, droplet tendrils,
     * and floating bubble particles. Visually distinct from Ember.
     */
    public static void drawAqua(Graphics2D g, int x, int y, boolean active) {
        float wave = active ? (float)(Math.sin(animFrame * 0.25) * 0.2 + 0.8) : 0.45f;
        int   bob  = (animFrame / 6) % 4;

        // Outer aura
        if (active) {
            g.setColor(new Color(0, 180, 255, (int)(70 * wave)));
            g.fillOval(x - 4, y - 4, TILE_SIZE + 8, TILE_SIZE + 8);
        }

        // Body
        g.setColor(new Color(0, 55, 155));
        g.fillOval(x + 7, y + 15, 18, 15);
        g.setColor(new Color(0, 115, 215));
        g.fillOval(x + 8, y + 16, 16, 13);
        g.setColor(new Color(90, 210, 255, 120));
        g.fillOval(x + 10, y + 18, 7, 4);

        // Head
        g.setColor(new Color(0, 75, 175));
        g.fillOval(x + 8, y + 5, 16, 16);
        g.setColor(new Color(0, 145, 235));
        g.fillOval(x + 9, y + 6, 14, 14);
        g.setColor(new Color(170, 235, 255, 120));
        g.fillOval(x + 11, y + 8, 7, 4);

        // Large luminous eyes — bigger than Ember's to tell them apart
        g.setColor(new Color(0, 215, 255, 110));
        g.fillOval(x + 9,  y + 10, 6, 6);
        g.fillOval(x + 17, y + 10, 6, 6);
        g.setColor(new Color(190, 240, 255));
        g.fillOval(x + 10, y + 11, 4, 4);
        g.fillOval(x + 18, y + 11, 4, 4);
        g.setColor(new Color(0, 95, 200));
        g.fillOval(x + 11, y + 12, 2, 2);
        g.fillOval(x + 19, y + 12, 2, 2);
        g.setColor(Color.WHITE);
        g.fillRect(x + 12, y + 11, 1, 1);
        g.fillRect(x + 20, y + 11, 1, 1);

        // Animated water-droplet tendrils
        int dy = (bob < 2) ? 0 : 1;
        g.setColor(new Color(0, 175, 255, 210));
        g.fillOval(x + 9,  y + 1 + dy, 5, 7);
        g.setColor(new Color(140, 225, 255, 160));
        g.fillOval(x + 10, y + 1 + dy, 3, 3);
        g.setColor(new Color(0, 195, 255, 210));
        g.fillOval(x + 13, y - 1 + dy, 6, 9);
        g.setColor(new Color(175, 240, 255, 160));
        g.fillOval(x + 14, y - 1 + dy, 3, 3);
        g.setColor(new Color(0, 175, 255, 210));
        g.fillOval(x + 18, y + 1 + dy, 5, 7);
        g.setColor(new Color(140, 225, 255, 160));
        g.fillOval(x + 19, y + 1 + dy, 3, 3);

        // Floating bubble particles
        g.setColor(new Color(100, 215, 255, 170));
        g.drawOval(x + 3  + (animFrame % 6), y + 14, 4, 4);
        g.drawOval(x + 25 - (animFrame % 5), y + 18, 3, 3);
        g.setColor(new Color(0, 200, 255, 120));
        g.fillOval(x + 5, y + 22 - (animFrame % 4), 2, 2);

        // Selection border
        if (active) {
            g.setColor(NEON_CYAN);
            g.setStroke(STROKE_2);
            g.drawRoundRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, 8, 8);
            g.setStroke(STROKE_1);
        }
    }

    // ── Collectibles ──────────────────────────────────────────────────────────

    /** Elemental crystal — diamond shape, fire or water colour, gentle bob. */
    public static void drawCrystal(Graphics2D g, int x, int y, boolean isFire) {
        float bob = (float)(Math.sin(animFrame * 0.2) * 3);
        int yOffset = (int) bob;
        Color baseColor = isFire ? CRYSTAL_FIRE  : CRYSTAL_WATER;
        Color glowColor = isFire ? new Color(255, 100,  50, 100)
                                 : new Color( 50, 150, 255, 100);
        g.setColor(glowColor);
        g.fillOval(x + 4, y + 4 + yOffset, TILE_SIZE - 8, TILE_SIZE - 8);
        int[] xP = {x+16, x+26, x+16, x+ 6};
        int[] yP = {y+ 4+yOffset, y+16+yOffset, y+28+yOffset, y+16+yOffset};
        g.setColor(baseColor);
        g.fillPolygon(xP, yP, 4);
        g.setColor(Color.WHITE);
        g.fillPolygon(
            new int[]{x+16, x+20, x+16, x+12},
            new int[]{y+ 8+yOffset, y+14+yOffset, y+20+yOffset, y+14+yOffset}, 4
        );
    }

    /**
     * Bonus crystal — gold diamond with shifting rainbow glow ring.
     * Either character can collect it.
     */
    public static void drawBonusCrystal(Graphics2D g, int x, int y) {
        float bob   = (float)(Math.sin(animFrame * 0.2) * 3);
        int   yOff  = (int) bob;
        float pulse = (float)(Math.sin(animFrame * 0.15) * 0.3 + 0.7);

        // Rotating rainbow halo
        float hue = (animFrame * 0.018f) % 1f;
        Color rainbow = Color.getHSBColor(hue, 0.85f, 1.0f);
        g.setColor(new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), (int)(100 * pulse)));
        g.fillOval(x + 1, y + 1 + yOff, TILE_SIZE - 2, TILE_SIZE - 2);

        // Gold inner glow
        g.setColor(new Color(255, 215, 0, (int)(90 * pulse)));
        g.fillOval(x + 4, y + 4 + yOff, TILE_SIZE - 8, TILE_SIZE - 8);

        // Diamond body
        int[] xP = {x+16, x+26, x+16, x+ 6};
        int[] yP = {y+ 4+yOff, y+16+yOff, y+28+yOff, y+16+yOff};
        g.setColor(new Color(255, 200, 0));
        g.fillPolygon(xP, yP, 4);

        // Bright facet highlight
        g.setColor(new Color(255, 255, 150, 210));
        g.fillPolygon(
            new int[]{x+16, x+20, x+16, x+12},
            new int[]{y+ 8+yOff, y+14+yOff, y+20+yOff, y+14+yOff}, 4
        );

        // White sparkle centre + cross glints
        g.setColor(new Color(255, 255, 255, (int)(230 * pulse)));
        g.fillOval(x + 12, y + 11 + yOff, 8, 8);
        g.setColor(new Color(255, 255, 180, (int)(160 * pulse)));
        g.fillRect(x + 14, y +  1 + yOff, 4, 3);
        g.fillRect(x + 14, y + 28 + yOff, 4, 3);
        g.fillRect(x +  1, y + 13 + yOff, 3, 4);
        g.fillRect(x + 28, y + 13 + yOff, 3, 4);
    }

    /** Generic gem — picks the right colour/sprite based on type. */
    public static void drawGem(Graphics2D g, int x, int y, String type) {
        float pulse = (float)(Math.sin(animFrame * 0.3) * 0.3 + 0.7);

        if ("BONUS".equals(type)) {
            drawBonusGem(g, x, y, pulse);
            return;
        }

        // Try the animated diamond sprite with a colour tint
        BufferedImage diamondImg = TilesetLoader.getDiamondFrame(animFrame);
        if (diamondImg != null) {
            Color tint;
            switch (type) {
                case "BLUE":   tint = GEM_BLUE;   break;
                case "GREEN":  tint = GEM_GREEN;  break;
                case "YELLOW": tint = GEM_YELLOW; break;
                case "WHITE":  tint = GEM_WHITE;  break;
                default:       tint = GEM_RED;    break;
            }
            g.setColor(new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), (int)(80 * pulse)));
            g.fillOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
            g.drawImage(diamondImg, x, y, TILE_SIZE, TILE_SIZE, null);
            Composite old = g.getComposite();
            g.setComposite(AC_ENEMY_TINT);
            g.setColor(tint);
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            g.setComposite(old);
            return;
        }

        // Fallback procedural hexagonal gem
        Color gemColor = GEM_RED;
        switch (type) {
            case "BLUE":   gemColor = GEM_BLUE;   break;
            case "GREEN":  gemColor = GEM_GREEN;  break;
            case "YELLOW": gemColor = GEM_YELLOW; break;
            case "WHITE":  gemColor = GEM_WHITE;  break;
        }
        g.setColor(new Color(gemColor.getRed(), gemColor.getGreen(), gemColor.getBlue(), (int)(100 * pulse)));
        g.fillOval(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        g.setColor(gemColor);
        int[] xP = {x+16, x+24, x+24, x+16, x+ 8, x+ 8};
        int[] yP = {y+ 6, y+12, y+22, y+28, y+22, y+12};
        g.fillPolygon(xP, yP, 6);
        g.setColor(new Color(255, 255, 255, 150));
        g.fillOval(x + 12, y + 10, 6, 8);
    }

    /**
     * Bonus gem — shimmering gold star-diamond with a cycling rainbow ring
     * and a bright white core. Collectible by both characters.
     */
    private static void drawBonusGem(Graphics2D g, int x, int y, float pulse) {
        float hue = (animFrame * 0.018f) % 1f;
        Color rainbow = Color.getHSBColor(hue, 0.9f, 1.0f);
        g.setColor(new Color(rainbow.getRed(), rainbow.getGreen(), rainbow.getBlue(), (int)(110 * pulse)));
        g.fillOval(x - 1, y - 1, TILE_SIZE + 2, TILE_SIZE + 2);

        g.setColor(new Color(255, 215, 0, (int)(90 * pulse)));
        g.fillOval(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6);

        BufferedImage diamondImg = TilesetLoader.getDiamondFrame(animFrame);
        if (diamondImg != null) {
            g.drawImage(diamondImg, x, y, TILE_SIZE, TILE_SIZE, null);
            Composite old = g.getComposite();
            g.setComposite(AC_GEM_TINT);
            g.setColor(new Color(255, 210, 0));
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            g.setComposite(old);
        } else {
            g.setColor(new Color(255, 200, 0));
            int[] xP = {x+16, x+24, x+24, x+16, x+8, x+8};
            int[] yP = {y+ 6, y+12, y+22, y+28, y+22, y+12};
            g.fillPolygon(xP, yP, 6);
            g.setColor(new Color(255, 255, 100, 200));
            g.setStroke(STROKE_1_5);
            g.drawPolygon(xP, yP, 6);
        }

        g.setColor(new Color(255, 255, 255, (int)(200 * pulse)));
        g.fillOval(x + 11, y + 9, 10, 10);
        g.setColor(new Color(255, 255, 180, (int)(160 * pulse)));
        g.fillRect(x + 14, y + 2,  4, 2);
        g.fillRect(x + 14, y + 28, 4, 2);
        g.fillRect(x +  2, y + 14, 2, 4);
        g.fillRect(x + 28, y + 14, 2, 4);
    }

    // ── Gates ─────────────────────────────────────────────────────────────────

    /** Fire gate — angular stone frame, orange/red, flame-tip decorations. */
    public static void drawFireGate(Graphics2D g, int x, int y, boolean unlocked) {
        Color frame = unlocked ? NEON_ORANGE : GATE_LOCKED;

        // Angular corner brackets
        g.setColor(frame);
        g.fillRect(x,               y,               8, 4);
        g.fillRect(x,               y,               4, 8);
        g.fillRect(x + TILE_SIZE-8, y,               8, 4);
        g.fillRect(x + TILE_SIZE-4, y,               4, 8);
        g.fillRect(x,               y + TILE_SIZE-4, 8, 4);
        g.fillRect(x,               y + TILE_SIZE-8, 4, 8);
        g.fillRect(x + TILE_SIZE-8, y + TILE_SIZE-4, 8, 4);
        g.fillRect(x + TILE_SIZE-4, y + TILE_SIZE-8, 4, 8);

        // Top bar with three small flame-tip triangles
        g.setColor(unlocked ? new Color(255, 140, 0) : new Color(80, 50, 20));
        g.fillRect(x + 8, y, TILE_SIZE - 16, 3);
        for (int i = 0; i < 3; i++) {
            int tx = x + 10 + i * 7;
            int[] xp = {tx, tx + 3, tx + 6};
            int[] yp = {y + 3, y - 3, y + 3};
            g.fillPolygon(xp, yp, 3);
        }

        if (unlocked) {
            // Inner fire portal
            g.setColor(new Color(80, 20, 0));
            g.fillRect(x + 5, y + 5, TILE_SIZE - 10, TILE_SIZE - 10);
            BufferedImage portal = TilesetLoader.getSwallowFrame(animFrame);
            if (portal != null) {
                int ix = x + 5, iy = y + 5, is = TILE_SIZE - 10;
                g.drawImage(portal, ix, iy, is, is, null);
                Composite old = g.getComposite();
                g.setComposite(AC_LAVA_TINT);
                g.setColor(NEON_ORANGE);
                g.fillRect(ix, iy, is, is);
                g.setComposite(old);
            } else {
                // Fallback: spinning fire orbs
                g.setColor(NEON_ORANGE);
                float swirl = animFrame * 0.12f;
                for (int i = 0; i < 4; i++) {
                    double angle = swirl + i * Math.PI / 2;
                    int px = x + 16 + (int)(7 * Math.cos(angle));
                    int py = y + 16 + (int)(7 * Math.sin(angle));
                    g.fillOval(px - 2, py - 2, 5, 5);
                }
            }
        } else {
            // Locked: dark interior + padlock symbol
            g.setColor(new Color(40, 15, 0));
            g.fillRect(x + 5, y + 5, TILE_SIZE - 10, TILE_SIZE - 10);
            g.setColor(new Color(110, 60, 0));
            g.fillRect(x + 12, y + 17, 8, 8);
            g.drawArc(x + 11, y + 11, 10, 10, 0, 180);
        }
    }

    /**
     * Water gate — elegant aquatic portal with animated water streams,
     * rising bubbles, and a glowing vortex when unlocked.
     */
    public static void drawWaterGate(Graphics2D g, int x, int y, boolean unlocked) {
        float pulse = (float)(Math.sin(animFrame * 0.18) * 0.3 + 0.7);
        float wave  = (float)(Math.sin(animFrame * 0.15) * 0.4 + 0.6);
        int   cx    = x + TILE_SIZE / 2;
        int   cy    = y + TILE_SIZE / 2;
        Stroke prevStroke = g.getStroke();

        // Outer water aura
        Color aura = unlocked ? new Color(0, 180, 255, (int)(80 * pulse))
                              : new Color(20, 50, 100, (int)(40 * pulse));
        g.setColor(aura);
        g.fillOval(x - 4, y - 4, TILE_SIZE + 8, TILE_SIZE + 8);

        // Deep ocean base — layered to fake a gradient
        g.setColor(unlocked ? new Color(0, 40, 100) : new Color(15, 25, 50));
        g.fillRoundRect(x, y, TILE_SIZE, TILE_SIZE, 8, 8);
        g.setColor(unlocked ? new Color(0, 80, 160) : new Color(25, 40, 70));
        g.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 6, 6);
        g.setColor(unlocked ? new Color(0, 120, 200) : new Color(30, 50, 80));
        g.fillRoundRect(x + 4, y + 4, TILE_SIZE - 8, TILE_SIZE - 8, 4, 4);

        // Animated wave arcs along the top
        g.setStroke(STROKE_2);
        int waveOffset = (animFrame / 4) % 6;
        Color waveColor = unlocked ? new Color(100, 220, 255, 200) : new Color(60, 100, 150, 150);
        g.setColor(waveColor);
        for (int i = 0; i < 5; i++) {
            int wx = x + 2 + i * 6 + waveOffset - 3;
            int wy = y + 3 + (int)(Math.sin((animFrame + i * 10) * 0.2) * 2);
            g.drawArc(wx, wy, 7, 4, 0, 180);
        }

        // Cascading waterfall streams on the sides
        int streamOffset = (animFrame / 2) % 8;
        Color streamColor = unlocked ? new Color(80, 200, 255, (int)(180 * wave))
                                     : new Color(50, 100, 150, (int)(100 * wave));
        g.setColor(streamColor);
        for (int i = 0; i < 4; i++) {
            int sy = y + 8 + i * 6 + streamOffset;
            if (sy < y + TILE_SIZE - 4) g.fillRoundRect(x + 2, sy, 3, 5, 2, 2);
        }
        for (int i = 0; i < 4; i++) {
            int sy = y + 6 + i * 6 + (streamOffset + 3) % 8;
            if (sy < y + TILE_SIZE - 4) g.fillRoundRect(x + TILE_SIZE - 5, sy, 3, 5, 2, 2);
        }

        // Droplet decorations on the frame
        Color dropletColor = unlocked ? new Color(150, 240, 255, (int)(200 * pulse))
                                      : new Color(80, 130, 180, 150);
        g.setColor(dropletColor);
        g.fillOval(x + 8,  y, 4, 6);
        g.fillOval(x + 20, y, 4, 6);
        g.fillOval(x + 8,  y + TILE_SIZE - 6, 4, 6);
        g.fillOval(x + 20, y + TILE_SIZE - 6, 4, 6);

        if (unlocked) {
            // Swirling water vortex — concentric rings + rotating orbs
            float swirl = animFrame * 0.12f;
            for (int ring = 3; ring >= 1; ring--) {
                int ringSize = 4 + ring * 4;
                int alpha = 100 + ring * 40;
                g.setColor(new Color(0, 140 + ring * 30, 255, alpha));
                g.setStroke(STROKE_1_5);
                int offset = (int)(Math.sin(swirl + ring) * 2);
                g.drawOval(cx - ringSize/2 + offset, cy - ringSize/2 - offset, ringSize, ringSize);
            }
            for (int i = 0; i < 6; i++) {
                double angle = swirl + i * Math.PI / 3;
                int radius = 6 + (i % 2) * 2;
                int px = cx + (int)(radius * Math.cos(angle));
                int py = cy + (int)(radius * Math.sin(angle));
                g.setColor(new Color(100, 200, 255, 220));
                g.fillOval(px - 2, py - 2, 4, 4);
                g.setColor(new Color(150, 230, 255, 100));
                int tx = cx + (int)((radius - 2) * Math.cos(angle - 0.3));
                int ty = cy + (int)((radius - 2) * Math.sin(angle - 0.3));
                g.fillOval(tx - 1, ty - 1, 3, 3);
            }

            // Bright water core
            g.setColor(new Color(200, 250, 255, (int)(220 * pulse)));
            g.fillOval(cx - 4, cy - 4, 8, 8);
            g.setColor(new Color(255, 255, 255, (int)(180 * pulse)));
            g.fillOval(cx - 2, cy - 2, 4, 4);

            // Rising bubble columns
            g.setColor(new Color(180, 245, 255, (int)(180 * pulse)));
            int b1 = (animFrame / 2) % 16, b2 = (animFrame / 3 + 4) % 14;
            int b3 = (animFrame / 4 + 8) % 12, b4 = (animFrame / 3 + 2) % 10;
            g.fillOval(x + 8,  y + TILE_SIZE - 6 - b1, 3, 3);
            g.fillOval(x + 14, y + TILE_SIZE - 4 - b2, 2, 2);
            g.fillOval(x + 20, y + TILE_SIZE - 5 - b3, 3, 3);
            g.fillOval(x + 26, y + TILE_SIZE - 6 - b4, 2, 2);
            g.setColor(new Color(255, 255, 255, (int)(120 * pulse)));
            g.fillOval(x + 8,  y + TILE_SIZE - 6 - b1, 1, 1);
            g.fillOval(x + 20, y + TILE_SIZE - 5 - b3, 1, 1);

            // Corner splash effects
            g.setColor(new Color(150, 230, 255, (int)(140 * wave)));
            int splash = (animFrame / 5) % 3;
            g.fillOval(x - 1 + splash, y + 10, 3, 4);
            g.fillOval(x + TILE_SIZE - 2 - splash, y + 18, 3, 4);

            // Glowing border
            g.setColor(new Color(0, 220, 255, (int)(220 * pulse)));
            g.setStroke(STROKE_2_5);
            g.drawRoundRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, 8, 8);
            g.setColor(new Color(100, 240, 255, (int)(100 * pulse)));
            g.setStroke(STROKE_1);
            g.drawRoundRect(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6, 6, 6);

        } else {
            // Locked: frozen ripples + ice-cross lock symbol
            g.setColor(new Color(60, 100, 150, 120));
            g.setStroke(STROKE_1);
            g.drawOval(cx - 8, cy - 8, 16, 16);
            g.drawOval(cx - 5, cy - 5, 10, 10);

            g.setColor(new Color(100, 160, 220));
            g.setStroke(STROKE_2);
            g.drawLine(cx, cy - 7, cx, cy + 7);
            g.drawLine(cx - 7, cy, cx + 7, cy);
            g.setStroke(STROKE_1_5);
            g.drawLine(cx - 5, cy - 5, cx + 5, cy + 5);
            g.drawLine(cx - 5, cy + 5, cx + 5, cy - 5);
            g.setColor(new Color(150, 200, 240));
            g.fillOval(cx - 2, cy - 9, 4, 4);
            g.fillOval(cx - 2, cy + 6, 4, 4);
            g.fillOval(cx - 9, cy - 2, 4, 4);
            g.fillOval(cx + 6, cy - 2, 4, 4);

            g.setColor(new Color(100, 150, 200, 100));
            g.fillOval(x + 8,  y + 22, 3, 3);
            g.fillOval(x + 20, y + 20, 2, 2);

            g.setColor(new Color(70, 110, 150, 180));
            g.setStroke(STROKE_2);
            g.drawRoundRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, 8, 8);
        }
        g.setStroke(prevStroke);
    }

    // ── Enemy ─────────────────────────────────────────────────────────────────

    /** Shadow Sentinel — animated blob sprite with a purple tint overlay. */
    public static void drawEnemy(Graphics2D g, int x, int y) {
        float pulse = (float)(Math.sin(animFrame * 0.25) * 0.3 + 0.7);

        g.setColor(new Color(50, 0, 80, (int)(100 * pulse)));
        g.fillOval(x - 4, y - 4, TILE_SIZE + 8, TILE_SIZE + 8);

        BufferedImage blob = TilesetLoader.getBlobFrame(animFrame);
        if (blob != null) {
            g.drawImage(blob, x, y, TILE_SIZE, TILE_SIZE, null);
            Composite old = g.getComposite();
            g.setComposite(AC_ENEMY_TINT);
            g.setColor(new Color(80, 0, 120));
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
            g.setComposite(old);
        } else {
            // Fallback procedural blob
            g.setColor(ENEMY_COLOR);
            g.fillOval(x + 4, y + 6, TILE_SIZE - 8, TILE_SIZE - 8);
            g.setColor(ENEMY_GLOW);
            g.fillOval(x + 10, y + 12, 5, 5);
            g.fillOval(x + 18, y + 12, 5, 5);
            g.setColor(new Color(255, 50, 255, 150));
            g.fillOval(x + 11, y + 13, 3, 3);
            g.fillOval(x + 19, y + 13, 3, 3);
        }
    }

    // ── Projectiles ───────────────────────────────────────────────────────────

    /** Fireball — glowing orange ball with animated flame flickers. */
    public static void drawFireball(Graphics2D g, int x, int y) {
        float pulse = (float)(Math.sin(animFrame * 0.4) * 0.3 + 0.7);
        int cx = x + TILE_SIZE / 2, cy = y + TILE_SIZE / 2;
        g.setColor(new Color(255, 80, 0, (int)(80 * pulse)));
        g.fillOval(cx - 14, cy - 14, 28, 28);
        g.setColor(new Color(255, 140, 0));
        g.fillOval(cx - 9, cy - 9, 18, 18);
        g.setColor(new Color(255, 230, 100, (int)(200 * pulse)));
        g.fillOval(cx - 5, cy - 5, 10, 10);
        g.setColor(new Color(255, 60, 0, 180));
        int flick = (animFrame / 3) % 4;
        g.fillOval(cx - 3 + flick, cy - 14, 6, 7);
        g.fillOval(cx - 8,         cy - 11, 5, 6);
    }

    /** Waterball — glowing cyan sphere with an expanding ripple ring. */
    public static void drawWaterball(Graphics2D g, int x, int y) {
        float wave = (float)(Math.sin(animFrame * 0.35) * 0.3 + 0.7);
        int cx = x + TILE_SIZE / 2, cy = y + TILE_SIZE / 2;
        g.setColor(new Color(0, 150, 255, (int)(80 * wave)));
        g.fillOval(cx - 14, cy - 14, 28, 28);
        g.setColor(new Color(0, 120, 220));
        g.fillOval(cx - 9, cy - 9, 18, 18);
        g.setColor(new Color(150, 230, 255, (int)(200 * wave)));
        g.fillOval(cx - 5, cy - 7, 8, 6);
        g.setColor(new Color(0, 220, 255, (int)(150 * wave)));
        int ripple = (animFrame / 4) % 5;
        g.drawOval(cx - 11 - ripple, cy - 11 - ripple, 22 + ripple * 2, 22 + ripple * 2);
    }

    // ── HUD helpers ───────────────────────────────────────────────────────────

    /** Draws one heart — filled (pink) or empty (dark red). */
    public static void drawHeart(Graphics2D g, int x, int y, boolean filled) {
        g.setColor(filled ? NEON_PINK : new Color(100, 50, 70));
        int[] xP = {x+12, x+20, x+24, x+20, x+12, x+ 4, x,   x+ 4};
        int[] yP = {y+20, y+16, y+ 8, y+ 4, y+ 8, y+ 4, y+8, y+16};
        g.fillPolygon(xP, yP, 8);
        if (filled) {
            g.setColor(new Color(255, 50, 150, 100));
            g.fillOval(x - 2, y, 28, 24);
        }
    }
}
