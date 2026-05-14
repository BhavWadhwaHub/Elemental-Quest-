package game.character;

import game.enemy.ShadowSentinel;
import game.level.Board;
import game.level.Cell;
import game.util.Direction;
import game.util.ElementType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extra rules for {@link Ember}, {@link Aqua}, and shared {@link ElementalCharacter} behaviour.
 */
class EmberAquaRulesTest {

    /** Verifies {@link Ember} may enter lava cells but not water. */
    @Test
    void ember_canWalkOnLava_notWater() {
        Cell lava = new Cell(0, 0);
        lava.setLava(true);
        Cell water = new Cell(0, 0);
        water.setWater(true);
        Ember ember = new Ember(0, 0);
        assertTrue(ember.canInteractWith(lava));
        assertFalse(ember.canInteractWith(water));
    }

    /** Verifies {@link Aqua} may enter water cells but not lava. */
    @Test
    void aqua_canWalkOnWater_notLava() {
        Cell lava = new Cell(0, 0);
        lava.setLava(true);
        Cell water = new Cell(0, 0);
        water.setWater(true);
        Aqua aqua = new Aqua(0, 0);
        assertTrue(aqua.canInteractWith(water));
        assertFalse(aqua.canInteractWith(lava));
    }

    /** Verifies trap and opposite-element terrain count as hazard damage for each character. */
    @Test
    void takesHazardDamage_trapAndOppositeElement() {
        Cell trap = new Cell(0, 0);
        trap.setTrap(true);
        Ember ember = new Ember(0, 0);
        Aqua aqua = new Aqua(0, 0);
        assertTrue(ember.takesHazardDamage(trap));
        assertTrue(aqua.takesHazardDamage(trap));

        Cell water = new Cell(0, 0);
        water.setWater(true);
        assertTrue(ember.takesHazardDamage(water));

        Cell lava = new Cell(0, 0);
        lava.setLava(true);
        assertTrue(aqua.takesHazardDamage(lava));
    }

    /** Verifies {@link Ember#move(Board, Direction)} returns false into a wall. */
    @Test
    void move_failsIntoWall() {
        Board board = new Board(3, 3);
        board.setWall(2, 1);
        Ember ember = new Ember(1, 1);
        assertFalse(ember.move(board, Direction.RIGHT));
        assertEquals(1, ember.getX());
    }

    /** Verifies Ember ability removes water ahead and spends one charge. */
    @Test
    void ember_useAbility_evaporatesWaterAhead() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        board.getCell(2, 3).setWater(true);
        int charges = ember.getAbilityCharges();
        ember.useAbility(board, Direction.DOWN);
        assertFalse(board.getCell(2, 3).isWater());
        assertEquals(charges - 1, ember.getAbilityCharges());
    }

    /** Verifies Ember ability places lava on an empty tile ahead. */
    @Test
    void ember_useAbility_placesLavaOnEmptyTile() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        ember.useAbility(board, Direction.DOWN);
        assertTrue(board.getCell(2, 3).isLava());
    }

    /** Verifies Aqua ability removes lava ahead and spends one charge. */
    @Test
    void aqua_useAbility_quenchesLavaAhead() {
        Board board = new Board(5, 5);
        Aqua aqua = new Aqua(2, 2);
        board.getCell(2, 3).setLava(true);
        int charges = aqua.getAbilityCharges();
        aqua.useAbility(board, Direction.DOWN);
        assertFalse(board.getCell(2, 3).isLava());
        assertEquals(charges - 1, aqua.getAbilityCharges());
    }

    /** Verifies Aqua ability floods an empty tile ahead with water. */
    @Test
    void aqua_useAbility_floodsEmptyTile() {
        Board board = new Board(5, 5);
        Aqua aqua = new Aqua(2, 2);
        aqua.useAbility(board, Direction.DOWN);
        assertTrue(board.getCell(2, 3).isWater());
    }

    /** Verifies abilities with zero charges do not alter the board or regain charges. */
    @Test
    void useAbility_withNoCharges_doesNothing() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        board.getCell(2, 3).setTrap(true);
        ember.useAbility(board, Direction.DOWN);
        board.getCell(3, 2).setTrap(true);
        ember.useAbility(board, Direction.RIGHT);
        board.getCell(2, 1).setTrap(true);
        ember.useAbility(board, Direction.UP);
        assertEquals(0, ember.getAbilityCharges());

        Cell leftCell = board.getCell(1, 2);
        boolean unchanged = leftCell.isEmpty();
        ember.useAbility(board, Direction.LEFT);
        assertEquals(0, ember.getAbilityCharges());
        assertEquals(unchanged, leftCell.isEmpty());
    }

    /** Verifies {@link Ember#rechargeAbility()} increments charges up to the maximum only. */
    @Test
    void rechargeAbility_increasesUpToMax() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        board.getCell(2, 3).setTrap(true);
        ember.useAbility(board, Direction.DOWN);
        board.getCell(3, 2).setTrap(true);
        ember.useAbility(board, Direction.RIGHT);
        board.getCell(2, 1).setTrap(true);
        ember.useAbility(board, Direction.UP);
        assertEquals(0, ember.getAbilityCharges());

        ember.rechargeAbility();
        assertEquals(1, ember.getAbilityCharges());
        ember.rechargeAbility();
        ember.rechargeAbility();
        assertEquals(3, ember.getAbilityCharges());
        ember.rechargeAbility();
        assertEquals(3, ember.getAbilityCharges());
    }

    /** Verifies element types and string type ids for Ember and Aqua. */
    @Test
    void elementalType_and_getType() {
        Ember ember = new Ember(0, 0);
        Aqua aqua = new Aqua(1, 1);
        assertEquals(ElementType.FIRE, ember.getElementType());
        assertEquals(ElementType.WATER, aqua.getElementType());
        assertEquals("EMBER", ember.getType());
        assertEquals("AQUA", aqua.getType());
    }

    /** Verifies Ember ability clears a trap ahead and spends a charge. */
    @Test
    void ember_useAbility_destroysTrapAhead() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        board.getCell(2, 3).setTrap(true);
        int c = ember.getAbilityCharges();
        ember.useAbility(board, Direction.DOWN);
        assertFalse(board.getCell(2, 3).isTrap());
        assertEquals(c - 1, ember.getAbilityCharges());
    }

    /** Verifies Aqua ability clears a trap ahead and spends a charge. */
    @Test
    void aqua_useAbility_destroysTrapAhead() {
        Board board = new Board(5, 5);
        Aqua aqua = new Aqua(2, 2);
        board.getCell(2, 3).setTrap(true);
        int c = aqua.getAbilityCharges();
        aqua.useAbility(board, Direction.DOWN);
        assertFalse(board.getCell(2, 3).isTrap());
        assertEquals(c - 1, aqua.getAbilityCharges());
    }

    /** Verifies Ember ability into a wall does not consume a charge. */
    @Test
    void ember_useAbility_wallAhead_doesNotSpendCharge() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        board.getCell(2, 3).setWall(true);
        int c = ember.getAbilityCharges();
        ember.useAbility(board, Direction.DOWN);
        assertEquals(c, ember.getAbilityCharges());
    }

    /** Verifies Aqua ability into a wall does not consume a charge. */
    @Test
    void aqua_useAbility_wallAhead_doesNotSpendCharge() {
        Board board = new Board(5, 5);
        Aqua aqua = new Aqua(2, 2);
        board.getCell(2, 3).setWall(true);
        int c = aqua.getAbilityCharges();
        aqua.useAbility(board, Direction.DOWN);
        assertEquals(c, aqua.getAbilityCharges());
    }

    /** Verifies Ember ability toward lava does not change the tile or spend a charge. */
    @Test
    void ember_useAbility_lavaTileAhead_noAbilityEffect() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        board.getCell(2, 3).setLava(true);
        int c = ember.getAbilityCharges();
        ember.useAbility(board, Direction.DOWN);
        assertTrue(board.getCell(2, 3).isLava());
        assertEquals(c, ember.getAbilityCharges());
    }

    /** Verifies Aqua ability toward water does not change the tile or spend a charge. */
    @Test
    void aqua_useAbility_waterTileAhead_noAbilityEffect() {
        Board board = new Board(5, 5);
        Aqua aqua = new Aqua(2, 2);
        board.getCell(2, 3).setWater(true);
        int c = aqua.getAbilityCharges();
        aqua.useAbility(board, Direction.DOWN);
        assertTrue(board.getCell(2, 3).isWater());
        assertEquals(c, aqua.getAbilityCharges());
    }

    /** Verifies Aqua ability removes trap before lava when both are on the target cell. */
    @Test
    void aqua_useAbility_trapTakesPrecedenceOverLava() {
        Board board = new Board(5, 5);
        Aqua aqua = new Aqua(2, 2);
        Cell t = board.getCell(2, 3);
        t.setLava(true);
        t.setTrap(true);
        int c = aqua.getAbilityCharges();
        aqua.useAbility(board, Direction.DOWN);
        assertFalse(t.isTrap());
        assertTrue(t.isLava());
        assertEquals(c - 1, aqua.getAbilityCharges());
    }

    /** Verifies Ember ability on a cell with {@link ShadowSentinel} still applies lava. */
    @Test
    void ember_useAbility_sentinelOnTile_placesLava() {
        Board board = new Board(5, 5);
        Ember ember = new Ember(2, 2);
        ShadowSentinel s = new ShadowSentinel(2, 3);
        board.getCell(2, 3).setEntity(s);
        ember.useAbility(board, Direction.DOWN);
        assertTrue(board.getCell(2, 3).isLava());
    }

    /** Verifies Aqua ability on a cell with {@link ShadowSentinel} still applies water. */
    @Test
    void aqua_useAbility_sentinelOnTile_placesWater() {
        Board board = new Board(5, 5);
        Aqua aqua = new Aqua(2, 2);
        ShadowSentinel s = new ShadowSentinel(2, 3);
        board.getCell(2, 3).setEntity(s);
        aqua.useAbility(board, Direction.DOWN);
        assertTrue(board.getCell(2, 3).isWater());
    }

    /** Verifies ability toward out-of-bounds target does not spend a charge. */
    @Test
    void useAbility_targetOutOfBounds_noOp() {
        Board board = new Board(3, 3);
        Ember ember = new Ember(1, 0);
        int c = ember.getAbilityCharges();
        ember.useAbility(board, Direction.UP);
        assertEquals(c, ember.getAbilityCharges());
    }

    /** Verifies {@link Ember#toggleFireTrail()} toggles fire-trail state twice. */
    @Test
    void ember_toggleFireTrail_and_query() {
        Ember ember = new Ember(0, 0);
        assertFalse(ember.isFireTrailActive());
        ember.toggleFireTrail();
        assertTrue(ember.isFireTrailActive());
        ember.toggleFireTrail();
        assertFalse(ember.isFireTrailActive());
    }

    /** Verifies {@link Aqua#toggleWaterShield()} toggles water-shield state twice. */
    @Test
    void aqua_toggleWaterShield_and_query() {
        Aqua aqua = new Aqua(0, 0);
        assertFalse(aqua.isWaterShieldActive());
        aqua.toggleWaterShield();
        assertTrue(aqua.isWaterShieldActive());
        aqua.toggleWaterShield();
        assertFalse(aqua.isWaterShieldActive());
    }

    /** Verifies {@link Ember#setActive(boolean)} and {@link Ember#isActive()} round-trip. */
    @Test
    void setActive_isActive_roundTrip() {
        Ember ember = new Ember(0, 0);
        assertFalse(ember.isActive());
        ember.setActive(true);
        assertTrue(ember.isActive());
        ember.setActive(false);
        assertFalse(ember.isActive());
    }

    /** Verifies recharge when already at max charges leaves count unchanged. */
    @Test
    void rechargeAbility_whenAlreadyFull_staysAtMax() {
        Ember ember = new Ember(0, 0);
        ember.rechargeAbility();
        assertEquals(3, ember.getAbilityCharges());
    }
}
