package mazegame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class TilemapTest {

    @Test
    @DisplayName("Grid is correct size after construction")
    void gridSize() {
        int gridSize = 11;
        Tilemap tm = new Tilemap(100, 0, gridSize);
        Tile[][] tiles = tm.getTileArr();

        assertEquals(gridSize, tiles.length);
        assertEquals(gridSize, tiles[0].length);
    }

    @Test
    @DisplayName("All tiles are TileWall after initial population")
    void allTilesAreWalls() {
        Tilemap tm = new Tilemap(50, 0, 5);
        Tile[][] tiles = tm.getTileArr();

        for (Tile[] row : tiles) {
            for (Tile tile : row) {
                assertInstanceOf(TileWall.class, tile);
            }
        }
    }

    @Test
    @DisplayName("Tile positions are calculated correctly based on tileWH")
    void tilePositions() {
        int tileWH = 100;
        Tilemap tm = new Tilemap(tileWH, 0, 3);
        Tile[][] tiles = tm.getTileArr();

        // First tile at (0,0)
        assertEquals(0, tiles[0][0].getMinX());
        assertEquals(0, tiles[0][0].getMinY());

        // Second column: x offset by tileWH
        assertEquals(tileWH, tiles[0][1].getMinX());
        assertEquals(0, tiles[0][1].getMinY());

        // Second row: y offset by tileWH
        assertEquals(0, tiles[1][0].getMinX());
        assertEquals(tileWH, tiles[1][0].getMinY());

        // Diagonal (1,1)
        assertEquals(tileWH, tiles[1][1].getMinX());
        assertEquals(tileWH, tiles[1][1].getMinY());
    }

    @Test
    @DisplayName("getCurrentTile returns correct row and column")
    void getCurrentTile() {
        int tileWH = 100;
        Tilemap tm = new Tilemap(tileWH, 0, 5);

        // Centre of tile at (0,0)
        int[] result = tm.getCurrentTile(50, 50);
        assertNotNull(result);
        assertEquals(0, result[0]); // row
        assertEquals(0, result[1]); // col

        // Centre of tile at row=2, col=3
        result = tm.getCurrentTile(350, 250);
        assertNotNull(result);
        assertEquals(2, result[0]);
        assertEquals(3, result[1]);
    }

    @Test
    @DisplayName("getCurrentTile returns null for out-of-bounds coordinates")
    void getCurrentTileOutOfBounds() {
        Tilemap tm = new Tilemap(100, 0, 5);

        int[] result = tm.getCurrentTile(999, 999);
        assertNull(result);
    }

    @Test
    @DisplayName("getPassageCount returns 0 for all-wall grid")
    void passageCountAllWalls() {
        Tilemap tm = new Tilemap(100, 0, 5);
        Tile[][] tiles = tm.getTileArr();

        assertEquals(0, tm.getPassageCount(tiles));
    }

    @Test
    @DisplayName("getPassageCount counts only TilePassage instances")
    void passageCountMixed() {
        Tilemap tm = new Tilemap(100, 0, 3);
        Tile[][] tiles = tm.getTileArr();

        // Replace two tiles with passages
        tiles[0][0] = new TilePassage(100, 0, 0);
        tiles[1][1] = new TilePassage(100, 100, 100);

        assertEquals(2, tm.getPassageCount(tiles));
    }

    @Test
    @DisplayName("getTileWH returns the configured tile width/height")
    void getTileWH() {
        Tilemap tm = new Tilemap(75, 0, 5);
        assertEquals(75, tm.getTileWH());
    }

    @Test
    @DisplayName("Odd grid size is preserved")
    void oddGridSize() {
        // The maze generator requires odd grid sizes
        Tilemap tm = new Tilemap(100, 0, 11);
        Tile[][] tiles = tm.getTileArr();
        assertEquals(11, tiles.length);
    }
}
