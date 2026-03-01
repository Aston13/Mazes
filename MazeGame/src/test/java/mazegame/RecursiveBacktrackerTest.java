package mazegame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecursiveBacktrackerTest {

  @Test
  @DisplayName("Generated starting coordinate is odd and within valid range")
  void startingCoordIsOddAndInRange() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    int coord = rb.getRandomStartingCoord();

    assertTrue(coord % 2 != 0, "Starting coordinate should be odd");
    assertTrue(
        coord >= 1 && coord <= rowColAmount - 2,
        "Coordinate should be within valid maze range [1, " + (rowColAmount - 2) + "]");
  }

  @Test
  @DisplayName("Maze generation produces a non-null tile grid")
  void mazeGenerationProducesGrid() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    Tile[][] tiles = rb.startGeneration();

    assertNotNull(tiles, "Generated tile grid should not be null");
    assertEquals(rowColAmount, tiles.length, "Grid should match rowColAmount");
    assertEquals(rowColAmount, tiles[0].length, "Grid columns should match rowColAmount");
  }

  @Test
  @DisplayName("Generated maze contains at least one TilePassage and one TileExit")
  void mazeContainsPassageAndExit() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    Tile[][] tiles = rb.startGeneration();

    assertNotNull(tiles);

    boolean hasPassage = false;
    boolean hasExit = false;

    for (Tile[] row : tiles) {
      for (Tile tile : row) {
        if (tile instanceof TilePassage) hasPassage = true;
        if (tile instanceof TileExit) hasExit = true;
      }
    }

    assertTrue(hasPassage, "Maze should contain at least one passage");
    assertTrue(hasExit, "Maze should contain an exit tile");
  }

  @Test
  @DisplayName("Keys are placed in the maze")
  void keysArePlaced() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    rb.startGeneration();

    assertFalse(rb.getKeyCoords().isEmpty(), "Maze should have keys placed");
  }
}
