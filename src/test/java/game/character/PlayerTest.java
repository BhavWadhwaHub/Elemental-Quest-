package game.character;

import game.level.Board;
import game.level.Cell;
import game.util.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link Player} character switching, abilities, damage, gates, reset, and life accessors.
 */
class PlayerTest {

    private Player player;
    private Board board; 

    @BeforeEach
    void setUp() {
        player = new Player(0, 0, 1, 1);
        board = new Board(5, 5);
    }

    /** Verifies {@link Player#switchCharacter()} toggles active {@link ElementalCharacter}. */
    @Test
    void switchCharacter_togglesActiveCharacter() {
        boolean emberActiveInitially = player.getActiveCharacter() instanceof Ember;
        player.switchCharacter();
        boolean emberActiveAfterSwitch = player.getActiveCharacter() instanceof Ember;
        assertNotEquals(emberActiveInitially, emberActiveAfterSwitch);
    }

    /** Verifies Ember {@link Player#useAbility(Board, Direction)} clears trap ahead and spends charge. */
    @Test
    void useAbility_asEmber_clearsTrapInFront() {
        Player p = new Player(2, 2, 0, 0);
        Board b = new Board(5, 5);
        Cell target = b.getCell(2, 3);
        assertNotNull(target);
        target.setTrap(true);

        int chargesBefore = p.getEmber().getAbilityCharges();
        p.useAbility(b, Direction.DOWN);

        assertFalse(target.isTrap());
        assertEquals(chargesBefore - 1, p.getEmber().getAbilityCharges());
    }

    /** Verifies Aqua ability removes lava in front when Aqua is active. */
    @Test
    void useAbility_asAqua_quenchesLavaInFront() {
        Player p = new Player(0, 0, 2, 2);
        Board b = new Board(5, 5);
        p.switchCharacter();

        Cell target = b.getCell(2, 3);
        assertNotNull(target);
        target.setLava(true);

        int chargesBefore = p.getAqua().getAbilityCharges();
        p.useAbility(b, Direction.DOWN);

        assertFalse(target.isLava());
        assertEquals(chargesBefore - 1, p.getAqua().getAbilityCharges());
    }

    /** Verifies ability toward missing off-board cell does not consume Ember charge. */
    @Test
    void useAbility_doesNotConsumeChargeWhenTargetCellMissing() {
        Player p = new Player(0, 0, 1, 1);
        Board b = new Board(3, 3);
        int chargesBefore = p.getEmber().getAbilityCharges();
        p.useAbility(b, Direction.UP);
        assertEquals(chargesBefore, p.getEmber().getAbilityCharges());
    }

    /** Verifies {@link Player#takeDamage()} decrements active character lives. */
    @Test
    void takeDamage_reducesActiveCharacterLives() {
        int livesBefore = player.getLives();
        player.takeDamage();
        assertEquals(livesBefore - 1, player.getLives());
    }

    /** Verifies {@link Player#takeEmberDamage()} decrements Ember pool. */
    @Test
    void takeEmberDamage_reducesEmberLives() {
        int emberLives = player.getEmberLives();
        player.takeEmberDamage();
        assertEquals(emberLives - 1, player.getEmberLives());
    }

    /** Verifies {@link Player#takeAquaDamage()} decrements Aqua pool. */
    @Test
    void takeAquaDamage_reducesAquaLives() {
        int aquaLives = player.getAquaLives();
        player.takeAquaDamage();
        assertEquals(aquaLives - 1, player.getAquaLives());
    }

    /** Verifies Ember gate flag setters and queries round-trip. */
    @Test
    void setAndCheckEmberAtGate() {
        player.setEmberAtGate(true);
        assertTrue(player.isEmberAtGate());
        player.setEmberAtGate(false);
        assertFalse(player.isEmberAtGate());
    }

    /** Verifies Aqua gate flag setters and queries round-trip. */
    @Test
    void setAndCheckAquaAtGate() {
        player.setAquaAtGate(true);
        assertTrue(player.isAquaAtGate());
        player.setAquaAtGate(false);
        assertFalse(player.isAquaAtGate());
    }

    /** Verifies {@link Player#bothAtGates()} requires both characters at gates. */
    @Test
    void bothAtGates_returnsTrueOnlyWhenBothAtGate() {
        player.setEmberAtGate(true);
        player.setAquaAtGate(true);
        assertTrue(player.bothAtGates());

        player.setAquaAtGate(false);
        assertFalse(player.bothAtGates());
    }

    /** Verifies {@link Player#reset(int, int, int, int)} restores max lives and spawn positions. */
    @Test
    void reset_restoresPositionsAndLives() {
        player.takeEmberDamage();
        player.takeAquaDamage();
        player.reset(2, 2, 3, 3);

        assertEquals(player.getMaxLives(), player.getEmberLives());
        assertEquals(player.getMaxLives(), player.getAquaLives());
        assertEquals(2, player.getEmber().getX());
        assertEquals(2, player.getEmber().getY());
        assertEquals(3, player.getAqua().getX());
        assertEquals(3, player.getAqua().getY());
    }

    /** Verifies {@link Player#getActiveCharacter()} is Ember or Aqua. */
    @Test
    void getActiveCharacter_returnsCurrentCharacter() {
        ElementalCharacter active = player.getActiveCharacter();
        assertTrue(active instanceof Ember || active instanceof Aqua);
    }

    /** Verifies {@link Player#getLives()} matches the active character's pool. */
    @Test
    void getLives_matchesActiveCharacter() {
        boolean emberActive = player.getActiveCharacter() instanceof Ember;
        int lives = player.getLives();
        if (emberActive) {
            assertEquals(player.getEmberLives(), lives);
        } else {
            assertEquals(player.getAquaLives(), lives);
        }
    }

    /** Verifies {@link Player#getMaxLives()} matches constructed default. */
    @Test
    void getMaxLives_returnsStartingValue() {
        assertEquals(5, player.getMaxLives());
    }

    /** Verifies {@link Player#getEmber()} and {@link Player#getAqua()} are non-null. */
    @Test
    void getEmberAndAqua_returnNonNull() {
        assertNotNull(player.getEmber());
        assertNotNull(player.getAqua());
    }
}
