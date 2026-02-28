package mazegame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;

import static org.junit.jupiter.api.Assertions.*;

class TileTest {

    @Test
    @DisplayName("TilePassage returns correct bounds")
    void tilePassageBounds() {
        TilePassage tp = new TilePassage(100, 50, 75);

        assertEquals(50, tp.getMinX());
        assertEquals(75, tp.getMinY());
        assertEquals(150, tp.getMaxX());
        assertEquals(175, tp.getMaxY());
        assertEquals(100, tp.getSize());
    }

    @Test
    @DisplayName("TilePassage item toggle changes imageString")
    void tilePassageItemToggle() {
        TilePassage tp = new TilePassage(100, 0, 0);

        assertEquals("Passage", tp.getImageString());
        assertFalse(tp.hasItem());

        tp.setItem(true);
        assertEquals("Key", tp.getImageString());
        assertTrue(tp.hasItem());

        tp.setItem(false);
        assertEquals("Passage", tp.getImageString());
        assertFalse(tp.hasItem());
    }

    @Test
    @DisplayName("TileWall default neighbours are 0000")
    void tileWallDefaultNeighbours() {
        TileWall tw = new TileWall(100, 0, 0);
        assertEquals("0000", tw.getPassageNeighbours());
    }

    @Test
    @DisplayName("TileWall imageString includes neighbour bits")
    void tileWallImageString() {
        TileWall tw = new TileWall(100, 0, 0);
        tw.setPassageNeighbours("1010");
        assertEquals("wall_1010", tw.getImageString());
    }

    @Test
    @DisplayName("TileExit accessibility toggle changes imageString")
    void tileExitAccessibility() {
        TileExit te = new TileExit(100, 0, 0);

        assertFalse(te.getAccessible());
        assertEquals("Locked Exit", te.getImageString());

        te.setAccessible(true);
        assertTrue(te.getAccessible());
        assertEquals("Open Exit", te.getImageString());
    }

    @Test
    @DisplayName("Tile position can be updated")
    void tilePositionUpdate() {
        TilePassage tp = new TilePassage(100, 10, 20);
        tp.setMinX(30);
        tp.setMinY(40);

        assertEquals(30, tp.getMinX());
        assertEquals(40, tp.getMinY());
        assertEquals(130, tp.getMaxX());
        assertEquals(140, tp.getMaxY());
    }
}
