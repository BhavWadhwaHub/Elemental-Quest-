package game.ui;

import game.character.*;
import game.collectible.BonusCrystal;
import game.collectible.Collectible;
import game.collectible.ElementalCrystal;
import game.combat.Projectile;
import game.core.*;
import game.enemy.*;
import game.gate.FireGate;
import game.gate.WaterGate;
import game.level.*;
import game.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The main game panel — handles the game loop, rendering, and all input.
 * Everything visible during gameplay goes through here.
 */
public class GamePanel extends JPanel implements ActionListener {

    // Screen dimensions
    private static final int W = 1024;
    private static final int H = 768;
    private static final int HUD_HEIGHT = 90;

    // Game mode flags
    private final boolean twoPlayer;
    private final String selectedCharacter; // "EMBER", "AQUA", or null for 2P

    // Core objects
    private final GameWindow gameWindow;
    private Level level;
    private Player player;
    private GameTimer gameTimer;
    private ScoreManager scoreManager;
    private GameState gameState;
    private Timer gameLoop;
    private Timer animationTimer;
    private int levelNumber;

    // Enemy respawn — keeps the sentinel count stable after kills
    private int targetEnemyCount = 0;
    private int spawnCooldown = 0;
    private static final int SPAWN_DELAY_TICKS = 320; // ~5 seconds at 16ms/tick
    private String spawnFlashMsg = "";
    private int spawnFlashTick = 0;
    private final Random spawnRng = new Random();

    // Input state
    private Direction lastDirEmber = Direction.RIGHT;
    private Direction lastDirAqua = Direction.RIGHT;
    private static final long MOVE_DELAY_MS = 120;
    private long lastMoveEmber = 0;
    private long lastMoveAqua = 0;
    private final Set<Integer> heldKeys = new HashSet<>();

    // Damage cooldown — stops a single enemy from draining all hearts instantly
    private static final long DAMAGE_COOLDOWN_MS = 1000;
    private long lastDamageTime = 0;

    // Shooting cooldown — 5 seconds between shots
    private static final long SHOOT_COOLDOWN_MS = 5000;
    private long lastShotEmber = Long.MIN_VALUE / 2;
    private long lastShotAqua = Long.MIN_VALUE / 2;

    // Pause menu: 0 = Resume, 1 = Exit to Menu
    private int pauseOption = 0;

    // Active projectiles in flight
    private final List<Projectile> projectiles = new ArrayList<>();

    // Tile size pulled from the renderer
    private final int TILE_SIZE = PixelArtRenderer.getTileSize();

    // --- Cached fonts (created once, reused every frame) ---
    private static final Font FONT_NAME = new Font("Consolas", Font.BOLD, 14);
    private static final Font FONT_PWR = new Font("Consolas", Font.BOLD, 11);
    private static final Font FONT_SCORE = new Font("Consolas", Font.BOLD, 12);
    private static final Font FONT_LEVEL = new Font("Consolas", Font.BOLD, 17);
    private static final Font FONT_TIME = new Font("Consolas", Font.BOLD, 15);
    private static final Font FONT_HINT = new Font("Consolas", Font.PLAIN, 10);
    private static final Font FONT_2P_NAME = new Font("Consolas", Font.BOLD, 13);
    private static final Font FONT_2P_LVL = new Font("Consolas", Font.BOLD, 16);
    private static final Font FONT_2P_TIME = new Font("Consolas", Font.BOLD, 14);
    private static final Font FONT_2P_SCR = new Font("Consolas", Font.BOLD, 12);
    private static final Font FONT_2P_HINT = new Font("Consolas", Font.PLAIN, 9);
    private static final Font FONT_WARN = new Font("Consolas", Font.BOLD, 22);
    private static final Font FONT_PAUSE = new Font("Consolas", Font.BOLD, 38);
    private static final Font FONT_OPT = new Font("Consolas", Font.BOLD, 20);
    private static final Font FONT_SMALL = new Font("Consolas", Font.PLAIN, 12);
    private static final Font FONT_OVERLAY = new Font("Consolas", Font.BOLD, 34);
    private static final Font FONT_OVR_MED = new Font("Consolas", Font.BOLD, 18);
    private static final Font FONT_OVR_SML = new Font("Consolas", Font.PLAIN, 15);
    private static final Font FONT_GAMEOVER = new Font("Consolas", Font.BOLD, 42);
    private static final Font FONT_GO_SML = new Font("Consolas", Font.PLAIN, 17);

    // --- Cached colors ---
    private static final Color COLOR_HUD_BG = new Color(20, 15, 35);
    private static final Color COLOR_PWR_LOW = new Color(110, 50, 50);
    private static final Color COLOR_HINT_TEXT = new Color(80, 80, 110);
    private static final Color COLOR_GATES_LOCKED = new Color(130, 130, 150);
    private static final Color COLOR_2P_EMBER_HINT = new Color(100, 75, 45);
    private static final Color COLOR_2P_CTR_HINT = new Color(70, 70, 100);
    private static final Color COLOR_2P_AQUA_HINT = new Color(40, 85, 95);
    private static final Color COLOR_OVERLAY_DIM = new Color(0, 0, 0, 170);
    private static final Color COLOR_PANEL_DARK = new Color(25, 20, 48, 245);
    private static final Color COLOR_PANEL_RED = new Color(50, 18, 28, 245);
    private static final Color COLOR_OPT_UNSELECTED = new Color(140, 140, 170);
    private static final Color COLOR_PAUSE_HINT = new Color(90, 90, 120);

    // --- Cached strokes ---
    private static final BasicStroke STROKE_1 = new BasicStroke(1f);
    private static final BasicStroke STROKE_2 = new BasicStroke(2f);
    private static final BasicStroke STROKE_2_5 = new BasicStroke(2.5f);

    // End-state flags
    private boolean levelComplete = false;
    private boolean gameOver = false;
    private String gameOverReason = "";

    /**
     * Creates and starts the game panel for the given level and mode.
     *
     * @param gameWindow parent window
     * @param levelNumber level to load (1–3)
     * @param twoPlayer true for 2-player co-op
     * @param selectedCharacter "EMBER" or "AQUA" for 1P, null for 2P
     */
    public GamePanel(GameWindow gameWindow, int levelNumber,
                     boolean twoPlayer, String selectedCharacter) {
        this.gameWindow = gameWindow;
        this.levelNumber = levelNumber;
        this.twoPlayer = twoPlayer;
        this.selectedCharacter = selectedCharacter;
        this.scoreManager = new ScoreManager();
        this.gameState = GameState.PLAYING;

        initializeLevel();
        targetEnemyCount = level.getBoard().getEnemies().size();

        setPreferredSize(new Dimension(W, H));
        setBackground(PixelArtRenderer.DARK_BG);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        setupKeyBindings();
        startGameLoop();
        SoundManager.getInstance().playBGM("battle");
    }

    /** Sets up the board, timer, and player positions for the current level. */
    private void initializeLevel() {
        if (twoPlayer) {
            level = new Level(levelNumber);
        } else {
            level = new Level(levelNumber, selectedCharacter);
        }
        gameTimer = new GameTimer(level.getTimeLimit());
        player = new Player(
            level.getEmberStart()[0], level.getEmberStart()[1],
            level.getAquaStart()[0],  level.getAquaStart()[1]
        );
        // In 1P Aqua mode, switch the active character right away
        if (!twoPlayer && "AQUA".equals(selectedCharacter)) {
            player.switchCharacter();
        }
        gameTimer.start();
    }

    /** Registers all keyboard input for movement, shooting, and menus. */
    private void setupKeyBindings() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();

                // If we're on an end screen, just wait for the player to continue
                if (levelComplete || gameOver) {
                    if (key == KeyEvent.VK_ENTER || key == KeyEvent.VK_SPACE) {
                        SoundManager.getInstance().stopBGM();
                        if (levelComplete && levelNumber < 3)
                            gameWindow.startGame(levelNumber + 1, twoPlayer, selectedCharacter);
                        else
                            gameWindow.showMenu();
                    }
                    return;
                }

                // Pause menu navigation
                if (gameState == GameState.PAUSED) {
                    switch (key) {
                        case KeyEvent.VK_W:
                        case KeyEvent.VK_UP:
                            pauseOption = 0;
                            break;
                        case KeyEvent.VK_S:
                        case KeyEvent.VK_DOWN:
                            pauseOption = 1;
                            break;
                        case KeyEvent.VK_ENTER:
                        case KeyEvent.VK_SPACE:
                            if (pauseOption == 1) {
                                SoundManager.getInstance().stopBGM();
                                gameWindow.showMenu();
                            } else {
                                resumeGame();
                            }
                            break;
                        case KeyEvent.VK_ESCAPE:
                            resumeGame();
                            break;
                    }
                    repaint();
                    return;
                }

                long now = System.currentTimeMillis();

                if (twoPlayer) {
                    // 2P controls — Ember on WASD/SPACE/Q, Aqua on Arrows/ENTER/SHIFT
                    switch (key) {
                        case KeyEvent.VK_W:
                            if (!heldKeys.contains(key) && now - lastMoveEmber >= MOVE_DELAY_MS) {
                                moveEmber(Direction.UP); lastMoveEmber = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_S:
                            if (!heldKeys.contains(key) && now - lastMoveEmber >= MOVE_DELAY_MS) {
                                moveEmber(Direction.DOWN); lastMoveEmber = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_A:
                            if (!heldKeys.contains(key) && now - lastMoveEmber >= MOVE_DELAY_MS) {
                                moveEmber(Direction.LEFT); lastMoveEmber = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_D:
                            if (!heldKeys.contains(key) && now - lastMoveEmber >= MOVE_DELAY_MS) {
                                moveEmber(Direction.RIGHT); lastMoveEmber = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_SPACE:
                            if (now - lastShotEmber >= SHOOT_COOLDOWN_MS) {
                                fireShot(player.getEmber(), lastDirEmber);
                                lastShotEmber = now;
                            }
                            break;
                        case KeyEvent.VK_Q:
                            useAbility(player.getEmber(), lastDirEmber);
                            break;
                        case KeyEvent.VK_UP:
                            if (!heldKeys.contains(key) && now - lastMoveAqua >= MOVE_DELAY_MS) {
                                moveAqua(Direction.UP); lastMoveAqua = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_DOWN:
                            if (!heldKeys.contains(key) && now - lastMoveAqua >= MOVE_DELAY_MS) {
                                moveAqua(Direction.DOWN); lastMoveAqua = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_LEFT:
                            if (!heldKeys.contains(key) && now - lastMoveAqua >= MOVE_DELAY_MS) {
                                moveAqua(Direction.LEFT); lastMoveAqua = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_RIGHT:
                            if (!heldKeys.contains(key) && now - lastMoveAqua >= MOVE_DELAY_MS) {
                                moveAqua(Direction.RIGHT); lastMoveAqua = now; heldKeys.add(key);
                            }
                            break;
                        case KeyEvent.VK_ENTER:
                            if (now - lastShotAqua >= SHOOT_COOLDOWN_MS) {
                                fireShot(player.getAqua(), lastDirAqua);
                                lastShotAqua = now;
                            }
                            break;
                        case KeyEvent.VK_SHIFT:
                            useAbility(player.getAqua(), lastDirAqua);
                            break;
                        case KeyEvent.VK_ESCAPE:
                            pauseGame();
                            break;
                    }
                } else {
                    // 1P controls — WASD or Arrows to move, SPACE/ENTER to shoot, Q/E for ability
                    Direction dir = null;
                    switch (key) {
                        case KeyEvent.VK_W:
                        case KeyEvent.VK_UP:
                            dir = Direction.UP;
                            break;
                        case KeyEvent.VK_S:
                        case KeyEvent.VK_DOWN:
                            dir = Direction.DOWN;
                            break;
                        case KeyEvent.VK_A:
                        case KeyEvent.VK_LEFT:
                            dir = Direction.LEFT;
                            break;
                        case KeyEvent.VK_D:
                        case KeyEvent.VK_RIGHT:
                            dir = Direction.RIGHT;
                            break;
                        case KeyEvent.VK_SPACE:
                        case KeyEvent.VK_ENTER:
                            if (now - lastShotEmber >= SHOOT_COOLDOWN_MS) {
                                fireShot(getActiveSingleChar(), lastDirEmber);
                                lastShotEmber = now;
                            }
                            break;
                        case KeyEvent.VK_Q:
                        case KeyEvent.VK_E:
                            useAbility(getActiveSingleChar(), lastDirEmber);
                            break;
                        case KeyEvent.VK_ESCAPE:
                            pauseGame();
                            break;
                    }
                    if (dir != null && !heldKeys.contains(key) && now - lastMoveEmber >= MOVE_DELAY_MS) {
                        moveSingle(dir);
                        lastMoveEmber = now;
                        heldKeys.add(key);
                    }
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                heldKeys.remove(e.getKeyCode());
            }
        });
    }

    /** Returns whichever character is active in single-player mode. */
    private ElementalCharacter getActiveSingleChar() {
        return "AQUA".equals(selectedCharacter) ? player.getAqua() : player.getEmber();
    }

    /** Moves Ember one tile in {@code dir} and remembers it as her last facing direction. */
    private void moveEmber(Direction dir) {
        lastDirEmber = dir;
        if (player.getEmber().move(level.getBoard(), dir)) postMove(player.getEmber());
    }

    /** Moves Aqua one tile in {@code dir} and remembers it as her last facing direction. */
    private void moveAqua(Direction dir) {
        lastDirAqua = dir;
        if (player.getAqua().move(level.getBoard(), dir)) postMove(player.getAqua());
    }

    /** Moves the active single-player character one tile in {@code dir}. */
    private void moveSingle(Direction dir) {
        lastDirEmber = dir;
        ElementalCharacter ch = getActiveSingleChar();
        if (ch.move(level.getBoard(), dir)) postMove(ch);
    }

    /** Runs all the post-move checks after any character steps. */
    private void postMove(ElementalCharacter moved) {
        checkCollectiblesFor(moved);
        checkEnemyCollision();
        checkGates();
    }

    /** Checks if any player is standing on a hazard, applying damage on a cooldown. */
    private void checkHazards() {
        long now = System.currentTimeMillis();
        if (now - lastDamageTime >= DAMAGE_COOLDOWN_MS) {
            boolean hit = false;
            
            if (twoPlayer) {
                if (isHazard(player.getEmber())) {
                    player.takeEmberDamage();
                    hit = true;
                }
                if (isHazard(player.getAqua())) {
                    player.takeAquaDamage();
                    hit = true;
                }
            } else {
                if (isHazard(getActiveSingleChar())) {
                    player.takeDamage();
                    hit = true;
                }
            }
            
            if (hit) {
                lastDamageTime = now;
                if (!player.isAlive()) triggerGameOver("All hearts lost!");
            }
        }
    }

    /** Returns true if the character is standing on a hazard cell. */
    private boolean isHazard(ElementalCharacter ch) {
        Cell cell = level.getBoard().getCell(ch.getX(), ch.getY());
        return cell != null && ch.takesHazardDamage(cell);
    }

    /** Checks if the character walked onto a collectible. */
    private void checkCollectiblesFor(ElementalCharacter ch) {
        Board board = level.getBoard();
        Iterator<Collectible> iter = board.getCollectibles().iterator();
        while (iter.hasNext()) {
            Collectible c = iter.next();
            if (c.getX() != ch.getX() || c.getY() != ch.getY() || c.isCollected()) continue;

            if (c instanceof ElementalCrystal) {
                ElementalCrystal crystal = (ElementalCrystal) c;
                boolean wrongElement = crystal.getElementType() != ch.getElementType();

                // In 2P, collecting the wrong crystal costs 100 points
                if (wrongElement && twoPlayer) {
                    scoreManager.deductPoints(100);
                    spawnFlashMsg = "WRONG CRYSTAL! -100";
                    spawnFlashTick = 50;
                    if (scoreManager.isNegative()) {
                        triggerGameOver("Score went negative!");
                        return;
                    }
                } else {
                    scoreManager.addCrystalPoints(crystal.getPointValue());
                }

                c.collect(player);
                level.collectCrystal();
                if (level.checkCompletion()) {
                    if (board.getFireGate()  != null) board.getFireGate().unlock();
                    if (board.getWaterGate() != null) board.getWaterGate().unlock();
                }
                iter.remove();
                board.detachCollectibleEntity(c);
                continue;
            }

            if (c instanceof BonusCrystal) {
                c.collect(player);
                scoreManager.addGemPoints(c.getPointValue());
                spawnFlashMsg = "BONUS CRYSTAL! +100";
                spawnFlashTick = 60;
                iter.remove();
                board.detachCollectibleEntity(c);
            }
        }
    }

    /** Checks if any enemy is on the same tile as a player. */
    private void checkEnemyCollision() {
        long now = System.currentTimeMillis();
        if (now - lastDamageTime < DAMAGE_COOLDOWN_MS) return;

        Board board = level.getBoard();
        List<ShadowSentinel> snap = new ArrayList<>(board.getEnemies());
        boolean emberHit = false;
        boolean aquaHit = false;
        boolean singleHit = false;

        for (ShadowSentinel enemy : snap) {
            if (twoPlayer) {
                if (!emberHit && enemy.getX() == player.getEmber().getX() && enemy.getY() == player.getEmber().getY()) {
                    player.takeEmberDamage();
                    emberHit = true;
                }
                if (!aquaHit && enemy.getX() == player.getAqua().getX()  && enemy.getY() == player.getAqua().getY()) {
                    player.takeAquaDamage();
                    aquaHit = true;
                }
            } else {
                if (!singleHit) {
                    ElementalCharacter a = getActiveSingleChar();
                    if (enemy.getX() == a.getX() && enemy.getY() == a.getY()) {
                        player.takeDamage();
                        singleHit = true;
                    }
                }
            }
        }

        if (emberHit || aquaHit || singleHit) {
            lastDamageTime = now;
            if (!player.isAlive()) triggerGameOver("Caught by Shadow Sentinel!");
        }
    }

    /** Checks if a player has stepped onto their unlocked gate. */
    private void checkGates() {
        Board board = level.getBoard();
        FireGate fg = board.getFireGate();
        WaterGate wg = board.getWaterGate();
        Ember ember = player.getEmber();
        Aqua aqua = player.getAqua();

        if (twoPlayer) {
            boolean emberReady = fg != null && fg.isUnlocked()
                && ember.getX() == fg.getX() && ember.getY() == fg.getY();
            boolean aquaReady = wg != null && wg.isUnlocked()
                && aqua.getX() == wg.getX() && aqua.getY() == wg.getY();
            player.setEmberAtGate(emberReady);
            player.setAquaAtGate(aquaReady);
            if (player.bothAtGates() && level.checkCompletion()) triggerLevelComplete();
        } else if ("AQUA".equals(selectedCharacter)) {
            if (wg != null && wg.isUnlocked()
                    && aqua.getX() == wg.getX() && aqua.getY() == wg.getY()
                    && level.checkCompletion()) {
                triggerLevelComplete();
            }
        } else {
            if (fg != null && fg.isUnlocked()
                    && ember.getX() == fg.getX() && ember.getY() == fg.getY()
                    && level.checkCompletion()) {
                triggerLevelComplete();
            }
        }
    }

    /** Fires a projectile from the character in the direction they're facing. */
    private void fireShot(ElementalCharacter shooter, Direction dir) {
        int sx = shooter.getX() + dir.getDx();
        int sy = shooter.getY() + dir.getDy();
        Board board = level.getBoard();
        if (!board.isValidPosition(sx, sy) || board.getCell(sx, sy).isWall()) return;

        Projectile proj = new Projectile(sx, sy, dir, shooter.getElementType());
        projectiles.add(proj);

        // Check for immediate hits right at the spawn tile
        if (destroyTrapAt(sx, sy)) {
            proj.deactivate();
        } else {
            killEnemiesAt(sx, sy, proj);
        }
        SoundManager.getInstance().playSFX("attack");
    }

    /** Removes any enemy at the given tile and awards kill points. */
    private void killEnemiesAt(int tx, int ty, Projectile proj) {
        List<ShadowSentinel> snap = new ArrayList<>(level.getBoard().getEnemies());
        for (ShadowSentinel enemy : snap) {
            if (enemy.getX() == tx && enemy.getY() == ty) {
                level.getBoard().removeEnemy(enemy);
                scoreManager.addKillPoints(50);
                proj.deactivate();
                break;
            }
        }
    }

    /**
     * Destroys a spike trap at the given tile if one exists.
     *
     * @return true if a trap was destroyed
     */
    private boolean destroyTrapAt(int tx, int ty) {
        Board board = level.getBoard();
        Cell cell = board.getCell(tx, ty);
        if (cell != null && cell.isTrap()) {
            cell.setTrap(false);
            scoreManager.addTrapPoints(25);
            spawnFlashMsg = "TRAP DESTROYED! +25";
            spawnFlashTick = 50;
            return true;
        }
        return false;
    }

    /** Activates the character's special ability in the direction they last moved. */
    private void useAbility(ElementalCharacter ch, Direction dir) {
        int targetX = ch.getX() + dir.getDx();
        int targetY = ch.getY() + dir.getDy();
        Board board = level.getBoard();
        Cell targetCell = board.getCell(targetX, targetY);

        // Award trap points before the ability removes it
        if (targetCell != null && targetCell.isTrap() && ch.getAbilityCharges() > 0) {
            scoreManager.addTrapPoints(25);
            spawnFlashMsg = "TRAP DESTROYED! +25";
            spawnFlashTick = 50;
        }

        ch.useAbility(board, dir);

        // If lava/water was placed, check if a sentinel is standing there
        Cell placed = board.getCell(targetX, targetY);
        if (placed != null && (placed.isLava() || placed.isWater())) {
            List<ShadowSentinel> snap = new ArrayList<>(board.getEnemies());
            for (ShadowSentinel enemy : snap) {
                if (enemy.getX() == targetX && enemy.getY() == targetY) {
                    board.removeEnemy(enemy);
                    scoreManager.addKillPoints(50);
                    spawnFlashMsg = placed.isLava() ? "SENTINEL BURNED! +50" : "SENTINEL DROWNED! +50";
                    spawnFlashTick = 60;
                    break;
                }
            }
        }
    }

    /** Moves all active projectiles and resolves any new hits. */
    private void updateProjectiles() {
        Board board = level.getBoard();
        Iterator<Projectile> pIter = projectiles.iterator();
        while (pIter.hasNext()) {
            Projectile proj = pIter.next();
            if (!proj.isActive()) { pIter.remove(); continue; }
            if (!proj.tick()) continue;

            proj.advance();
            int px = proj.getX(), py = proj.getY();

            if (!board.isValidPosition(px, py) || board.getCell(px, py).isWall()) {
                proj.deactivate();
                pIter.remove();
                continue;
            }
            if (destroyTrapAt(px, py)) {
                proj.deactivate();
                pIter.remove();
                continue;
            }
            killEnemiesAt(px, py, proj);
            if (!proj.isActive()) pIter.remove();
        }
    }

    /**
     * Placeholder for gem update logic — gems are static so this is intentionally empty.
     * Kept here so the game loop stays easy to extend later.
     */
    private void updateGems() {}

    /** Freezes the timer and shows the pause overlay. */
    private void pauseGame()  { gameState = GameState.PAUSED;   gameTimer.stop();  }

    /** Resumes the timer and returns to active gameplay. */
    private void resumeGame() { gameState = GameState.PLAYING;  gameTimer.start(); }

    /** Stops the timer, tallies the time bonus, and switches to the level-complete state. */
    private void triggerLevelComplete() {
        levelComplete = true;
        gameTimer.stop();
        scoreManager.addTimeBonus(gameTimer.getTimeRemaining());
        gameState = GameState.LEVEL_COMPLETE;
    }

    /**
     * Ends the game with a reason string shown on the game over screen.
     *
     * @param reason short description of why the game ended
     */
    private void triggerGameOver(String reason) {
        gameOver = true;
        gameOverReason = reason;
        gameTimer.stop();
        gameState = GameState.GAME_OVER;
    }

    /** Starts the game loop at ~60fps, plus a slower animation tick at 50ms. */
    private void startGameLoop() {
        gameLoop = new Timer(16, this);
        gameLoop.start();
        // Animation timer only advances the frame counter — repaint is driven by the game loop
        animationTimer = new Timer(50, e -> PixelArtRenderer.updateAnimation());
        animationTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameState == GameState.PLAYING) {
            gameTimer.update();
            if (gameTimer.hasExpired()) triggerGameOver("Time's up!");
            updateProjectiles();
            updateGems();
            level.getBoard().updateEnemies();
            checkEnemyCollision();
            checkHazards();
            checkSentinelRespawn();
            if (spawnFlashTick > 0) spawnFlashTick--;
            repaint();
        }
    }

    /** Respawns a sentinel if the current count dropped below the original target. */
    private void checkSentinelRespawn() {
        if (levelComplete || gameOver) return;
        int current = level.getBoard().getEnemies().size();
        if (current < targetEnemyCount) {
            spawnCooldown++;
            if (spawnCooldown >= SPAWN_DELAY_TICKS) {
                spawnCooldown = 0;
                if (spawnSentinel()) {
                    spawnFlashMsg = "! SENTINEL INCOMING !";
                    spawnFlashTick = 80;
                }
            }
        } else {
            spawnCooldown = 0;
        }
    }

    /**
     * Picks a random safe tile and spawns a new sentinel there.
     *
     * @return true if a spawn position was found
     */
    private boolean spawnSentinel() {
        Board board = level.getBoard();
        List<int[]> candidates = new ArrayList<>();

        for (int x = 1; x < board.getWidth() - 1; x++) {
            for (int y = 1; y < board.getHeight() - 1; y++) {
                Cell cell = board.getCell(x, y);
                if (cell.isWall() || cell.isLava() || cell.isWater()) continue;
                if (!isFreeTile(x, y, board)) continue;
                if (!isAwayFromPlayers(x, y, 5)) continue;
                candidates.add(new int[]{x, y});
            }
        }

        if (candidates.isEmpty()) return false;
        int[] pos = candidates.get(spawnRng.nextInt(candidates.size()));
        board.addEnemy(new ShadowSentinel(pos[0], pos[1], 1));
        return true;
    }

    /** Returns true if no player or enemy is currently standing on this tile. */
    private boolean isFreeTile(int x, int y, Board board) {
        for (ShadowSentinel s : board.getEnemies())
            if (s.getX() == x && s.getY() == y) return false;
        Ember em = player.getEmber();
        Aqua aq = player.getAqua();
        if (em.getX() == x && em.getY() == y) return false;
        if (aq.getX() == x && aq.getY() == y) return false;
        return true;
    }

    /**
     * Returns true if the given tile is at least {@code minDist} tiles away from all players.
     * Uses Manhattan distance so it's cheap to compute.
     */
    private boolean isAwayFromPlayers(int x, int y, int minDist) {
        Ember em = player.getEmber();
        Aqua aq = player.getAqua();
        if (twoPlayer) {
            if (Math.abs(em.getX() - x) + Math.abs(em.getY() - y) < minDist) return false;
            if (Math.abs(aq.getX() - x) + Math.abs(aq.getY() - y) < minDist) return false;
        } else {
            ElementalCharacter ch = getActiveSingleChar();
            if (Math.abs(ch.getX() - x) + Math.abs(ch.getY() - y) < minDist) return false;
        }
        return true;
    }

    // ── Rendering ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Speed hints: no AA for shapes (pixel art), AA on for text, prefer speed
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_SPEED);

        drawHUD(g2d);
        drawBoard(g2d);

        if (spawnFlashTick > 0)                  drawSpawnWarning(g2d);
        if (gameState == GameState.PAUSED)        drawPauseOverlay(g2d);
        else if (levelComplete)                   drawLevelCompleteOverlay(g2d);
        else if (gameOver)                        drawGameOverOverlay(g2d);
    }

    /** Draws the top HUD bar (picks 1P or 2P layout). */
    private void drawHUD(Graphics2D g) {
        g.setColor(COLOR_HUD_BG);
        g.fillRect(0, 0, getWidth(), HUD_HEIGHT);
        g.setColor(PixelArtRenderer.NEON_PURPLE);
        g.drawLine(0, HUD_HEIGHT - 1, getWidth(), HUD_HEIGHT - 1);
        if (twoPlayer) drawHUD2P(g); else drawHUD1P(g);
    }

    /** Single-player HUD layout. */
    private void drawHUD1P(Graphics2D g) {
        int w = getWidth(), cx = w / 2;
        boolean isEmber = !"AQUA".equals(selectedCharacter);
        ElementalCharacter ch = getActiveSingleChar();

        // Left side — character name, hearts, power charges, score
        g.setFont(FONT_NAME);
        g.setColor(isEmber ? PixelArtRenderer.NEON_ORANGE : PixelArtRenderer.NEON_CYAN);
        g.drawString(isEmber ? "EMBER" : "AQUA", 14, 20);
        for (int i = 0; i < player.getMaxLives(); i++)
            PixelArtRenderer.drawHeart(g, 14 + i * 22, 26, i < player.getLives());
        int charges = ch.getAbilityCharges();
        g.setFont(FONT_PWR);
        g.setColor(charges > 0 ? PixelArtRenderer.NEON_GREEN : COLOR_PWR_LOW);
        g.drawString("PWR  " + charges + " / 3", 14, 62);
        g.setFont(FONT_SCORE);
        g.setColor(PixelArtRenderer.NEON_GREEN);
        g.drawString("SCORE  " + scoreManager.getScore(), 14, 78);

        // Centre — level number and timer
        g.setFont(FONT_LEVEL);
        g.setColor(PixelArtRenderer.NEON_CYAN);
        String lvl = "LEVEL " + levelNumber;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(lvl, cx - fm.stringWidth(lvl) / 2, 24);
        int time = gameTimer.getTimeRemaining();
        g.setFont(FONT_TIME);
        g.setColor(time <= 30 ? PixelArtRenderer.NEON_PINK : PixelArtRenderer.NEON_ORANGE);
        String tmStr = String.format("TIME  %d:%02d", time / 60, time % 60);
        fm = g.getFontMetrics();
        g.drawString(tmStr, cx - fm.stringWidth(tmStr) / 2, 48);
        g.setFont(FONT_HINT);
        g.setColor(COLOR_HINT_TEXT);
        String hint = "WASD/Arrows \u2013 move   SPACE \u2013 shoot   Q/E \u2013 ability   ESC \u2013 pause";
        fm = g.getFontMetrics();
        g.drawString(hint, cx - fm.stringWidth(hint) / 2, 74);

        // Right side — crystal count, gate status, gems
        g.setFont(FONT_SCORE);
        int rx = w - 14;
        g.setColor(Color.WHITE);
        String cryst = "CRYSTALS  " + level.getCollectedCrystals() + " / " + level.getRequiredCrystals();
        fm = g.getFontMetrics();
        g.drawString(cryst, rx - fm.stringWidth(cryst), 20);
        g.setColor(level.checkCompletion() ? PixelArtRenderer.NEON_GREEN : COLOR_GATES_LOCKED);
        String gates = level.checkCompletion() ? "GATES  UNLOCKED" : "GATES  LOCKED";
        g.drawString(gates, rx - fm.stringWidth(gates), 38);
        g.setColor(PixelArtRenderer.GEM_YELLOW);
        String gems = "GEMS  " + scoreManager.getGemsCollected();
        g.drawString(gems, rx - fm.stringWidth(gems), 56);
    }

    /** Two-player HUD layout — Ember on left, Aqua on right, shared stats in the middle. */
    private void drawHUD2P(Graphics2D g) {
        int w = getWidth(), cx = w / 2;

        // P1 Ember — left side
        g.setFont(FONT_2P_NAME);
        g.setColor(PixelArtRenderer.NEON_ORANGE);
        g.drawString("P1  EMBER", 14, 18);
        for (int i = 0; i < player.getMaxLives(); i++)
            PixelArtRenderer.drawHeart(g, 14 + i * 22, 24, i < player.getEmberLives());
        int ec = player.getEmber().getAbilityCharges();
        g.setFont(FONT_PWR);
        g.setColor(ec > 0 ? PixelArtRenderer.NEON_GREEN : COLOR_PWR_LOW);
        g.drawString("PWR  " + ec + " / 3", 14, 60);
        g.setFont(FONT_2P_HINT);
        g.setColor(COLOR_2P_EMBER_HINT);
        g.drawString("WASD \u00b7 SPACE \u00b7 Q", 14, 75);

        // Centre — level, timer, score, ESC hint
        g.setFont(FONT_2P_LVL);
        g.setColor(PixelArtRenderer.NEON_CYAN);
        String lvl = "LEVEL " + levelNumber;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(lvl, cx - fm.stringWidth(lvl) / 2, 20);
        int time = gameTimer.getTimeRemaining();
        g.setFont(FONT_2P_TIME);
        g.setColor(time <= 30 ? PixelArtRenderer.NEON_PINK : PixelArtRenderer.NEON_ORANGE);
        String tmStr = String.format("TIME  %d:%02d", time / 60, time % 60);
        fm = g.getFontMetrics();
        g.drawString(tmStr, cx - fm.stringWidth(tmStr) / 2, 40);
        g.setFont(FONT_2P_SCR);
        g.setColor(PixelArtRenderer.NEON_GREEN);
        String score = "SCORE  " + scoreManager.getScore();
        fm = g.getFontMetrics();
        g.drawString(score, cx - fm.stringWidth(score) / 2, 58);
        g.setFont(FONT_2P_HINT);
        g.setColor(COLOR_2P_CTR_HINT);
        String escHint = "ESC \u2013 pause";
        fm = g.getFontMetrics();
        g.drawString(escHint, cx - fm.stringWidth(escHint) / 2, 76);

        // Shared stats — crystal count, gate, gems (right of centre)
        int statsX = (int)(w * 0.63);
        g.setFont(FONT_2P_SCR);
        g.setColor(Color.WHITE);
        g.drawString("CRYSTALS  " + level.getCollectedCrystals() + " / " + level.getRequiredCrystals(), statsX, 20);
        g.setColor(level.checkCompletion() ? PixelArtRenderer.NEON_GREEN : COLOR_GATES_LOCKED);
        g.drawString(level.checkCompletion() ? "GATES  UNLOCKED" : "GATES  LOCKED", statsX, 38);
        g.setColor(PixelArtRenderer.GEM_YELLOW);
        g.drawString("GEMS  " + scoreManager.getGemsCollected(), statsX, 56);

        // P2 Aqua — right side (mirrored)
        g.setFont(FONT_2P_NAME);
        g.setColor(PixelArtRenderer.NEON_CYAN);
        int rx = w - 14;
        fm = g.getFontMetrics();
        String p2name = "AQUA  P2";
        g.drawString(p2name, rx - fm.stringWidth(p2name), 18);
        int heartsW = player.getMaxLives() * 22;
        for (int i = 0; i < player.getMaxLives(); i++)
            PixelArtRenderer.drawHeart(g, rx - heartsW + i * 22, 24, i < player.getAquaLives());
        int ac = player.getAqua().getAbilityCharges();
        g.setFont(FONT_PWR);
        g.setColor(ac > 0 ? PixelArtRenderer.NEON_GREEN : COLOR_PWR_LOW);
        String p2pwr = ac + " / 3  PWR";
        fm = g.getFontMetrics();
        g.drawString(p2pwr, rx - fm.stringWidth(p2pwr), 60);
        g.setFont(FONT_2P_HINT);
        g.setColor(COLOR_2P_AQUA_HINT);
        String p2hint = "ARROWS \u00b7 ENTER \u00b7 SHIFT";
        fm = g.getFontMetrics();
        g.drawString(p2hint, rx - fm.stringWidth(p2hint), 75);
    }

    /** Draws the board, scaling it to fit the space below the HUD. */
    private void drawBoard(Graphics2D g) {
        Board board = level.getBoard();
        Cell[][] cells = board.getCells();

        int availW = getWidth();
        int availH = getHeight() - HUD_HEIGHT;
        int naturalW = board.getWidth()  * TILE_SIZE;
        int naturalH = board.getHeight() * TILE_SIZE;
        float scale = Math.min((float) availW / naturalW, (float) availH / naturalH);
        int scaledW = (int)(naturalW * scale);
        int scaledH = (int)(naturalH * scale);
        int bx = (availW - scaledW) / 2;
        int by = HUD_HEIGHT + (availH - scaledH) / 2;

        AffineTransform saved = g.getTransform();
        g.translate(bx, by);
        g.scale(scale, scale);

        // Tiles
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                int dx = x * TILE_SIZE, dy = y * TILE_SIZE;
                Cell cell = cells[x][y];
                if      (cell.isWall())  PixelArtRenderer.drawWall(g, dx, dy);
                else if (cell.isTrap())  PixelArtRenderer.drawTrap(g, dx, dy);
                else if (cell.isLava())  PixelArtRenderer.drawLava(g, dx, dy);
                else if (cell.isWater()) PixelArtRenderer.drawWater(g, dx, dy);
                else                     PixelArtRenderer.drawFloor(g, dx, dy);
            }
        }

        // Gates
        FireGate fg = board.getFireGate();
        WaterGate wg = board.getWaterGate();
        if (fg != null) PixelArtRenderer.drawFireGate(g,  fg.getX() * TILE_SIZE, fg.getY() * TILE_SIZE, fg.isUnlocked());
        if (wg != null) PixelArtRenderer.drawWaterGate(g, wg.getX() * TILE_SIZE, wg.getY() * TILE_SIZE, wg.isUnlocked());

        // Collectibles
        for (Collectible c : board.getCollectibles()) {
            int dx = c.getX() * TILE_SIZE, dy = c.getY() * TILE_SIZE;
            if (c instanceof ElementalCrystal) {
                PixelArtRenderer.drawCrystal(g, dx, dy,
                    ((ElementalCrystal) c).getElementType() == ElementType.FIRE);
            } else if (c instanceof BonusCrystal) {
                PixelArtRenderer.drawBonusCrystal(g, dx, dy);
            }
        }

        // Enemies and projectiles
        for (ShadowSentinel enemy : board.getEnemies())
            PixelArtRenderer.drawEnemy(g, enemy.getX() * TILE_SIZE, enemy.getY() * TILE_SIZE);
        for (Projectile proj : projectiles) {
            int dx = proj.getX() * TILE_SIZE, dy = proj.getY() * TILE_SIZE;
            if (proj.isFireball()) PixelArtRenderer.drawFireball(g, dx, dy);
            else                   PixelArtRenderer.drawWaterball(g, dx, dy);
        }

        // Player character(s)
        Ember em = player.getEmber();
        Aqua aq = player.getAqua();
        if (twoPlayer) {
            PixelArtRenderer.drawEmber(g, em.getX() * TILE_SIZE, em.getY() * TILE_SIZE, true);
            drawDirectionIndicator(g, em.getX() * TILE_SIZE, em.getY() * TILE_SIZE, lastDirEmber, PixelArtRenderer.NEON_ORANGE);
            PixelArtRenderer.drawAqua(g,  aq.getX() * TILE_SIZE, aq.getY() * TILE_SIZE, true);
            drawDirectionIndicator(g, aq.getX() * TILE_SIZE, aq.getY() * TILE_SIZE, lastDirAqua, PixelArtRenderer.NEON_CYAN);
        } else if ("AQUA".equals(selectedCharacter)) {
            PixelArtRenderer.drawAqua(g, aq.getX() * TILE_SIZE, aq.getY() * TILE_SIZE, true);
            drawDirectionIndicator(g, aq.getX() * TILE_SIZE, aq.getY() * TILE_SIZE, lastDirEmber, PixelArtRenderer.NEON_CYAN);
        } else {
            PixelArtRenderer.drawEmber(g, em.getX() * TILE_SIZE, em.getY() * TILE_SIZE, true);
            drawDirectionIndicator(g, em.getX() * TILE_SIZE, em.getY() * TILE_SIZE, lastDirEmber, PixelArtRenderer.NEON_ORANGE);
        }

        g.setTransform(saved);
    }

    /** Draws a small arrow next to the character tile to indicate their facing direction. */
    private void drawDirectionIndicator(Graphics2D g, int dx, int dy, Direction dir, Color color) {
        if (dir == null) return;
        int cx = dx + TILE_SIZE / 2;
        int cy = dy + TILE_SIZE / 2;
        
        // Push the indicator just outside the tile edge
        int offset = TILE_SIZE / 2 + 2; 
        
        int tipX = cx + dir.getDx() * (offset + 6);
        int tipY = cy + dir.getDy() * (offset + 6);
        
        // Calculate the base of the triangle perpendicular to the direction
        int base1X = cx + dir.getDx() * offset + dir.getDy() * 5;
        int base1Y = cy + dir.getDy() * offset - dir.getDx() * 5;
        
        int base2X = cx + dir.getDx() * offset - dir.getDy() * 5;
        int base2Y = cy + dir.getDy() * offset + dir.getDx() * 5;
        
        g.setColor(color);
        g.fillPolygon(new int[]{tipX, base1X, base2X}, new int[]{tipY, base1Y, base2Y}, 3);
    }

    /** Flash message shown when a sentinel spawns or a bonus is earned. */
    private void drawSpawnWarning(Graphics2D g) {
        float alpha = Math.min(1f, spawnFlashTick / 20f);
        int a = (int)(180 * alpha);
        g.setFont(FONT_WARN);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(spawnFlashMsg);
        int tx = (getWidth() - tw) / 2;
        int ty = HUD_HEIGHT + 38;
        // Drop shadow then the actual text
        g.setColor(new Color(0, 0, 0, a));
        g.drawString(spawnFlashMsg, tx + 2, ty + 2);
        g.setColor(new Color(255, 60, 30, a));
        g.drawString(spawnFlashMsg, tx, ty);
    }

  /** Pause overlay with Resume / Exit to Menu options. */
private void drawPauseOverlay(Graphics2D g) {
    int cx = getWidth() / 2, cy = getHeight() / 2;
    float pulse = (float)(Math.sin(System.currentTimeMillis() * 0.003) * 0.3 + 0.7);

    // Background dim
    g.setColor(COLOR_OVERLAY_DIM);
    g.fillRect(0, 0, getWidth(), getHeight());

    // Panel
    g.setColor(COLOR_PANEL_DARK);
    g.fillRoundRect(cx - 165, cy - 120, 330, 240, 22, 22);
    g.setColor(PixelArtRenderer.NEON_CYAN);
    g.setStroke(STROKE_2_5);
    g.drawRoundRect(cx - 165, cy - 120, 330, 240, 22, 22);
    g.setStroke(STROKE_1);
    FontMetrics fm;

    g.setFont(FONT_PAUSE);
    g.setColor(PixelArtRenderer.NEON_PINK);
    fm = g.getFontMetrics();
    String paused = "PAUSED";
    int pausedOffsetX = 10;
    g.drawString(paused, cx - fm.stringWidth(paused) / 2 + pausedOffsetX, cy - 60);
    String[] opts = {"\u25B8 RESUME", "\u2302 EXIT TO MENU"};
    Color[]  colors = {PixelArtRenderer.NEON_GREEN, PixelArtRenderer.NEON_ORANGE};
    for (int i = 0; i < opts.length; i++) {
        int oy = cy - 10 + i * 62;
        if (i == pauseOption) {
            g.setColor(new Color(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), (int)(60 * pulse)));
            g.fillRoundRect(cx - 130, oy - 26, 260, 44, 10, 10);
            g.setColor(colors[i]);
            g.setStroke(STROKE_2);
            g.drawRoundRect(cx - 130, oy - 26, 260, 44, 10, 10);
            g.setStroke(STROKE_1);
            g.setColor(Color.WHITE);
        } else {
            g.setColor(COLOR_OPT_UNSELECTED);
        }
        g.setFont(FONT_OPT);
        fm = g.getFontMetrics();
        g.drawString(opts[i], cx - fm.stringWidth(opts[i]) / 2, oy);
    }
    g.setFont(FONT_SMALL);
    g.setColor(COLOR_PAUSE_HINT);
    fm = g.getFontMetrics();
    String hint = "W/S  navigate    ENTER  confirm    ESC  resume";
    g.drawString(hint, cx - fm.stringWidth(hint) / 2, cy + 100);
}

    /** Level complete overlay with final score breakdown. */
   private void drawLevelCompleteOverlay(Graphics2D g) {
    int w = getWidth(), h = getHeight();
    int cx = w / 2, cy = h / 2;

    g.setColor(COLOR_OVERLAY_DIM);
    g.fillRect(0, 0, w, h);

    int panelW = 440, panelH = 330;
    int panelX = cx - panelW / 2;
    int panelY = cy - panelH / 2;

    g.setColor(COLOR_PANEL_DARK);
    g.fillRoundRect(panelX, panelY, panelW, panelH, 22, 22);

    g.setColor(PixelArtRenderer.NEON_GREEN);
    g.setStroke(STROKE_2_5);
    g.drawRoundRect(panelX, panelY, panelW, panelH, 22, 22);
    g.setStroke(STROKE_1);

    g.setFont(FONT_OVERLAY);
    FontMetrics fmTitle = g.getFontMetrics();
    String title = "LEVEL COMPLETE!";

    int titleY = panelY + 60;
    int titleX = cx - fmTitle.stringWidth(title) / 2;

    g.drawString(title, titleX, titleY);

    g.setFont(FONT_OVR_MED);
    g.setColor(Color.WHITE);
    FontMetrics fmMed = g.getFontMetrics();

    int itemsLeftX = cx - 140;

    int startY = titleY + 55;

    g.drawString("Final Score:     " + scoreManager.getScore(),             itemsLeftX, startY);
    g.drawString("Crystals:        " + level.getCollectedCrystals(),        itemsLeftX, startY + 37);
    g.drawString("Gems:            " + scoreManager.getGemsCollected(),     itemsLeftX, startY + 74);
    g.drawString("Time Bonus:      " + (gameTimer.getTimeRemaining() * 10), itemsLeftX, startY + 111);

    g.setFont(FONT_OVR_SML);
    g.setColor(PixelArtRenderer.NEON_CYAN);
    FontMetrics fmSmall = g.getFontMetrics();

    if (levelNumber < 3) {
        String prompt = "Press ENTER for next level";
        int px = cx - fmSmall.stringWidth(prompt) / 2;
        int py = panelY + panelH - 30;
        g.drawString(prompt, px, py);
    } else {
        String p1 = "ALL LEVELS COMPLETE!  Well done!";
        String p2 = "Press ENTER to return to menu";

        int p1x = cx - fmSmall.stringWidth(p1) / 2;
        int p2x = cx - fmSmall.stringWidth(p2) / 2;

        int p1y = panelY + panelH - 55;
        int p2y = panelY + panelH - 30;

        g.drawString(p1, p1x, p1y);
        g.drawString(p2, p2x, p2y);
    }
}

    /** Game over overlay with the reason and final score. */
 private void drawGameOverOverlay(Graphics2D g) {
    int cx = getWidth() / 2, cy = getHeight() / 2;
    g.setColor(COLOR_OVERLAY_DIM);
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(COLOR_PANEL_RED);
    g.fillRoundRect(cx - 220, cy - 140, 440, 280, 22, 22);
    g.setColor(PixelArtRenderer.NEON_PINK);
    g.setStroke(STROKE_2_5);
    g.drawRoundRect(cx - 220, cy - 140, 440, 280, 22, 22);
    g.setStroke(STROKE_1);

    g.setFont(FONT_GAMEOVER);
    FontMetrics fm = g.getFontMetrics();
    String title = "GAME OVER";
    g.drawString(title, cx - fm.stringWidth(title) / 2, cy - 70);
    g.setFont(FONT_GO_SML);
    g.setColor(Color.WHITE);
    fm = g.getFontMetrics();
    g.drawString(gameOverReason, cx - fm.stringWidth(gameOverReason) / 2, cy - 22);
    String scoreText = "Final Score:  " + scoreManager.getScore();
    g.drawString(scoreText, cx - fm.stringWidth(scoreText) / 2, cy + 22);
    g.setFont(FONT_OVR_SML);
    g.setColor(PixelArtRenderer.NEON_CYAN);
    fm = g.getFontMetrics();
    String hint = "Press ENTER to return to menu";
    g.drawString(hint, cx - fm.stringWidth(hint) / 2, cy + 110);
}

    // ── Same-package hooks for automated tests (coverage of large private paths) ──

    void triggerTestGameOver(String reason) {
        triggerGameOver(reason);
    }

    void triggerTestLevelComplete() {
        triggerLevelComplete();
    }

    void enterTestPausedState() {
        pauseGame();
    }

    /**
     * Live level instance while this panel is active — for integration tests in {@code game.ui}
     * (same package; not part of the public game API).
     */
    Level getRunningLevel() {
        return level;
    }

    /**
     * Live player instance wired to that level — for integration tests in {@code game.ui}.
     */
    Player getRunningPlayer() {
        return player;
    }

    /** Current high-level game state (menu overlay / playing / etc.) — for integration tests in {@code game.ui}. */
    GameState getRunningGameState() {
        return gameState;
    }

    /** Stops the game loop and animation timer (called when leaving the panel). */
    public void stopGame() {
        if (gameLoop       != null) gameLoop.stop();
        if (animationTimer != null) animationTimer.stop();
    }
}
