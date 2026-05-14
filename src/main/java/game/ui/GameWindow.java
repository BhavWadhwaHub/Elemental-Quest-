package game.ui;

import javax.swing.*;
import java.awt.*;

/**
 * The main JFrame. Uses a {@link CardLayout} to swap between
 * the main menu, character select, and in-game panel.
 */
public class GameWindow extends JFrame {

    private MainMenu              mainMenu;
    private CharacterSelectScreen charSelect;
    private GamePanel             gamePanel;
    private final CardLayout      cardLayout;
    private final JPanel          mainPanel;

    /**
     * Builds the main window, wires up the card layout, and displays the main menu.
     * The window is sized to 1024×768 and centred on screen by default.
     */
    public GameWindow() {
        setTitle("Elemental Quest - Cyberpunk Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);

        mainMenu   = new MainMenu(this);
        charSelect = new CharacterSelectScreen(this);

        mainPanel.add(mainMenu,   "MENU");
        mainPanel.add(charSelect, "CHAR_SELECT");

        add(mainPanel);
        pack();
        setSize(1024, 768);
        setLocationRelativeTo(null);

        cardLayout.show(mainPanel, "MENU");
        mainMenu.requestFocusInWindow();
    }

    /** Stops any running game and returns to the main menu. */
    public void showMenu() {
        if (gamePanel != null) {
            gamePanel.stopGame();
            mainPanel.remove(gamePanel);
            gamePanel = null;
        }
        mainMenu = new MainMenu(this);
        mainPanel.add(mainMenu, "MENU");
        cardLayout.show(mainPanel, "MENU");
        mainMenu.requestFocusInWindow();
    }

    /** Opens the character selection screen. */
    public void showCharacterSelect() {
        charSelect = new CharacterSelectScreen(this);
        mainPanel.add(charSelect, "CHAR_SELECT");
        cardLayout.show(mainPanel, "CHAR_SELECT");
        charSelect.requestFocusInWindow();
    }

    /**
     * Starts (or restarts) a game level.
     *
     * @param levelNumber 1-based level index
     * @param twoPlayer   {@code true} for simultaneous two-player mode
     * @param character   "EMBER" or "AQUA" for single-player; {@code null} for two-player
     */
    public void startGame(int levelNumber, boolean twoPlayer, String character) {
        if (gamePanel != null) {
            gamePanel.stopGame();
            mainPanel.remove(gamePanel);
        }
        gamePanel = new GamePanel(this, levelNumber, twoPlayer, character);
        mainPanel.add(gamePanel, "GAME");
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
    }

    /** {@code true} while an in-game {@link GamePanel} is attached (between {@link #startGame} and {@link #showMenu}). */
    public boolean hasActiveGamePanel() {
        return gamePanel != null;
    }

    /** Same-package access for integration tests (not part of the public game API). */
    GamePanel getActiveGamePanelForTests() {
        return gamePanel;
    }
}
