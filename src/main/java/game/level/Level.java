package game.level;

import game.collectible.BonusCrystal;
import game.collectible.ElementalCrystal;
import game.enemy.*;
import game.gate.FireGate;
import game.gate.WaterGate;
import game.util.ElementType;

/**
 * Represents a single game level — board layout, crystal requirements,
 * time limit, and player start positions.
 *
 * Supports both two-player mode (mixed crystals) and
 * single-player mode for either Ember or Aqua.
 */
public class Level {

    private Board board;
    private int requiredCrystals;
    private int collectedCrystals;
    private boolean isCompleted;
    private int levelNumber;
    private int timeLimit;
    private int[] emberStart;
    private int[] aquaStart;

    /** Creates a two-player level. */
    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        this.collectedCrystals = 0;
        this.isCompleted = false;
        initializeTwoPlayerLevel();
    }

    /** Creates a single-player level for the given character ("EMBER" or "AQUA"). */
    public Level(int levelNumber, String character) {
        this.levelNumber = levelNumber;
        this.collectedCrystals = 0;
        this.isCompleted = false;
        if ("AQUA".equals(character)) {
            initializeAquaLevel();
        } else {
            initializeEmberLevel();
        }
    }

    private void initializeTwoPlayerLevel() {
        switch (levelNumber) {
            case 1:  create2PLevel1(); break;
            case 2:  create2PLevel2(); break;
            case 3:  create2PLevel3(); break;
            default: create2PLevel1(); break;
        }
    }

    // ── Two-Player Levels ─────────────────────────────────────────────────────

    /** 2P Level 1 — Divided Cavern (16×12). Collect 6 elemental crystals. */
    private void create2PLevel1() {
        board = new Board(16, 12);
        timeLimit = 120;
        requiredCrystals = 6;
        emberStart = new int[]{1, 1};
        aquaStart  = new int[]{1, 2};

        // Border walls
        for (int x = 0; x < 16; x++) { board.setWall(x, 0);  board.setWall(x, 11); }
        for (int y = 0; y < 12; y++) { board.setWall(0, y);  board.setWall(15, y); }
        // Inner dividers that split the map into two halves
        for (int y = 2; y < 6;  y++) board.setWall(5,  y);
        for (int y = 6; y < 10; y++) board.setWall(10, y);

        // Hazards
        board.setLava(3, 5);  board.setLava(4, 5);  board.setLava(3, 6);
        board.setWater(11, 3); board.setWater(12, 3); board.setWater(11, 4);
        board.setTrap(7, 5);  board.setTrap(8, 5);
        board.setTrap(6, 8);  board.setTrap(13, 4);

        // Fire crystals (Ember's targets)
        board.addCollectible(new ElementalCrystal(7,  2, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(3,  9, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(2,  7, ElementType.FIRE));
        // Water crystals (Aqua's targets)
        board.addCollectible(new ElementalCrystal(12, 8, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(8,  5, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(13, 6, ElementType.WATER));

        // Bonus crystals — either player can collect these
        board.addCollectible(new BonusCrystal(6, 3));
        board.addCollectible(new BonusCrystal(9, 7));

        board.setFireGate(new FireGate(14, 2));
        board.setWaterGate(new WaterGate(14, 9));
        board.addEnemy(new ShadowSentinel(8, 8, 1));
    }

    /**
     * 2P Level 2 — Cross Corridors (18×14).
     * Longer map with choke-point corridors and hazard zones.
     * Collect 8 elemental crystals.
     */
    private void create2PLevel2() {
        board = new Board(18, 14);
        timeLimit = 150;
        requiredCrystals = 8;
        emberStart = new int[]{1, 1};
        aquaStart  = new int[]{1, 12};

        for (int x = 0; x < 18; x++) { board.setWall(x, 0);  board.setWall(x, 13); }
        for (int y = 0; y < 14; y++) { board.setWall(0, y);  board.setWall(17, y); }
        for (int x = 3; x < 8;  x++) { board.setWall(x, 4);  board.setWall(x, 9); }
        for (int x = 10; x < 15; x++) { board.setWall(x, 4); board.setWall(x, 9); }
        for (int y = 6; y < 10; y++) { board.setWall(6, y);  board.setWall(11, y); }

        // Left zone: lava (dangerous for Aqua)
        for (int x = 2; x < 6; x++) { board.setLava(x, 6);   board.setLava(x, 7); }
        // Right zone: water (dangerous for Ember)
        for (int x = 12; x < 16; x++) { board.setWater(x, 6); board.setWater(x, 7); }
        board.setTrap(8, 3);  board.setTrap(9, 3);
        board.setTrap(8, 10); board.setTrap(9, 10);
        board.setTrap(1, 6);  board.setTrap(16, 6);

        board.addCollectible(new ElementalCrystal(4,  2,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(4,  11, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(8,  5,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(2,  5,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(13, 2,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(13, 11, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(9,  8,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(15, 5,  ElementType.WATER));

        board.addCollectible(new BonusCrystal(2,  2));
        board.addCollectible(new BonusCrystal(8, 11));

        board.setFireGate(new FireGate(16, 1));
        board.setWaterGate(new WaterGate(16, 12));
        board.addEnemy(new ShadowSentinel(8, 2,  1));
        board.addEnemy(new ShadowSentinel(9, 11, 1));
    }

    /**
     * 2P Level 3 — Elemental Arena (20×16).
     * Large open arena with corner hazard zones and a walled centre.
     * Collect 10 elemental crystals.
     */
    private void create2PLevel3() {
        board = new Board(20, 16);
        timeLimit = 180;
        requiredCrystals = 10;
        emberStart = new int[]{1, 7};
        aquaStart  = new int[]{1, 8};

        for (int x = 0; x < 20; x++) { board.setWall(x, 0);  board.setWall(x, 15); }
        for (int y = 0; y < 16; y++) { board.setWall(0, y);  board.setWall(19, y); }
        for (int y = 2; y < 7;  y++) { board.setWall(5, y);  board.setWall(14, y); }
        for (int y = 9; y < 14; y++) { board.setWall(5, y);  board.setWall(14, y); }
        for (int x = 7; x < 13; x++) { board.setWall(x, 5);  board.setWall(x, 10); }
        board.setWall(9, 7); board.setWall(10, 7);
        board.setWall(9, 8); board.setWall(10, 8);

        // Top-left: lava (Ember safe)   Top-right: water (Aqua safe)
        for (int x = 2; x < 5;  x++) for (int y = 2; y < 5;  y++) board.setLava(x, y);
        for (int x = 15; x < 18; x++) for (int y = 2; y < 5;  y++) board.setWater(x, y);
        // Bottom-left: water            Bottom-right: lava
        for (int x = 2; x < 5;  x++) for (int y = 11; y < 14; y++) board.setWater(x, y);
        for (int x = 15; x < 18; x++) for (int y = 11; y < 14; y++) board.setLava(x, y);
        board.setTrap(6,  3); board.setTrap(6,  4);
        board.setTrap(13, 3); board.setTrap(13, 4);
        board.setTrap(6,  11); board.setTrap(6,  12);
        board.setTrap(13, 11); board.setTrap(13, 12);
        board.setTrap(11, 7); board.setTrap(8, 8);

        board.addCollectible(new ElementalCrystal(3,  3,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(16, 12, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(7,  7,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(9,  2,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(2,  6,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(16, 3,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(3,  12, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(12, 8,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(10, 13, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(17, 9,  ElementType.WATER));

        board.addCollectible(new BonusCrystal(6,  6));
        board.addCollectible(new BonusCrystal(13, 6));

        board.setFireGate(new FireGate(18, 7));
        board.setWaterGate(new WaterGate(18, 8));
        board.addEnemy(new ShadowSentinel(6,  7,  1));
        board.addEnemy(new ShadowSentinel(13, 8,  1));
        board.addEnemy(new ShadowSentinel(9,  12, 1));
    }

    // ── Ember Single-Player Levels ────────────────────────────────────────────
    // Only fire crystals appear; reaching the FireGate wins the level.
    // Lava is safe for Ember — water is dangerous.

    private void initializeEmberLevel() {
        switch (levelNumber) {
            case 1:  createEmberLevel1(); break;
            case 2:  createEmberLevel2(); break;
            case 3:  createEmberLevel3(); break;
            default: createEmberLevel1(); break;
        }
    }

    /**
     * Ember Level 1 — Volcanic Outpost (16×12).
     * Lava pools give Ember a safe route through water and spike traps.
     * Collect 5 fire crystals.
     */
    private void createEmberLevel1() {
        board = new Board(16, 12);
        timeLimit = 120;
        requiredCrystals = 5;
        emberStart = new int[]{1, 1};
        aquaStart  = new int[]{1, 2}; // not used in 1P, stored for consistency

        for (int x = 0; x < 16; x++) { board.setWall(x, 0);  board.setWall(x, 11); }
        for (int y = 0; y < 12; y++) { board.setWall(0, y);  board.setWall(15, y); }
        for (int y = 3; y < 7; y++) board.setWall(4, y);
        for (int y = 5; y < 9; y++) board.setWall(8, y);
        board.setWall(11, 3); board.setWall(11, 4); board.setWall(12, 3);

        // Lava — Ember can walk right through
        board.setLava(2, 5); board.setLava(3, 5); board.setLava(3, 6);
        board.setLava(5, 4); board.setLava(6, 4); board.setLava(6, 5);
        board.setLava(9, 2); board.setLava(10, 2);
        // Water — hurts Ember
        board.setWater(12, 7); board.setWater(13, 7); board.setWater(13, 8);
        board.setWater(6, 9);  board.setWater(7, 9);
        // Traps — hurt everyone
        board.setTrap(5, 8);  board.setTrap(10, 5);
        board.setTrap(13, 2); board.setTrap(2,  9);

        board.addCollectible(new ElementalCrystal(3,  3, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(7,  8, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(13, 5, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(9,  2, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(2,  8, ElementType.FIRE));

        board.addCollectible(new BonusCrystal(6,  2));
        board.addCollectible(new BonusCrystal(12, 9));

        board.setFireGate(new FireGate(14, 5));
        board.addEnemy(new ShadowSentinel(7, 6, 1));
    }

    /**
     * Ember Level 2 — Magma Forge (18×14).
     * Lava rivers act as shortcuts; water pools and spike corridors block the quick route.
     * Collect 7 fire crystals.
     */
    private void createEmberLevel2() {
        board = new Board(18, 14);
        timeLimit = 150;
        requiredCrystals = 7;
        emberStart = new int[]{1, 1};
        aquaStart  = new int[]{1, 2};

        for (int x = 0; x < 18; x++) { board.setWall(x, 0);  board.setWall(x, 13); }
        for (int y = 0; y < 14; y++) { board.setWall(0, y);  board.setWall(17, y); }
        for (int x = 4; x < 8;  x++) board.setWall(x, 4);
        for (int x = 10; x < 14; x++) board.setWall(x, 4);
        for (int y = 6; y < 10; y++) { board.setWall(6, y);  board.setWall(11, y); }
        board.setWall(8, 8); board.setWall(9, 8);

        // Lava rivers — free shortcuts for Ember
        for (int x = 1; x < 5; x++) board.setLava(x, 7);
        for (int y = 2; y < 4; y++) { board.setLava(8, y); board.setLava(9, y); }
        board.setLava(3, 10); board.setLava(4, 10); board.setLava(5, 10);
        // Water pools — avoid
        for (int x = 13; x < 17; x++) board.setWater(x, 7);
        board.setWater(12, 10); board.setWater(13, 10); board.setWater(14, 10);
        board.setWater(15, 2);  board.setWater(15, 3);
        board.setTrap(7, 6);  board.setTrap(10, 6);
        board.setTrap(8, 11); board.setTrap(9, 11);
        board.setTrap(1, 4);  board.setTrap(16, 4);

        board.addCollectible(new ElementalCrystal(2,  3,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(15, 5,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(8,  6,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(3,  11, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(14, 11, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(6,  3,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(9,  5,  ElementType.FIRE));

        board.addCollectible(new BonusCrystal(5,  2));
        board.addCollectible(new BonusCrystal(12, 2));

        board.setFireGate(new FireGate(16, 6));
        board.addEnemy(new ShadowSentinel(7,  2,  1));
        board.addEnemy(new ShadowSentinel(10, 11, 1));
    }

    /**
     * Ember Level 3 — Inferno Core (20×16).
     * Large map with a lava maze, water barriers, and spike gauntlets.
     * Collect 8 fire crystals.
     */
    private void createEmberLevel3() {
        board = new Board(20, 16);
        timeLimit = 180;
        requiredCrystals = 8;
        emberStart = new int[]{1, 1};
        aquaStart  = new int[]{1, 2};

        for (int x = 0; x < 20; x++) { board.setWall(x, 0);  board.setWall(x, 15); }
        for (int y = 0; y < 16; y++) { board.setWall(0, y);  board.setWall(19, y); }
        for (int y = 2; y < 7;  y++) { board.setWall(5, y);  board.setWall(14, y); }
        for (int y = 9; y < 14; y++) { board.setWall(5, y);  board.setWall(14, y); }
        for (int x = 7; x < 13; x++) { board.setWall(x, 5);  board.setWall(x, 10); }
        board.setWall(9, 7); board.setWall(10, 7);
        board.setWall(9, 8); board.setWall(10, 8);

        // Lava zones — top-left and bottom-right corners are Ember's playground
        for (int x = 2; x < 5;  x++) for (int y = 2;  y < 5;  y++) board.setLava(x, y);
        for (int x = 15; x < 18; x++) for (int y = 11; y < 14; y++) board.setLava(x, y);
        for (int x = 6; x < 8;  x++) { board.setLava(x, 7); board.setLava(x, 8); }
        // Water zones — dangerous for Ember
        for (int x = 15; x < 18; x++) for (int y = 2;  y < 5;  y++) board.setWater(x, y);
        for (int x = 2;  x < 5;  x++) for (int y = 11; y < 14; y++) board.setWater(x, y);
        for (int x = 12; x < 14; x++) { board.setWater(x, 7); board.setWater(x, 8); }
        board.setTrap(6,  3); board.setTrap(6,  4);
        board.setTrap(13, 3); board.setTrap(13, 4);
        board.setTrap(6,  11); board.setTrap(6,  12);
        board.setTrap(13, 11); board.setTrap(13, 12);
        board.setTrap(11, 7); board.setTrap(8,  8);

        board.addCollectible(new ElementalCrystal(3,  3,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(16, 3,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(3,  12, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(16, 12, ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(7,  7,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(12, 8,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(8,  2,  ElementType.FIRE));
        board.addCollectible(new ElementalCrystal(11, 13, ElementType.FIRE));

        board.addCollectible(new BonusCrystal(7,  2));
        board.addCollectible(new BonusCrystal(12, 2));

        board.setFireGate(new FireGate(18, 7));
        board.addEnemy(new ShadowSentinel(6,  7,  1));
        board.addEnemy(new ShadowSentinel(13, 8,  1));
        board.addEnemy(new ShadowSentinel(9,  13, 1));
    }

    // ── Aqua Single-Player Levels ─────────────────────────────────────────────
    // Only water crystals appear; reaching the WaterGate wins the level.
    // Water is safe for Aqua — lava is dangerous.

    private void initializeAquaLevel() {
        switch (levelNumber) {
            case 1:  createAquaLevel1(); break;
            case 2:  createAquaLevel2(); break;
            case 3:  createAquaLevel3(); break;
            default: createAquaLevel1(); break;
        }
    }

    /**
     * Aqua Level 1 — Tidal Cavern (16×12).
     * Water pools give Aqua a safe route through lava and spike traps.
     * Collect 5 water crystals.
     */
    private void createAquaLevel1() {
        board = new Board(16, 12);
        timeLimit = 120;
        requiredCrystals = 5;
        emberStart = new int[]{1, 2};
        aquaStart  = new int[]{1, 1};

        for (int x = 0; x < 16; x++) { board.setWall(x, 0);  board.setWall(x, 11); }
        for (int y = 0; y < 12; y++) { board.setWall(0, y);  board.setWall(15, y); }
        for (int y = 3; y < 7; y++) board.setWall(4, y);
        for (int y = 5; y < 9; y++) board.setWall(8, y);
        board.setWall(11, 7); board.setWall(11, 8); board.setWall(12, 7);

        // Water — Aqua can walk right through
        board.setWater(2, 5); board.setWater(3, 5); board.setWater(3, 6);
        board.setWater(5, 4); board.setWater(6, 4); board.setWater(6, 5);
        board.setWater(9, 2); board.setWater(10, 2);
        // Lava — hurts Aqua
        board.setLava(12, 4); board.setLava(13, 4); board.setLava(13, 5);
        board.setLava(6, 9);  board.setLava(7, 9);
        board.setTrap(5, 8);  board.setTrap(10, 5);
        board.setTrap(13, 2); board.setTrap(2,  9);

        board.addCollectible(new ElementalCrystal(3,  3,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(7,  8,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(13, 9,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(9,  2,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(2,  7,  ElementType.WATER));

        board.addCollectible(new BonusCrystal(6,  2));
        board.addCollectible(new BonusCrystal(12, 2));

        board.setWaterGate(new WaterGate(14, 5));
        board.addEnemy(new ShadowSentinel(7, 6, 1));
    }

    /**
     * Aqua Level 2 — Frozen Aqueduct (18×14).
     * Water channels act as shortcuts; lava and spike corridors block the direct path.
     * Collect 7 water crystals.
     */
    private void createAquaLevel2() {
        board = new Board(18, 14);
        timeLimit = 150;
        requiredCrystals = 7;
        emberStart = new int[]{1, 2};
        aquaStart  = new int[]{1, 1};

        for (int x = 0; x < 18; x++) { board.setWall(x, 0);  board.setWall(x, 13); }
        for (int y = 0; y < 14; y++) { board.setWall(0, y);  board.setWall(17, y); }
        for (int x = 4; x < 8;  x++) board.setWall(x, 4);
        for (int x = 10; x < 14; x++) board.setWall(x, 4);
        for (int y = 6; y < 10; y++) { board.setWall(6, y);  board.setWall(11, y); }
        board.setWall(8, 8); board.setWall(9, 8);

        // Water channels — free shortcuts for Aqua
        for (int x = 1; x < 5; x++) board.setWater(x, 7);
        for (int y = 2; y < 4; y++) { board.setWater(8, y); board.setWater(9, y); }
        board.setWater(3, 10); board.setWater(4, 10); board.setWater(5, 10);
        // Lava — avoid
        for (int x = 13; x < 17; x++) board.setLava(x, 7);
        board.setLava(12, 10); board.setLava(13, 10); board.setLava(14, 10);
        board.setLava(15, 2);  board.setLava(15, 3);
        board.setTrap(7, 6);  board.setTrap(10, 6);
        board.setTrap(8, 11); board.setTrap(9, 11);
        board.setTrap(1, 4);  board.setTrap(16, 4);

        board.addCollectible(new ElementalCrystal(2,  3,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(15, 5,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(8,  6,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(3,  11, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(14, 11, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(6,  3,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(9,  5,  ElementType.WATER));

        board.addCollectible(new BonusCrystal(5,  2));
        board.addCollectible(new BonusCrystal(12, 2));

        board.setWaterGate(new WaterGate(16, 6));
        board.addEnemy(new ShadowSentinel(7,  2,  1));
        board.addEnemy(new ShadowSentinel(10, 11, 1));
    }

    /**
     * Aqua Level 3 — Abyssal Depths (20×16).
     * Large map with a water maze, lava barriers, and spike gauntlets.
     * Collect 8 water crystals.
     */
    private void createAquaLevel3() {
        board = new Board(20, 16);
        timeLimit = 180;
        requiredCrystals = 8;
        emberStart = new int[]{1, 2};
        aquaStart  = new int[]{1, 1};

        for (int x = 0; x < 20; x++) { board.setWall(x, 0);  board.setWall(x, 15); }
        for (int y = 0; y < 16; y++) { board.setWall(0, y);  board.setWall(19, y); }
        for (int y = 2; y < 7;  y++) { board.setWall(5, y);  board.setWall(14, y); }
        for (int y = 9; y < 14; y++) { board.setWall(5, y);  board.setWall(14, y); }
        for (int x = 7; x < 13; x++) { board.setWall(x, 5);  board.setWall(x, 10); }
        board.setWall(9, 7); board.setWall(10, 7);
        board.setWall(9, 8); board.setWall(10, 8);

        // Water zones — Aqua's playground (top-left and bottom-right)
        for (int x = 2; x < 5;  x++) for (int y = 2;  y < 5;  y++) board.setWater(x, y);
        for (int x = 15; x < 18; x++) for (int y = 11; y < 14; y++) board.setWater(x, y);
        for (int x = 6; x < 8;  x++) { board.setWater(x, 7); board.setWater(x, 8); }
        // Lava zones — dangerous for Aqua
        for (int x = 15; x < 18; x++) for (int y = 2;  y < 5;  y++) board.setLava(x, y);
        for (int x = 2;  x < 5;  x++) for (int y = 11; y < 14; y++) board.setLava(x, y);
        for (int x = 12; x < 14; x++) { board.setLava(x, 7); board.setLava(x, 8); }
        board.setTrap(6,  3); board.setTrap(6,  4);
        board.setTrap(13, 3); board.setTrap(13, 4);
        board.setTrap(6,  11); board.setTrap(6,  12);
        board.setTrap(13, 11); board.setTrap(13, 12);
        board.setTrap(11, 7); board.setTrap(8,  8);

        board.addCollectible(new ElementalCrystal(3,  3,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(16, 3,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(3,  12, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(16, 12, ElementType.WATER));
        board.addCollectible(new ElementalCrystal(7,  7,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(12, 8,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(8,  2,  ElementType.WATER));
        board.addCollectible(new ElementalCrystal(11, 13, ElementType.WATER));

        board.addCollectible(new BonusCrystal(7,  2));
        board.addCollectible(new BonusCrystal(12, 2));

        board.setWaterGate(new WaterGate(18, 7));
        board.addEnemy(new ShadowSentinel(6,  7,  1));
        board.addEnemy(new ShadowSentinel(13, 8,  1));
        board.addEnemy(new ShadowSentinel(9,  13, 1));
    }

    // ── Getters & helpers ─────────────────────────────────────────────────────

    /** Returns true once all required crystals have been collected. */
    public boolean checkCompletion()    { return collectedCrystals >= requiredCrystals; }

    /** Called each time a player picks up an elemental crystal. */
    public void collectCrystal()        { collectedCrystals++; }

    /** How many crystals still need to be found. */
    public int getRemainingCrystals()   { return requiredCrystals - collectedCrystals; }

    /** Returns the board for this level — the caller can read and modify it directly. */
    public Board   getBoard()             { return board; }

    /** Total number of elemental crystals that must be collected to unlock the gates. */
    public int     getRequiredCrystals()  { return requiredCrystals; }

    /** How many elemental crystals have been picked up so far this run. */
    public int     getCollectedCrystals() { return collectedCrystals; }

    /** Returns {@code true} once the level has been flagged as complete by the game loop. */
    public boolean isCompleted()          { return isCompleted; }

    /** Manually marks the level complete (or un-marks it when restarting). */
    public void    setCompleted(boolean c){ isCompleted = c; }

    /** 1-based level number — used to pick the right layout and display the level index in the HUD. */
    public int     getLevelNumber()       { return levelNumber; }

    /** Time limit in seconds for this level. */
    public int     getTimeLimit()         { return timeLimit; }

    /** Ember's spawn tile as {@code [x, y]}. */
    public int[]   getEmberStart()        { return emberStart; }

    /** Aqua's spawn tile as {@code [x, y]} (may be unused in single-player Ember mode). */
    public int[]   getAquaStart()         { return aquaStart; }
}
