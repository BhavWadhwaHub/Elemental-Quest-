package game.integration;

import game.character.Player;
import game.collectible.BonusCrystal;
import game.collectible.ElementalCrystal;
import game.combat.Projectile;
import game.core.ScoreManager;
import game.enemy.ShadowSentinel;
import game.gate.FireGate;
import game.gate.WaterGate;
import game.level.Board;
import game.level.Level;
import game.util.Direction;
import game.util.ElementType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests crossing domain layers: {@link Board}, collectibles, {@link Projectile},
 * {@link ScoreManager}, gates, {@link Level}, and {@link Player}.
 */
class DomainCombinationsIntegrationTest {

    /** Move onto an elemental crystal, collect it, remove from board, and increment level crystal counts. */
    @Test
    void elementalCrystalCollectedAndLevelCrystalCount() {
        Board board = new Board(6, 6);
        ElementalCrystal crystal = new ElementalCrystal(2, 2, ElementType.FIRE);
        board.addCollectible(crystal);
        Player player = new Player(2, 1, 4, 4);

        assertTrue(player.move(board, Direction.DOWN));
        assertEquals(2, player.getEmber().getX());
        assertEquals(2, player.getEmber().getY());

        crystal.collect(player);
        assertTrue(crystal.isCollected());
        board.removeCollectible(crystal);

        Level level = new Level(1);
        level.collectCrystal();
        assertEquals(1, level.getCollectedCrystals());
    }

    /** Bonus gem collection and removal leaves the board collectible list empty. */
    @Test
    void bonusCrystalCollectedAndRemovedFromBoard() {
        Board board = new Board(5, 5);
        BonusCrystal gem = new BonusCrystal(1, 1);
        board.addCollectible(gem);
        Player player = new Player(1, 0, 3, 3);

        assertTrue(player.move(board, Direction.DOWN));
        gem.collect(player);
        board.removeCollectible(gem);

        assertTrue(board.getCollectibles().isEmpty());
    }

    /** Fire projectile stops when it hits a wall tile. */
    @Test
    void fireProjectileInactiveAfterHittingWall() {
        Board board = new Board(10, 10);
        board.setWall(5, 0);
        Projectile projectile = new Projectile(0, 0, Direction.RIGHT, ElementType.FIRE);
        int guard = 0;
        while (projectile.isActive() && guard++ < 50) {
            if (!projectile.tick()) {
                continue;
            }
            projectile.advance();
            if (!board.isValidPosition(projectile.getX(), projectile.getY())) {
                projectile.deactivate();
                break;
            }
            if (board.getCell(projectile.getX(), projectile.getY()).isWall()) {
                projectile.deactivate();
                break;
            }
        }
        assertFalse(projectile.isActive());
    }

    /** Water projectile becomes inactive after leaving valid board bounds. */
    @Test
    void waterProjectileInactiveAfterLeavingBoard() {
        Board board = new Board(4, 4);
        Projectile projectile = new Projectile(0, 0, Direction.LEFT, ElementType.WATER);
        int guard = 0;
        while (projectile.isActive() && guard++ < 40) {
            if (!projectile.tick()) {
                continue;
            }
            projectile.advance();
            if (!board.isValidPosition(projectile.getX(), projectile.getY())) {
                projectile.deactivate();
                break;
            }
        }
        assertFalse(projectile.isActive());
    }

    /** After many {@link Board#updateEnemies()} ticks, at least one sentinel changes position. */
    @Test
    void twoSentinelsMoveWhenBoardUpdatesEnemies() {
        Board board = new Board(20, 20);
        ShadowSentinel one = new ShadowSentinel(5, 5);
        ShadowSentinel two = new ShadowSentinel(10, 10);
        board.addEnemy(one);
        board.addEnemy(two);

        int x1 = one.getX();
        int y1 = one.getY();
        int x2 = two.getX();
        int y2 = two.getY();

        for (int i = 0; i < 30; i++) {
            board.updateEnemies();
        }

        boolean oneMoved = one.getX() != x1 || one.getY() != y1;
        boolean twoMoved = two.getX() != x2 || two.getY() != y2;
        assertTrue(oneMoved || twoMoved);
    }

    /** Two-player level: both gates unlocked and player standing on both sets completion flags. */
    @Test
    void twoPlayerLevelBothCharactersAtUnlockedGates() {
        Level level = new Level(1);
        Board board = level.getBoard();
        FireGate fireGate = board.getFireGate();
        WaterGate waterGate = board.getWaterGate();
        assertNotNull(fireGate);
        assertNotNull(waterGate);

        fireGate.unlock();
        waterGate.unlock();

        Player player = new Player(
            fireGate.getX(), fireGate.getY(),
            waterGate.getX(), waterGate.getY());
        fireGate.interact(player);
        waterGate.interact(player);

        assertTrue(player.isEmberAtGate());
        assertTrue(player.isAquaAtGate());
        assertTrue(player.bothAtGates());
    }

    /** Single-player Ember level: fire gate interaction sets only Ember-at-gate. */
    @Test
    void singleEmberLevelFireGateInteraction() {
        Level level = new Level(1, "EMBER");
        Board board = level.getBoard();
        FireGate fireGate = board.getFireGate();
        assertNotNull(fireGate);
        fireGate.unlock();

        Player player = new Player(fireGate.getX(), fireGate.getY(), 1, 2);
        fireGate.interact(player);

        assertTrue(player.isEmberAtGate());
        assertFalse(player.isAquaAtGate());
    }

    /** Single-player Aqua level: water gate interaction sets only Aqua-at-gate. */
    @Test
    void singleAquaLevelWaterGateInteraction() {
        Level level = new Level(1, "AQUA");
        Board board = level.getBoard();
        WaterGate waterGate = board.getWaterGate();
        assertNotNull(waterGate);
        waterGate.unlock();

        Player player = new Player(1, 2, waterGate.getX(), waterGate.getY());
        waterGate.interact(player);

        assertTrue(player.isAquaAtGate());
        assertFalse(player.isEmberAtGate());
    }

    /** Chained scoring events produce expected current score, gem count, and peak high score. */
    @Test
    void scoreManagerAccumulatesAndTracksHighScore() {
        ScoreManager scores = new ScoreManager();
        scores.addCrystalPoints(100);
        scores.addGemPoints(50);
        scores.addKillPoints(25);
        scores.addTrapPoints(25);
        scores.addTimeBonus(2);
        scores.deductPoints(20);

        assertEquals(1, scores.getGemsCollected());
        assertEquals(200, scores.getScore());
        int best = scores.getHighScore();
        assertEquals(220, best);
    }
}
