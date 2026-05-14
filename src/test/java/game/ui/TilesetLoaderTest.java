package game.ui;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link TilesetLoader}: initialization, wall tiles, cache helpers, and resilient image loading.
 */
class TilesetLoaderTest {

    /** Verifies {@link TilesetLoader#initialize()} completes without throwing. */
    @Test
    void initialize_loadsWithoutCrashing() {
        assertDoesNotThrow(TilesetLoader::initialize);
    }

    /** Verifies default construction does not throw. */
    @Test
    void defaultConstructor_runs() {
        assertDoesNotThrow(TilesetLoader::new);
    }

    /** Verifies {@link TilesetLoader#WALL_TILES} has nine entries. */
    @Test
    void wallTilesArray_hasNineEntries() {
        assertEquals(9, TilesetLoader.WALL_TILES.length);
    }

    /** Verifies {@link TilesetLoader#getWallTile(int, int, int)} returns null or a non-empty image. */
    @Test
    void getWallTile_returnsNullOrImage() {
        TilesetLoader.initialize();
        BufferedImage img = TilesetLoader.getWallTile(0, 0, PixelArtRenderer.TILE_SIZE);
        assertTrue(img == null || img.getWidth() > 0);
    }

    /** Verifies animation frame accessors do not throw after init. */
    @Test
    void animationFrameHelpers_doNotThrow() {
        TilesetLoader.initialize();
        assertDoesNotThrow(() -> {
            TilesetLoader.getBlobFrame(0);
            TilesetLoader.getDiamondFrame(0);
            TilesetLoader.getSpikeFrame(0);
            TilesetLoader.getBlackHoleFrame(0);
            TilesetLoader.getSwallowFrame(0);
        });
    }

    /** Verifies {@link TilesetLoader#has(String)} is false when cache holds a null image. */
    @Test
    void has_falseWhenCachedImageIsNull() {
        TilesetLoader.resetForTestsOnly();
        TilesetLoader.initialize();
        TilesetLoader.putCacheEntryForTests("__null_entry__", null);
        assertFalse(TilesetLoader.has("__null_entry__"));
    }

    /** Verifies test tile load for a missing resource does not throw. */
    @Test
    void loadTileForTests_missingResource_doesNotThrow() {
        TilesetLoader.resetForTestsOnly();
        assertDoesNotThrow(() -> TilesetLoader.loadTileForTests("__no_such_tile_resource__"));
        TilesetLoader.initialize();
    }

    /** Verifies corrupt PNG test resource does not throw the loader. */
    @Test
    void loadTileForTests_corruptPng_doesNotThrow() {
        TilesetLoader.resetForTestsOnly();
        assertDoesNotThrow(() -> TilesetLoader.loadTileForTests("bad_tile"));
        TilesetLoader.initialize();
    }

    /** Verifies unknown tile name is absent before init. */
    @Test
    void has_unknownName_falseAfterInit() {
        TilesetLoader.resetForTestsOnly();
        assertFalse(TilesetLoader.has("__unknown_tile_name__"));
        TilesetLoader.initialize();
    }

    /** Verifies {@link TilesetLoader#get(String)} returns null for a missing name after init. */
    @Test
    void get_unknownNameAfterInit_returnsNull() {
        TilesetLoader.initialize();
        assertNull(TilesetLoader.get("__definitely_missing_tile__"));
    }
}
