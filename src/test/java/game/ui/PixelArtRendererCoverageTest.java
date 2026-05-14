package game.ui;

import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises {@link PixelArtRenderer} drawing entry points to cover tile fallbacks, gem types,
 * gates (locked/unlocked), and animation-driven branches.
 */
class PixelArtRendererCoverageTest {

    private static Graphics2D newG() {
        BufferedImage img = new BufferedImage(480, 480, BufferedImage.TYPE_INT_ARGB);
        return img.createGraphics();
    }

    /** Verifies static draw helpers run on a {@link Graphics2D} after animation warmup without throwing. */
    @Test
    void allStaticDrawMethods_runOnGraphics() {
        TilesetLoader.initialize();
        for (int i = 0; i < 130; i++) {
            PixelArtRenderer.updateAnimation();
        }

        Graphics2D g = newG();
        try {
            int t = PixelArtRenderer.TILE_SIZE;
            PixelArtRenderer.drawFloor(g, 0, 0);
            PixelArtRenderer.drawWall(g, 0, 0);
            PixelArtRenderer.drawLava(g, t, 0);
            PixelArtRenderer.drawWater(g, 0, t);
            PixelArtRenderer.drawTrap(g, t, t);

            PixelArtRenderer.drawEmber(g, 0, 0, true);
            PixelArtRenderer.drawEmber(g, 40, 0, false);
            PixelArtRenderer.drawAqua(g, 0, 40, true);
            PixelArtRenderer.drawAqua(g, 40, 40, false);

            PixelArtRenderer.drawCrystal(g, 0, 0, true);
            PixelArtRenderer.drawCrystal(g, t, 0, false);
            PixelArtRenderer.drawBonusCrystal(g, 0, t);

            PixelArtRenderer.drawGem(g, t, t, "BONUS");
            PixelArtRenderer.drawGem(g, 0, 2 * t, "RED");
            PixelArtRenderer.drawGem(g, t, 2 * t, "BLUE");
            PixelArtRenderer.drawGem(g, 2 * t, 2 * t, "GREEN");
            PixelArtRenderer.drawGem(g, 3 * t, 2 * t, "YELLOW");
            PixelArtRenderer.drawGem(g, 4 * t, 2 * t, "WHITE");
            PixelArtRenderer.drawGem(g, 5 * t, 2 * t, "UNKNOWN_DEFAULT");

            PixelArtRenderer.drawFireGate(g, 0, 3 * t, false);
            PixelArtRenderer.drawFireGate(g, t, 3 * t, true);
            PixelArtRenderer.drawWaterGate(g, 2 * t, 3 * t, false);
            PixelArtRenderer.drawWaterGate(g, 3 * t, 3 * t, true);

            PixelArtRenderer.drawEnemy(g, 0, 4 * t);
            PixelArtRenderer.drawFireball(g, t, 4 * t);
            PixelArtRenderer.drawWaterball(g, 2 * t, 4 * t);
            PixelArtRenderer.drawHeart(g, 0, 5 * t, true);
            PixelArtRenderer.drawHeart(g, t, 5 * t, false);
        } finally {
            g.dispose();
        }
    }

    /** Verifies {@link PixelArtRenderer#getTileSize()} matches the tile size constant. */
    @Test
    void tileSizeAccessor() {
        assertEquals(32, PixelArtRenderer.getTileSize());
    }

    /** Verifies high animation frame counts still allow terrain and gate draws without throwing. */
    @Test
    void highAnimationFrames_exerciseMoreBranches() {
        TilesetLoader.initialize();
        for (int i = 0; i < 500; i++) {
            PixelArtRenderer.updateAnimation();
        }
        Graphics2D g = newG();
        try {
            int t = PixelArtRenderer.TILE_SIZE;
            PixelArtRenderer.drawLava(g, 0, 0);
            PixelArtRenderer.drawWater(g, t, 0);
            PixelArtRenderer.drawTrap(g, 0, t);
            PixelArtRenderer.drawFireGate(g, t, t, true);
            PixelArtRenderer.drawWaterGate(g, 2 * t, t, true);
            PixelArtRenderer.drawBonusCrystal(g, 0, 3 * t);
        } finally {
            g.dispose();
        }
    }

    /**
     * Long animation cycles hit additional modulo / frame-index branches inside tile-based draws.
     */
    @Test
    void extremeAnimationCycle_redrawsTerrainAndSprites() {
        TilesetLoader.initialize();
        for (int i = 0; i < 2500; i++) {
            PixelArtRenderer.updateAnimation();
        }
        Graphics2D g = newG();
        try {
            int t = PixelArtRenderer.TILE_SIZE;
            PixelArtRenderer.drawFloor(g, 0, 0);
            PixelArtRenderer.drawWall(g, t, 0);
            PixelArtRenderer.drawLava(g, 0, t);
            PixelArtRenderer.drawWater(g, t, t);
            PixelArtRenderer.drawTrap(g, 2 * t, 0);
            PixelArtRenderer.drawCrystal(g, 0, 2 * t, true);
            PixelArtRenderer.drawCrystal(g, t, 2 * t, false);
            PixelArtRenderer.drawEnemy(g, 2 * t, 2 * t);
            PixelArtRenderer.drawBonusCrystal(g, 3 * t, 0);
            PixelArtRenderer.drawFireball(g, 0, 3 * t);
            PixelArtRenderer.drawWaterball(g, t, 3 * t);
        } finally {
            g.dispose();
        }
    }

    /** Verifies public colour constants on {@link PixelArtRenderer} are non-null. */
    @Test
    void colorConstants_areNonNull() {
        assertNotNull(PixelArtRenderer.DARK_BG);
        assertNotNull(PixelArtRenderer.NEON_CYAN);
        assertNotNull(PixelArtRenderer.GATE_LOCKED);
        assertNotNull(PixelArtRenderer.ENEMY_COLOR);
    }
}
