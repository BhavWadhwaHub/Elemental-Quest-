package game.ui;

import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke tests for {@link BorderAnimation}: frame update and drawing without throwing.
 */
class BorderAnimationTest {

    /** {@link BorderAnimation#update()} runs without error. */
    @Test
    void update_advancesFrame() {
        BorderAnimation anim = new BorderAnimation();
        assertDoesNotThrow(anim::update);
    }

    /** {@link BorderAnimation#draw} completes when given a {@link Graphics2D} from a bitmap. */
    @Test
    void draw_runsWithoutException() {
        BorderAnimation anim = new BorderAnimation();
        anim.update();
        BufferedImage img = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            assertDoesNotThrow(() -> anim.draw(g, 400, 300));
        } finally {
            g.dispose();
        }
    }
}
