package game.core;

import game.ui.GameWindow;
import javax.swing.*;

/**
 * Entry point — creates the window and hands control to Swing's event thread.
 */
public class GameManager {
    /**
     * Application entry point. Applies the system look-and-feel for native window
     * decorations, then schedules the {@link GameWindow} to open on the Swing
     * event-dispatch thread.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        initLookAndFeel();
        SwingUtilities.invokeLater(() -> openGameWindow(true));
    }

    /**
     * Opens a new {@link GameWindow}. Package-private so tests can use {@code visible=false} and dispose.
     *
     * @return the created window (caller may {@link GameWindow#dispose()} in tests)
     */
    static GameWindow openGameWindow(boolean visible) {
        GameWindow window = new GameWindow();
        window.setVisible(visible);
        return window;
    }

    /**
     * Applies the system look-and-feel (same steps as {@link #main}).
     * Package-private so tests in {@code game.core} can verify startup without opening a window.
     */
    static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
