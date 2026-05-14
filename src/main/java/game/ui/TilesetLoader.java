package game.ui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads and caches tileset PNG images from src/main/resources/tileset/.
 * Falls back gracefully if an image is missing.
 */
public class TilesetLoader {

    private static final Map<String, BufferedImage> CACHE = new HashMap<>();
    private static boolean initialized = false;

    // ── Tile name arrays for animated/varied elements ──────────────────────

    /** 9 wall variants – picked deterministically by grid position. */
    public static final String[] WALL_TILES = {
        "wall1", "wall2", "wall3", "wall4", "wall5",
        "wall6", "wall7", "wall8", "wall9"
    };

    /** 4-frame blob animation → ShadowSentinel enemy. */
    public static final String[] BLOB_FRAMES = {
        "blob_1", "blob_2", "blob_3", "blob_4"
    };

    /** 2-frame diamond animation → GoldenGem collectible. */
    public static final String[] DIAMOND_FRAMES = {
        "diamond_1", "diamond_2"
    };

    /** 2-frame spike animation → lava hazard overlay. */
    public static final String[] SPIKE_FRAMES = {
        "spike_1", "spike_2"
    };

    /** 4-frame black-hole animation → water hazard overlay. */
    public static final String[] BLACK_HOLE_FRAMES = {
        "black_hole_1", "black_hole_2", "black_hole_3", "black_hole_4"
    };

    /** 4-frame black-hole-swallow → unlocked gate animation. */
    public static final String[] SWALLOW_FRAMES = {
        "black_hole_swallow_1", "black_hole_swallow_2",
        "black_hole_swallow_3", "black_hole_swallow_4"
    };

    // ──────────────────────────────────────────────────────────────────────

    private static final String[] ALL_IMAGES = {
        // Terrain
        "blank", "block", "blocke", "blockr",
        "wall1","wall2","wall3","wall4","wall5","wall6","wall7","wall8","wall9",
        "sticky_horizontal","sticky_vertical",
        "turner_clockwise","turner_anticlockwise",
        "oneway_up_1","oneway_up_2","oneway_down_1","oneway_down_2",
        "oneway_left_1","oneway_left_2","oneway_right_1","oneway_right_2",
        // Collectibles
        "diamond_1","diamond_2",
        // Hazards
        "spike_1","spike_2",
        "black_hole_1","black_hole_2","black_hole_3","black_hole_4",
        "black_hole_swallow_1","black_hole_swallow_2","black_hole_swallow_3","black_hole_swallow_4",
        // Enemies
        "blob_1","blob_2","blob_3","blob_4",
        "gnasher_1","gnasher_2",
        "snake_1","snake_2",
        "sentry_up","sentry_down","sentry_left","sentry_right",
        "rocky_up","rocky_down","rocky_left","rocky_right",
        "rocky_shooter_up","rocky_shooter_down","rocky_shooter_left","rocky_shooter_right",
        "slider_up","slider_down","slider_left","slider_right",
        "slider_shooter_up","slider_shooter_down","slider_shooter_left","slider_shooter_right",
        "twister_1","twister_2",
        // Player sprites
        "kye","kye_fading","kye_faint",
        // Timer blocks
        "block_timer_0","block_timer_1","block_timer_2","block_timer_3","block_timer_4",
        "block_timer_5","block_timer_6","block_timer_7","block_timer_8","block_timer_9"
    };

    // ──────────────────────────────────────────────────────────────────────

    /**
     * Loads all known tile images into the cache on the first call.
     * Subsequent calls are no-ops. Thread-safe — the method is synchronized
     * so it's safe to call from the animation timer thread.
     */
    public static synchronized void initialize() {
        if (initialized) return;
        for (String name : ALL_IMAGES) {
            loadImage(name);
        }
        initialized = true;
    }

    /**
     * Clears the image cache and reload flag so tests can re-run {@link #initialize()} deterministically.
     * Not used by the game at runtime.
     */
    public static synchronized void resetForTestsOnly() {
        CACHE.clear();
        initialized = false;
    }

    /** Loads one tile by name (same as internal preload). For tests only. */
    static synchronized void loadTileForTests(String name) {
        loadImage(name);
    }

    /** Inserts or replaces a cache entry. For tests only (e.g. {@code null} image edge cases). */
    static synchronized void putCacheEntryForTests(String name, BufferedImage image) {
        CACHE.put(name, image);
    }

    /**
     * Attempts to read a single PNG from the classpath and adds it to the cache.
     * Silently skips the image if the resource is missing, so the renderer
     * can fall back to procedural drawing.
     */
    private static void loadImage(String name) {
        String path = "/tileset/" + name + ".png";
        try (InputStream is = TilesetLoader.class.getResourceAsStream(path)) {
            if (is != null) {
                CACHE.put(name, ImageIO.read(is));
            }
        } catch (IOException ignored) {
            // Will fall back to programmatic drawing
        }
    }

    /** Returns the cached image, or {@code null} if not found. */
    public static BufferedImage get(String name) {
        if (!initialized) initialize();
        return CACHE.get(name);
    }

    /** {@code true} if the image was loaded successfully. */
    public static boolean has(String name) {
        if (!initialized) initialize();
        return CACHE.containsKey(name) && CACHE.get(name) != null;
    }

    // ── Convenience helpers ────────────────────────────────────────────────

    /**
     * Returns one of the 9 wall-tile images, chosen deterministically
     * by pixel coordinates so the same grid cell always gets the same tile.
     */
    public static BufferedImage getWallTile(int pixelX, int pixelY, int tileSize) {
        int gx = pixelX / tileSize;
        int gy = pixelY / tileSize;
        int idx = Math.abs(gx * 7 + gy * 13) % WALL_TILES.length;
        return get(WALL_TILES[idx]);
    }

    /** Animated blob frame for the current animation tick. */
    public static BufferedImage getBlobFrame(int animFrame) {
        return get(BLOB_FRAMES[(animFrame / 5) % BLOB_FRAMES.length]);
    }

    /** Animated diamond frame for the current animation tick. */
    public static BufferedImage getDiamondFrame(int animFrame) {
        return get(DIAMOND_FRAMES[(animFrame / 10) % DIAMOND_FRAMES.length]);
    }

    /** Animated spike frame for the current animation tick. */
    public static BufferedImage getSpikeFrame(int animFrame) {
        return get(SPIKE_FRAMES[(animFrame / 8) % SPIKE_FRAMES.length]);
    }

    /** Animated black-hole frame for the current animation tick. */
    public static BufferedImage getBlackHoleFrame(int animFrame) {
        return get(BLACK_HOLE_FRAMES[(animFrame / 5) % BLACK_HOLE_FRAMES.length]);
    }

    /** Animated swallow frame for the current animation tick. */
    public static BufferedImage getSwallowFrame(int animFrame) {
        return get(SWALLOW_FRAMES[(animFrame / 5) % SWALLOW_FRAMES.length]);
    }
}
