package game.core;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Singleton audio manager – MP3 playback via mp3spi.
 *
 * Files are searched in this order (first match wins):
 *   1. ./audio/<name>.mp3          ← folder next to the JAR / project root  ← EASIEST
 *   2. classpath /audio/<name>.mp3 ← bundled inside the JAR after mvn package
 *
 * So just create an  audio/  folder next to the JAR (or in the project root)
 * and drop your files there:
 *   audio/menu.mp3     – main-menu loop
 *   audio/battle.mp3   – in-level loop
 *   audio/attack.mp3   – shoot sound effect
 */
public class SoundManager {

    private static SoundManager instance;

    private Clip   bgmClip;
    private String currentBGM = "";
    private float  bgmVolume  = 0.60f;
    private float  sfxVolume  = 0.85f;
    private boolean muted = false;

    /** Returns {@code true} if all audio output is currently silenced. */
    public boolean isMuted() {
        return muted;
    }

    /**
     * Flips the mute flag. Stops the currently playing BGM when muting,
     * and resumes it automatically when un-muting.
     */
    public void toggleMute() {
        muted = !muted;

        if (muted) {
            if (bgmClip != null) bgmClip.stop();
        } else {
            if (currentBGM != null && !currentBGM.isEmpty()) {
                playBGM(currentBGM);
            }
        }
    }

    // ────────────────────────────────────────────────────────────────

    /** Private constructor — use {@link #getInstance()} instead. */
    private SoundManager() {}

    /**
     * Returns the single shared instance, creating it on first call.
     * Thread safety is not a concern here since Swing is single-threaded.
     */
    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    // ── Background music (looping) ───────────────────────────────────────────

    /**
     * Starts looping the named BGM track. Does nothing if that track is
     * already playing, and gracefully skips if the audio file is missing.
     *
     * @param trackName base filename without extension (e.g. {@code "menu"})
     */
    public void playBGM(String trackName) {
        if (muted) return;
        if (trackName.equals(currentBGM) && bgmClip != null && bgmClip.isRunning()) return;
        stopBGM();
        Clip clip = loadClip(trackName);
        if (clip == null) return;
        setVolume(clip, bgmVolume);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
        bgmClip    = clip;
        currentBGM = trackName;
    }

    /** Stops and releases the current BGM clip so the next track can load cleanly. */
    public void stopBGM() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.close();
            bgmClip    = null;
            currentBGM = "";
        }
    }

    // ── Sound effects (one-shot, non-blocking) ─────────────────────────────────────────────

    /**
     * Plays a one-shot sound effect on a daemon thread so it never blocks the game loop.
     * The clip is automatically closed as soon as playback finishes.
     *
     * @param trackName base filename without extension (e.g. {@code "attack"})
     */
    public void playSFX(String trackName) {
        if (muted) return;
        Thread t = new Thread(() -> {
            Clip clip = loadClip(trackName);
            if (clip == null) return;
            setVolume(clip, sfxVolume);
            clip.addLineListener(ev -> {
                if (ev.getType() == LineEvent.Type.STOP) clip.close();
            });
            clip.start();
        }, "sfx-" + trackName);
        t.setDaemon(true);
        t.start();
    }

    // ── Clip loader ──────────────────────────────────────────────────────────

    private Clip loadClip(String name) {
        InputStream raw = findStream(name);
        if (raw == null) {
            System.err.println("[SoundManager] audio file not found: " + name + ".mp3");
            System.err.println("  -> Place it at:  audio/" + name + ".mp3  next to the JAR");
            return null;
        }
        try {
             // Decode MP3 → PCM (mp3spi registers itself via META-INF/services)
            AudioInputStream mp3  = AudioSystem.getAudioInputStream(new BufferedInputStream(raw));
            AudioFormat base      = mp3.getFormat();
            AudioFormat pcm       = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(), 16,
                    base.getChannels(), base.getChannels() * 2,
                    base.getSampleRate(), false);
            AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcm, mp3);
            Clip clip = (Clip) AudioSystem.getLine(new DataLine.Info(Clip.class, pcm));
            clip.open(pcmStream);
            return clip;
        } catch (Exception e) {
            System.err.println("[SoundManager] failed to open clip for: " + name + "  (" + e.getMessage() + ")");
            return null;
        }
    }

   /** Try filesystem first, then classpath, then return null. */
    private InputStream findStream(String name) {
        String filename = name + ".mp3";

        // 1. Filesystem: ./audio/<name>.mp3  (works without rebuilding)
        try {
            File f = new File("audio/" + filename);
            if (f.exists()) return new BufferedInputStream(new FileInputStream(f));
        } catch (Exception ignored) {}

        // 2. Classpath (bundled in JAR after mvn package)
        InputStream cp = SoundManager.class.getResourceAsStream("/audio/" + filename);
        if (cp != null) return new BufferedInputStream(cp);

        return null;
    }

    // ── Volume helper ─────────────────────────────────────────────────────────

    private void setVolume(Clip clip, float volume) {
        try {
            FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float min = fc.getMinimum(), max = fc.getMaximum();
            fc.setValue(min + (max - min) * volume);
        } catch (Exception ignored) {}
    }

    /**
     * Sets the background music volume. Value is clamped to [0.0, 1.0].
     * Takes effect on the next {@link #playBGM} call.
     *
     * @param v desired volume fraction (0.0 = silent, 1.0 = full)
     */
    public void setBGMVolume(float v) { bgmVolume = Math.max(0f, Math.min(1f, v)); }

    /**
     * Sets the sound-effects volume. Value is clamped to [0.0, 1.0].
     *
     * @param v desired volume fraction (0.0 = silent, 1.0 = full)
     */
    public void setSFXVolume(float v) { sfxVolume = Math.max(0f, Math.min(1f, v)); }

    /** Current BGM volume fraction after clamping (0.0–1.0). */
    public float getBGMVolume() {
        return bgmVolume;
    }

    /** Current SFX volume fraction after clamping (0.0–1.0). */
    public float getSFXVolume() {
        return sfxVolume;
    }
}