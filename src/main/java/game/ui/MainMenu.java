package game.ui;

import game.core.SoundManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * The main menu panel. Shows the game title, navigation options,
 * and an animated neon border. Can also flip to a "How to Play" overlay.
 */
public class MainMenu extends JPanel {

    /**
     * Invoked when the player confirms EXIT. Defaults to {@code System.exit(0)}; tests may replace
     * this via {@link #setMenuExitActionForTest(Runnable)} so Surefire is not killed.
     */
    private static volatile Runnable menuExitAction = () -> System.exit(0);

    /**
     * Replaces the EXIT action (e.g. with a no-op or assertion). Pass {@code null} to restore default.
     */
    static void setMenuExitActionForTest(Runnable action) {
        menuExitAction = action != null ? action : () -> System.exit(0);
    }

    private final GameWindow gameWindow;
    private int selectedOption = 0;
    private final String[] menuOptions = {"1 PLAYER", "2 PLAYERS", "HOW TO PLAY", "EXIT"};
    private int animFrame = 0;
    private Timer animTimer;
    private boolean showInstructions = false;
    private BorderAnimation borderAnimation = new BorderAnimation();

    public int getSelectedOption() { return selectedOption; }
    public boolean isShowingInstructions() { return showInstructions; }

    public MainMenu(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setBackground(PixelArtRenderer.DARK_BG);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        animTimer = new Timer(50, e -> {
            animFrame++;
            borderAnimation.update();
            repaint();
        });
        animTimer.start();

        SoundManager.getInstance().playBGM("menu");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                 // Dismiss the instructions overlay with any confirm key
                if (showInstructions) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                        showInstructions = false;
                    }
                    repaint();
                    return;
                }

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        selectedOption = (selectedOption - 1 + menuOptions.length) % menuOptions.length;
                        break;

                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        selectedOption = (selectedOption + 1) % menuOptions.length;
                        break;

                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        selectOption();
                        break;

                    case KeyEvent.VK_M:
                        SoundManager.getInstance().toggleMute();
                        break;
                }

                repaint();
            }
        });
    }

    /**
     * Executes whichever menu option is currently highlighted:
     * <ol>
     *   <li>1 Player → go to character select</li>
     *   <li>2 Players → start level 1 in co-op immediately</li>
     *   <li>How to Play → overlay the instruction screen</li>
     *   <li>Exit → quit the application</li>
     * </ol>
     */
    private void selectOption() {
        switch (selectedOption) {
            case 0: stopAnimation(); gameWindow.showCharacterSelect(); break;
            case 1: stopAnimation(); gameWindow.startGame(1, true, null); break;
            case 2: showInstructions = true; break;
            case 3: menuExitAction.run(); break;
        }
    }

    /** Stops the animation timer — call this before navigating away from the menu. */
    public void stopAnimation() { if (animTimer != null) animTimer.stop(); }

    // ── Painting ──────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2d);
    
        if (!showInstructions) {
            borderAnimation.draw(g2d, getWidth(), getHeight());
            drawMainMenu(g2d);
        } else {
            drawInstructions(g2d);
        }

        drawMuteButton(g2d);
    }

    /** Draws the mute/unmute icon in the top-right corner of the menu. */
    private void drawMuteButton(Graphics2D g) {
        int size = (int)(getHeight() * 0.036); 
        int rightOffset = (int)(getWidth() * 0.06);
        int x = getWidth() - size - rightOffset;
        int y = (int)(getHeight() * 0.04);

        boolean muted = SoundManager.getInstance().isMuted();
        g.setFont(new Font("Consolas", Font.BOLD, size));
        g.setColor(muted ? Color.RED : PixelArtRenderer.NEON_CYAN);

        String icon = muted ? "\u25A0" : "\u266B";
        g.drawString(icon, x + 2, y + size);
    }
/** Dark gradient with a subtle grid overlay. */
    private void drawBackground(Graphics2D g) {
        int w = getWidth(), h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, PixelArtRenderer.DARK_BG,
                                             0, h, PixelArtRenderer.DARK_PURPLE);
        g.setPaint(gp);
        g.fillRect(0, 0, w, h);

        g.setColor(new Color(50, 40, 80, 50));
        int grid = (int)(getWidth() * 0.047); // scale grid spacing
        for (int i = 0; i < w; i += grid) g.drawLine(i, 0, i, h);
        for (int i = 0; i < h; i += grid) g.drawLine(0, i, w, i);
    }

  /** Draws the title text, subtitle, and the navigable list of menu options. */
  private void drawMainMenu(Graphics2D g) {
    int w = getWidth(), h = getHeight();
    int cx = w / 2;
    float pulse = (float)(Math.sin(animFrame * 0.1) * 0.3 + 0.7);

    // Titles
    int titleFont = (int)(h * 0.094);
    g.setFont(new Font("Consolas", Font.BOLD, titleFont));
    FontMetrics fmTitle = g.getFontMetrics();
    int subFont = (int)(h * 0.022);
    int menuFont = (int)(h * 0.036);
    int menuSpacing = (int)(h * 0.084);

    int titleBlockTop = (int)(h * 0.18);
    int elX = cx - fmTitle.stringWidth("ELEMENTAL") / 2;
    int qX  = cx - fmTitle.stringWidth("QUEST") / 2;
    int titleY1 = titleBlockTop + fmTitle.getAscent();
    int titleY2 = titleY1 + fmTitle.getHeight();
    int titleHeight = titleFont;
    int subtitleHeight = subFont;
    int menuHeight = menuFont * menuOptions.length + menuSpacing * (menuOptions.length - 1);

    int totalBlockHeight = titleHeight + subtitleHeight * 2 + menuHeight + 20;
    int startY = (h - totalBlockHeight) / 2;
g.drawString("ELEMENTAL", elX + 3, titleY1 + 3);
g.drawString("QUEST",     qX  + 3, titleY2 + 3);

g.setColor(PixelArtRenderer.NEON_PINK);
g.drawString("ELEMENTAL", elX, titleY1);
g.setColor(PixelArtRenderer.NEON_PINK);
g.drawString("QUEST", qX, titleY2);

g.setFont(new Font("Consolas", Font.PLAIN, subFont));
FontMetrics fmSub = g.getFontMetrics();
String sub = "A Cyberpunk Puzzle Adventure";
int spaceBelowQuest = (int)(h * 0.04);
int subY = titleY2 + spaceBelowQuest + fmSub.getAscent();
int subX = cx - fmSub.stringWidth(sub) / 2;
g.setColor(new Color(0, 0, 0, 120));
g.drawString(sub, subX + 2, subY + 2);
g.setColor(PixelArtRenderer.NEON_GREEN);
g.drawString(sub, subX, subY);

    // Menu options
    g.setFont(new Font("Consolas", Font.BOLD, menuFont));
    int menuStartY = subY + menuFont * 2;
    for (int i = 0; i < menuOptions.length; i++) {
        int iy = menuStartY + i * menuSpacing;
        if (i == selectedOption) {
            float borderPulse = (float)(Math.sin(animFrame * 0.15) * 0.35 + 0.65);

            g.setColor(new Color(0, 255, 255, (int)(120 * pulse)));
            g.fillRoundRect(cx - 155, iy - 30, 310, 48, 12, 12);

            g.setColor(new Color(PixelArtRenderer.NEON_CYAN.getRed(),
                                 PixelArtRenderer.NEON_CYAN.getGreen(),
                                 PixelArtRenderer.NEON_CYAN.getBlue(), (int)(80 * borderPulse)));
            g.setStroke(new BasicStroke(3.5f));
            g.drawRoundRect(cx - 158, iy - 33, 316, 54, 14, 14);

            g.setColor(PixelArtRenderer.NEON_CYAN);
            g.setStroke(new BasicStroke(2.5f));
            g.drawRoundRect(cx - 155, iy - 30, 310, 48, 12, 12);
            g.setStroke(new BasicStroke(1));

            g.drawString(">", cx - 178, iy);
            g.drawString("<", cx + 162, iy);
            g.setColor(Color.WHITE);
        } else g.setColor(new Color(140, 140, 170));

        FontMetrics fm = g.getFontMetrics();
        g.drawString(menuOptions[i], cx - fm.stringWidth(menuOptions[i]) / 2, iy);
    }
}

    /** Draws the full-screen "How to Play" overlay with controls and objectives. */
    private void drawInstructions(Graphics2D g) {
        int w = getWidth(), h = getHeight();
        int cx = w / 2;
        int padX = (int)(w * 0.02), padY = (int)(h * 0.02);
        int boxW = w - padX * 2, boxH = h - padY * 2;
        int contentOffset = 25;

        g.setColor(new Color(18, 14, 38, 245));
        g.fillRoundRect(padX, padY, boxW, boxH, 22, 22);
        g.setColor(PixelArtRenderer.NEON_CYAN);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(padX, padY, boxW, boxH, 22, 22);
        g.setStroke(new BasicStroke(1));

        // Title
        int titleFont = (int)(h * 0.039);
        int underlineWidth = (int)(boxW * 0.55);
        int underlineX = cx - underlineWidth / 2;
        int underlineY = padY + titleFont + 32 + contentOffset;
        g.setFont(new Font("Consolas", Font.BOLD, 34));
        g.setColor(PixelArtRenderer.NEON_CYAN);
        FontMetrics fmTitle = g.getFontMetrics();
        String title = "HOW TO PLAY";
        g.drawString(title, cx - fmTitle.stringWidth(title) / 2, padY + 34 + 20);
        g.setColor(new Color(PixelArtRenderer.NEON_CYAN.getRed(),
                             PixelArtRenderer.NEON_CYAN.getGreen(),
                             PixelArtRenderer.NEON_CYAN.getBlue(), 120));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(underlineX, underlineY, underlineX + underlineWidth, underlineY);
        g.setStroke(new BasicStroke(1));

        int textFont = (int)(h * 0.022);
        g.setFont(new Font("Consolas", Font.PLAIN, textFont));
        int lineSpacing = (int)(textFont * 1.5);
        int textX = padX + 28;
        int y = padY + titleFont + 72 + contentOffset;

        String[] lines = {
            "CONTROLS:",
            "  1-PLAYER  \u2013  WASD or Arrow keys to move  (one step per press)",
            "  1-PLAYER  \u2013  SPACE / ENTER to shoot  |  Q or E to use ability",
            "  1-PLAYER  \u2013  ESC to pause / exit to menu",
            "  2-PLAYERS \u2013  P1 EMBER: WASD + SPACE + Q",
            "  2-PLAYERS \u2013  P2 AQUA:  Arrows + ENTER + SHIFT",
            "",
            "CHARACTERS:",
            "  EMBER  \u2013  Fire spirit.  Safe on lava, damaged by water.",
            "  AQUA   \u2013  Water spirit. Safe on water, damaged by lava.",
            "",
            "ABILITY  (Q / E / SHIFT)  \u2013  3 charges max:",
            "  \u25b8 Destroys a spike trap in front of you  (+25 score)",
            "  EMBER: clears water ahead  \u2013  OR  \u2013  places lava on empty tile",
            "  AQUA:  clears lava ahead   \u2013  OR  \u2013  places water on empty tile",
            "  Tip: place lava/water ON a Shadow Sentinel to kill it!  (+50)",
            "",
            "OBJECTIVES:",
            "  1. Collect all Elemental Crystals to unlock the exit gate.",
            "  2. Reach your exit gate to complete the level.",
            "  3. Shoot or trap Shadow Sentinels for bonus points.",
            "  4. Collect Golden Gems for extra score.",
            "  5. Spike traps damage ALL characters \u2013 shoot or ability to destroy."
        };

        for (String line : lines) {
            if      (line.startsWith("CONTROLS"))  g.setColor(PixelArtRenderer.NEON_GREEN);
            else if (line.startsWith("CHARACTE"))  g.setColor(PixelArtRenderer.NEON_GREEN);
            else if (line.startsWith("ABILITY"))   g.setColor(PixelArtRenderer.NEON_GREEN);
            else if (line.startsWith("OBJECTIV"))  g.setColor(PixelArtRenderer.NEON_GREEN);
            else if (line.contains("EMBER"))       g.setColor(new Color(200, 200, 220));
            else if (line.contains("AQUA"))        g.setColor(new Color(200, 200, 220));
            else if (line.contains("Tip:"))        g.setColor(new Color(255, 220, 80));
            else                                   g.setColor(new Color(200, 200, 220));
            g.drawString(line, textX, y);
            y += line.isEmpty() ? lineSpacing / 2 : lineSpacing;
        }

        g.setColor(PixelArtRenderer.NEON_GREEN);
        g.setFont(new Font("Consolas", Font.BOLD, textFont));
        FontMetrics fmBack = g.getFontMetrics();
        String back = "Press ENTER or ESC to return";
        g.drawString(back, cx - fmBack.stringWidth(back) / 2, padY + boxH - textFont);
    }
}