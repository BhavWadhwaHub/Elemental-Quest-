package game.ui;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for {@link PixelArtRenderer} constants, animation tick, and basic drawing on a {@link BufferedImage}.
 */
class PixelArtRendererTest {

    /** Verifies tile size field and accessor match. */
    @Test
    void tileSize_constant() {
        assertEquals(32, PixelArtRenderer.TILE_SIZE);
        assertEquals(32, PixelArtRenderer.getTileSize());
    }

    /** Verifies {@link PixelArtRenderer#updateAnimation()} does not throw. */
    @Test
    void updateAnimation_doesNotThrow() {
        assertDoesNotThrow(PixelArtRenderer::updateAnimation);
    }

    /** Verifies floor and wall draw methods run on image graphics without throwing. */
    @Test
    void drawFloor_drawWall_runOnImageGraphics() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            PixelArtRenderer.drawFloor(g, 0, 0);
            PixelArtRenderer.drawWall(g, 32, 0);
        } finally {
            g.dispose();
        }
        assertNotNull(img);
    }
}
