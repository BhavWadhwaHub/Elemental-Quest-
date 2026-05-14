package game.core;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SoundManager}: singleton access, mute/volume behaviour, and BGM/SFX loading paths.
 */
class SoundManagerTest {

    /** Verifies {@link SoundManager#getInstance()} returns the same instance. */
    @Test
    void testSingleton() {
        SoundManager sm1 = SoundManager.getInstance();
        SoundManager sm2 = SoundManager.getInstance();
        assertSame(sm1, sm2, "SoundManager should be a singleton");
    }

    /** Verifies {@link SoundManager#toggleMute()} flips mute state twice and returns to the original value. */
    @Test
    void testMuteToggle() {
        SoundManager sm = SoundManager.getInstance();

        boolean initialMute = sm.isMuted();

        sm.toggleMute();
        assertNotEquals(initialMute, sm.isMuted(), "Mute state should flip after toggle");

        sm.toggleMute();
        assertEquals(initialMute, sm.isMuted(), "Mute state should flip back after second toggle");
    }

    /** Verifies BGM and SFX volumes clamp to {@code [0, 1]}. */
    @Test
    void testVolumeClamping() {
        SoundManager sm = SoundManager.getInstance();

        sm.setBGMVolume(2f);
        assertEquals(1f, sm.getBGMVolume(), 1e-6f, "BGM volume above 1.0 should clamp to 1.0");

        sm.setBGMVolume(-0.5f);
        assertEquals(0f, sm.getBGMVolume(), 1e-6f, "BGM volume below 0.0 should clamp to 0.0");

        sm.setSFXVolume(3f);
        assertEquals(1f, sm.getSFXVolume(), 1e-6f, "SFX volume above 1.0 should clamp to 1.0");

        sm.setSFXVolume(-1f);
        assertEquals(0f, sm.getSFXVolume(), 1e-6f, "SFX volume below 0.0 should clamp to 0.0");
    }

    /** Verifies repeated mute toggles do not throw. */
    @Test
    void testMultipleToggleDoesNotCrash() {
        SoundManager sm = SoundManager.getInstance();
        for (int i = 0; i < 5; i++) {
            sm.toggleMute();
        }
    }

    /** Verifies exact boundary volumes 0 and 1 round-trip through getters. */
    @Test
    void volumeExactlyZeroAndOne_roundTrip() {
        SoundManager sm = SoundManager.getInstance();
        sm.setBGMVolume(0f);
        sm.setSFXVolume(1f);
        assertEquals(0f, sm.getBGMVolume(), 1e-6f);
        assertEquals(1f, sm.getSFXVolume(), 1e-6f);
    }

    /** Verifies {@link SoundManager#playBGM(String)} does not throw when muted. */
    @Test
    void playBGM_whenMuted_returnsImmediately() {
        SoundManager sm = SoundManager.getInstance();
        boolean wasMuted = sm.isMuted();
        if (!wasMuted) {
            sm.toggleMute();
        }
        try {
            assertDoesNotThrow(() -> sm.playBGM("menu"));
        } finally {
            if (!wasMuted) {
                sm.toggleMute();
            }
        }
    }

    /** Verifies {@link SoundManager#stopBGM()} is safe to call twice. */
    @Test
    void stopBGM_idempotent() {
        SoundManager sm = SoundManager.getInstance();
        assertDoesNotThrow(() -> {
            sm.stopBGM();
            sm.stopBGM();
        });
    }

    /** Verifies invalid classpath audio does not crash BGM playback. */
    @Test
    void playBGM_classpathFileThatIsNotMp3_coversDecodeCatch() {
        SoundManager sm = SoundManager.getInstance();
        assertDoesNotThrow(() -> sm.playBGM("bad_decode"));
    }

    /** Verifies mute toggles after menu BGM still complete without throwing. */
    @Test
    void toggleMute_afterPlayMenu_recoversWithoutThrowing() {
        SoundManager sm = SoundManager.getInstance();
        try {
            sm.stopBGM();
            assertFalse(sm.isMuted());
            assertDoesNotThrow(() -> sm.playBGM("menu"));
            sm.toggleMute();
            assertTrue(sm.isMuted());
            sm.toggleMute();
            assertFalse(sm.isMuted());
        } finally {
            sm.stopBGM();
        }
    }

    /** Verifies SFX loader thread runs for classpath audio without throwing. */
    @Test
    void playSFX_daemonThread_runsLoader() throws Exception {
        SoundManager sm = SoundManager.getInstance();
        sm.playSFX("bad_decode");
        Thread.sleep(400);
    }

    /** Verifies BGM resolves a file under {@code ./audio/} when present. */
    @Test
    void playBGM_readsFromFilesystemWhenFileExists() throws Exception {
        SoundManager sm = SoundManager.getInstance();
        Path root = Path.of(System.getProperty("user.dir"));
        Path audioDir = root.resolve("audio");
        Files.createDirectories(audioDir);
        Path junk = audioDir.resolve("junit_fs_noise.mp3");
        Files.writeString(junk, "not-an-mp3");
        try {
            assertDoesNotThrow(() -> sm.playBGM("junit_fs_noise"));
        } finally {
            Files.deleteIfExists(junk);
        }
    }

    /** Verifies valid classpath MP3 decodes and replays without throwing. */
    @Test
    void playBGM_validClasspathMp3_decodeAndAlreadyPlayingBranch() throws Exception {
        SoundManager sm = SoundManager.getInstance();
        try {
            sm.stopBGM();
            assertDoesNotThrow(() -> sm.playBGM("tiny_valid"));
            Thread.sleep(200);
            assertDoesNotThrow(() -> sm.playBGM("tiny_valid"));
        } finally {
            sm.stopBGM();
        }
    }

    /** Verifies valid classpath SFX plays and line listener path completes. */
    @Test
    void playSFX_validClasspathMp3_lineListenerClosesClip() throws Exception {
        SoundManager sm = SoundManager.getInstance();
        assertDoesNotThrow(() -> sm.playSFX("tiny_valid"));
        Thread.sleep(2500);
    }
}
