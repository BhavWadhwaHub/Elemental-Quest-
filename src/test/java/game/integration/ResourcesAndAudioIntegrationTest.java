package game.integration;

import game.core.SoundManager;
import game.ui.PixelArtRenderer;
import game.ui.TilesetLoader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p><b>Integration focus — classpath / filesystem ↔ rendering ↔ audio</b></p>
 * <ul>
 *   <li><b>Tileset + renderer:</b> {@link TilesetLoader} reads PNGs from the JAR classpath
 *       ({@code /tileset/...}); {@link PixelArtRenderer#updateAnimation()} triggers lazy load so
 *       drawing code and the resource layer work together.</li>
 *   <li><b>Sound:</b> {@link SoundManager} resolves {@code .mp3} from {@code ./audio/} first, then
 *       classpath {@code /audio/}. Missing files must not crash the game — only log and skip.</li>
 * </ul>
 */
class ResourcesAndAudioIntegrationTest {

    /** Verifies {@link TilesetLoader#initialize()} with {@link PixelArtRenderer#updateAnimation()} does not throw. */
    @Test
    void tilesetLoader_and_pixelRenderer_animationTick_loadCooperate() {
        assertDoesNotThrow(() -> {
            TilesetLoader.initialize();
            PixelArtRenderer.updateAnimation();
        });
    }

    /** Verifies {@link TilesetLoader#get(String)} for blank tile after init does not throw. */
    @Test
    void tilesetLoader_getBlank_doesNotThrowAfterInit() {
        TilesetLoader.initialize();
        assertDoesNotThrow(() -> TilesetLoader.get("blank"));
    }

    /** Verifies {@link SoundManager} BGM and SFX for missing tracks do not throw. */
    @Test
    void soundManager_playMissingTrack_doesNotThrow() {
        SoundManager sm = SoundManager.getInstance();
        assertDoesNotThrow(() -> sm.playBGM("___integration_test_missing_track___"));
        assertDoesNotThrow(() -> sm.playSFX("___integration_test_missing_sfx___"));
    }
}
