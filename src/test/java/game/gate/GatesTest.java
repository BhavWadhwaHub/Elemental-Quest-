package game.gate;

import game.character.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FireGate} and {@link WaterGate}: lock state, interact, positions, and types.
 */
class GatesTest {

    /** Fire gate unlocks, sets Ember-at-gate on interact, and reports position/type. */
    @Test
    void testFireGateUnlockAndInteract() {
        FireGate fireGate = new FireGate(2, 3);
        Player ember = new Player(0, 0, 1, 1); // dummy values

        // Initially locked
        assertFalse(fireGate.isUnlocked());

        // Unlock the gate
        fireGate.unlock();
        assertTrue(fireGate.isUnlocked());

        // Player interacts while unlocked
        fireGate.interact(ember);
        assertTrue(ember.isEmberAtGate());

        // Check position getters
        assertEquals(2, fireGate.getX());
        assertEquals(3, fireGate.getY());

        // Move gate and check again
        fireGate.setPosition(5, 6);
        assertEquals(5, fireGate.getX());
        assertEquals(6, fireGate.getY());

        // Type and element
        assertEquals("GATE_FIRE", fireGate.getType());
        assertEquals(fireGate.getElementType(), fireGate.getElementType());
    }

    /** Water gate unlocks, sets Aqua-at-gate on interact, and reports position/type. */
    @Test
    void testWaterGateUnlockAndInteract() {
        WaterGate waterGate = new WaterGate(4, 1);
        Player aqua = new Player(0, 0, 1, 1); // dummy values

        // Initially locked
        assertFalse(waterGate.isUnlocked());

        // Unlock the gate
        waterGate.unlock();
        assertTrue(waterGate.isUnlocked());

        // Player interacts while unlocked
        waterGate.interact(aqua);
        assertTrue(aqua.isAquaAtGate());

        // Check position getters
        assertEquals(4, waterGate.getX());
        assertEquals(1, waterGate.getY());

        // Move gate and check again
        waterGate.setPosition(7, 8);
        assertEquals(7, waterGate.getX());
        assertEquals(8, waterGate.getY());

        // Type and element
        assertEquals("GATE_WATER", waterGate.getType());
        assertEquals(waterGate.getElementType(), waterGate.getElementType());
    }

    /** Locked fire gate does not set Ember-at-gate when interacted. */
    @Test
    void fireGate_whenLocked_interactDoesNothing() {
        FireGate gate = new FireGate(1, 1);
        Player p = new Player(0, 0, 2, 2);
        assertFalse(gate.isUnlocked());
        gate.interact(p);
        assertFalse(p.isEmberAtGate());
    }

    /** Locked water gate does not set Aqua-at-gate when interacted. */
    @Test
    void waterGate_whenLocked_interactDoesNothing() {
        WaterGate gate = new WaterGate(1, 1);
        Player p = new Player(0, 0, 2, 2);
        assertFalse(gate.isUnlocked());
        gate.interact(p);
        assertFalse(p.isAquaAtGate());
    }
}
