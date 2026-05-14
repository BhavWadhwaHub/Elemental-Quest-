package game.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import game.core.SoundManager;

/**
 * Single-player character selection screen.
 * Player picks EMBER or AQUA with A/D (or LEFT/RIGHT), confirms with ENTER.
 */
public class CharacterSelectScreen extends JPanel {

    private final GameWindow gameWindow;
    private int selectedIndex = 0;  // 0 = EMBER, 1 = AQUA
    private int animFrame = 0;
    private Timer animTimer;
    private Rectangle muteButton = new Rectangle(0,0,32,32);
    private boolean mouseInsideMute = false;

    private static final String[] CHAR_NAMES    = {"EMBER", "AQUA"};
    private static final String[] CHAR_ELEMENTS = {"ELEMENT: FIRE", "ELEMENT: WATER"};
    private static final String[][] CHAR_TRAITS = {
        {"Immune to lava tiles", "Damaged by water tiles", "Shoots fireballs", "Ability: traps / clear water", "Ability: place lava on floor"},
        {"Immune to water tiles", "Damaged by lava tiles", "Shoots waterballs", "Ability: traps / clear lava", "Ability: place water on floor"}
    };

    public CharacterSelectScreen(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        setPreferredSize(new Dimension(1024, 768));
        setBackground(PixelArtRenderer.DARK_BG);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        animTimer = new Timer(50, e -> {
            animFrame++;
            PixelArtRenderer.updateAnimation();
            repaint();
        });
        animTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        selectedIndex = 0;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        selectedIndex = 1;
                        break;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        confirm();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        stopAnimation();
                        gameWindow.showMenu();
                        break;
                    case KeyEvent.VK_M:
                        SoundManager.getInstance().toggleMute();
                        break;
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (muteButton.contains(e.getPoint())) {
                    SoundManager.getInstance().toggleMute();
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseInsideMute = muteButton.contains(e.getPoint());
                repaint();
            }
        });
    }

    /** Stops the animation timer and launches the game with the currently highlighted character. */
    private void confirm() {
        stopAnimation();
        gameWindow.startGame(1, false, CHAR_NAMES[selectedIndex]);
    }

    /** Stops the animation timer — should be called before navigating away from this screen. */
    public void stopAnimation() {
        if (animTimer != null) animTimer.stop();
    }

    // ── Paint ────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);
        drawNeonBorder(g2);
        drawMuteButton(g2);
        drawTitle(g2);
        drawDivider(g2);
        drawCards(g2);
        drawConfirmHint(g2);
        drawFooter(g2);
    }

    /** Draws a pulsing neon rectangle around the screen edge. */
    private void drawNeonBorder(Graphics2D g) {
        float pulse = (float)(Math.sin(animFrame * 0.08) * 0.25 + 0.75);
        int margin = Math.max(20, getWidth()/50);

        g.setColor(new Color(0, 255, 255, (int)(60 * pulse)));
        g.setStroke(new BasicStroke(6f));
        g.drawRoundRect(margin, margin, getWidth() - 2*margin, getHeight() - 2*margin, 22, 22);

        g.setColor(new Color(0, 255, 255, 180));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(margin, margin, getWidth() - 2*margin, getHeight() - 2*margin, 22, 22);
    }

    /** Paints the dark gradient background with a subtle grid overlay. */
    private void drawBackground(Graphics2D g) {
        GradientPaint gp = new GradientPaint(0, 0, PixelArtRenderer.DARK_BG,
                                             0, getHeight(), PixelArtRenderer.DARK_PURPLE);
        g.setPaint(gp);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(new Color(50, 40, 80, 50));
        for (int i = 0; i < getWidth();  i += 40) g.drawLine(i, 0, i, getHeight());
        for (int i = 0; i < getHeight(); i += 40) g.drawLine(0, i, getWidth(), i);
        g.setColor(new Color(0, 255, 255, 25));
    }

    /** Draws the "SELECT YOUR CHARACTER" heading and keyboard-hint subtitle. */
    private void drawTitle(Graphics2D g) {
        int panelW = getWidth();
        int panelH = getHeight();
        int titleSize = (int)(panelH * 0.045);
        g.setFont(new Font("Consolas", Font.BOLD, titleSize));
        g.setColor(PixelArtRenderer.NEON_CYAN);
        String title = "SELECT YOUR CHARACTER";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, (panelW - fm.stringWidth(title)) / 2, (int)(panelH * 0.18));

        g.setFont(new Font("Consolas", Font.PLAIN, (int)(panelH*0.022)));
        g.setColor(new Color(150, 150, 180));
        String sub = "A / ← choose EMBER     D / → choose AQUA     ENTER to confirm";
        fm = g.getFontMetrics();
        g.drawString(sub, (panelW - fm.stringWidth(sub)) / 2, (int)(panelH * 0.23));
    }

    /** Draws a faint horizontal rule between the title and the character cards. */
    private void drawDivider(Graphics2D g) {
        int panelW = getWidth();
        int panelH = getHeight(); 
        int y = (int)(panelH * 0.25);
        g.setColor(new Color(0, 255, 255, 60));
        g.setStroke(new BasicStroke(2f));
        int margin = (int)(panelW * 0.2);
        g.drawLine(margin, y, panelW - margin, y);
    }

    /** Lays out and draws the two character selection cards side by side. */
    private void drawCards(Graphics2D g) {
        int panelW = getWidth();
        int panelH = getHeight();

        int cardH = (int)(panelH * 0.44);
        int cardW = (int)(panelW * 0.25);
        int gap   = (int)(panelW * 0.08);
        int startX = (panelW - (cardW*2 + gap)) / 2;
        int cardY = (int)(panelH * 0.3);

        drawCard(g, startX, cardY, cardW, cardH, 0);
        drawCard(g, startX + cardW + gap, cardY, cardW, cardH, 1);
    }

    /**
     * Draws a single character card at the given bounds.
     * The card shows the character's sprite preview, name, element tag,
     * trait list, and a "SELECTED" badge when highlighted.
     *
     * @param cx      left edge of the card
     * @param cy      top edge of the card
     * @param cw      card width
     * @param ch      card height
     * @param charIdx 0 for Ember, 1 for Aqua
     */
    private void drawCard(Graphics2D g, int cx, int cy, int cw, int ch, int charIdx) {
        boolean selected = (selectedIndex == charIdx);
        boolean isEmber  = (charIdx == 0);
        Color accent     = isEmber ? PixelArtRenderer.NEON_ORANGE : PixelArtRenderer.NEON_CYAN;
        float pulse = (float)(Math.sin(animFrame * 0.12) * 0.35 + 0.65);

        // Background
        g.setColor(selected ? new Color(30, 25, 55, 240) : new Color(18, 14, 38, 210));
        g.fillRoundRect(cx, cy, cw, ch, 18, 18);

        // Border
        if (selected) {
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int)(70 * pulse)));
            g.setStroke(new BasicStroke(4f));
            g.drawRoundRect(cx - 3, cy - 3, cw + 6, ch + 6, 22, 22);
            g.setColor(accent);
            g.setStroke(new BasicStroke(2.5f));
            g.drawRoundRect(cx, cy, cw, ch, 18, 18);
        } else {
            g.setColor(new Color(70, 60, 100));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(cx, cy, cw, ch, 18, 18);
        }
        g.setStroke(new BasicStroke(1));

        // Sprite preview
        int previewSize = (int)(ch * 0.23);
        int spriteX = cx + (cw - previewSize) / 2;
        int spriteY = cy + (int)(ch * 0.05);
        BufferedImage sprite = new BufferedImage(
                PixelArtRenderer.TILE_SIZE,
                PixelArtRenderer.TILE_SIZE,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D sg = sprite.createGraphics();
        if (isEmber) PixelArtRenderer.drawEmber(sg, 0, 0, selected);
        else         PixelArtRenderer.drawAqua(sg, 0, 0, selected);
        sg.dispose();
        g.drawImage(sprite, spriteX, spriteY, previewSize, previewSize, null);

        // Name
        int nameY = spriteY + previewSize + (int)(ch*0.06);
        g.setFont(new Font("Consolas", Font.BOLD, (int)(ch*0.06)));
        g.setColor(selected ? accent : new Color(170, 160, 200));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(CHAR_NAMES[charIdx], cx + (cw - fm.stringWidth(CHAR_NAMES[charIdx])) / 2, nameY);

        // Element
        g.setFont(new Font("Consolas", Font.PLAIN, (int)(ch*0.035)));
        g.setColor(selected ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 200)
                            : new Color(110, 100, 140));
        fm = g.getFontMetrics();
        g.drawString(CHAR_ELEMENTS[charIdx], cx + (cw - fm.stringWidth(CHAR_ELEMENTS[charIdx])) / 2, nameY + (int)(ch*0.05));

        // Divider above traits
        g.setColor(new Color(100, 100, 140, 80));
        g.drawLine(cx + 12, nameY + (int)(ch*0.08), cx + cw - 12, nameY + (int)(ch*0.08));

        // Traits
        Shape oldClip = g.getClip();
        g.setClip(cx + 4, cy + 4, cw - 8, ch - 8);
        g.setFont(new Font("Consolas", Font.PLAIN, (int)(ch*0.04)));
        g.setColor(selected ? new Color(200, 200, 225) : new Color(120, 110, 150));
        int traitY = nameY + (int)(ch*0.15);
        for (String trait : CHAR_TRAITS[charIdx]) {
            g.drawString("\u2022 " + trait, cx + 12, traitY);
            traitY += (int)(ch*0.07);
        }
        g.setClip(oldClip);

        // SELECTED badge
        if (selected) {
            g.setFont(new Font("Consolas", Font.BOLD, (int)(ch*0.04)));
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int)(200 * pulse)));
            String badge = "[ SELECTED ]";
            fm = g.getFontMetrics();
            g.drawString(badge, cx + (cw - fm.stringWidth(badge)) / 2, cy + ch - (int)(ch*0.035));
        }
    }

    /** Draws the "Press ENTER to play as …" prompt and the ESC / mute hints below the cards. */
    private void drawConfirmHint(Graphics2D g) {
        int panelW = getWidth();
        int panelH = getHeight();
        int unit = (int)(panelH * 0.035);

        int hintY = (int)(panelH * 0.82);
        int enterY = hintY;
        int backY  = hintY + unit;
        int muteY  = hintY + unit * 2;

        g.setFont(new Font("Consolas", Font.BOLD, (int)(panelH*0.03)));
        Color accent = selectedIndex == 0 ? PixelArtRenderer.NEON_ORANGE : PixelArtRenderer.NEON_CYAN;
        g.setColor(accent);
        String msg = "Press ENTER to play as " + CHAR_NAMES[selectedIndex];
        FontMetrics fm = g.getFontMetrics();
        g.drawString(msg, (panelW - fm.stringWidth(msg)) / 2, enterY);

        // ESC line
        g.setFont(new Font("Consolas", Font.PLAIN, (int)(panelH*0.022)));
        g.setColor(new Color(100, 100, 130));
        String back = "ESC ← back to menu";
        fm = g.getFontMetrics();
        g.drawString(back, (panelW - fm.stringWidth(back)) / 2, backY);

        // MUTE line
        String muteHint = "M ← to mute music";
        fm = g.getFontMetrics();
        g.drawString(muteHint, (panelW - fm.stringWidth(muteHint)) / 2, muteY);

    }

    /** Draws the small version/credit string at the very bottom of the screen. */
    private void drawFooter(Graphics2D g) {
        int panelW = getWidth();
        int panelH = getHeight();

        g.setFont(new Font("Consolas", Font.PLAIN, (int)(panelH*0.016)));
        g.setColor(new Color(90, 90, 120));
        String footer = "Elemental Quest — v1.0";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(footer, (panelW - fm.stringWidth(footer)) / 2, panelH - (int)(panelH*0.05));
    }

    /** Draws the mute/unmute music icon in the top-right corner. */
    private void drawMuteButton(Graphics2D g) {
        int panelW = getWidth();
        int panelH = getHeight();

        int size = (int)(panelW*0.027);
        int x = panelW - size - (int)(panelW*0.03);
        int y = (int)(panelH*0.04);
        muteButton.setBounds(x, y, size, size);

        boolean muted = SoundManager.getInstance().isMuted();
        g.setFont(new Font("Consolas", Font.BOLD, size));
        g.setColor(muted ? Color.RED : PixelArtRenderer.NEON_CYAN);
        String icon = muted ? "\u25A0" : "\u266B";
        g.drawString(icon, x + 2, y + size - 2);
    }
}