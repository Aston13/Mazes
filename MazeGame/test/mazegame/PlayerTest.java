package mazegame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    @DisplayName("Player initialises with correct position and size")
    void playerInitialisation() {
        Player p = new Player(100, 200, 50);

        assertEquals(100, p.getX());
        assertEquals(200, p.getY());
        assertEquals(50, p.getSize());
    }

    @Test
    @DisplayName("setX and setY update individual coordinates")
    void setIndividualCoordinates() {
        Player p = new Player(0, 0, 50);

        p.setX(42);
        assertEquals(42, p.getX());
        assertEquals(0, p.getY());

        p.setY(99);
        assertEquals(42, p.getX());
        assertEquals(99, p.getY());
    }

    @Test
    @DisplayName("setXY updates both coordinates at once")
    void setXYUpdatesBoth() {
        Player p = new Player(0, 0, 50);
        p.setXY(10, 20);

        assertEquals(10, p.getX());
        assertEquals(20, p.getY());
    }

    @Test
    @DisplayName("Movement flags default to false")
    void movementDefaultsFalse() {
        Player p = new Player(0, 0, 50);

        assertFalse(p.getMoveN());
        assertFalse(p.getMoveE());
        assertFalse(p.getMoveS());
        assertFalse(p.getMoveW());
    }

    @Test
    @DisplayName("Movement flags can be toggled independently")
    void movementToggle() {
        Player p = new Player(0, 0, 50);

        p.setMoveN(true);
        assertTrue(p.getMoveN());
        assertFalse(p.getMoveE());

        p.setMoveE(true);
        p.setMoveS(true);
        p.setMoveW(true);
        assertTrue(p.getMoveN());
        assertTrue(p.getMoveE());
        assertTrue(p.getMoveS());
        assertTrue(p.getMoveW());

        p.setMoveN(false);
        assertFalse(p.getMoveN());
        assertTrue(p.getMoveE());
    }

    @Test
    @DisplayName("Size is immutable after construction")
    void sizeImmutable() {
        Player p = new Player(0, 0, 100);
        assertEquals(100, p.getSize());
        // No setSize method exists — size is final
    }
}
