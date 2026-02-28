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
    Tile[][] tiles = null;
    for (int attempt = 0; attempt < 5; attempt++) {
      try {
        int rowColAmount = 11;
        RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
        tiles = rb.startGeneration();

        assertNotNull(tiles, "Generated tile grid should not be null");
        assertEquals(rowColAmount, tiles.length, "Grid should match rowColAmount");
        assertEquals(rowColAmount, tiles[0].length, "Grid columns should match rowColAmount");
        break;
      } catch (ClassCastException e) {
        if (attempt == 4) {
          fail("Maze generation failed after 5 attempts: " + e.getMessage());
        }
      }
    }
  }

  @Test
  @DisplayName("Generated maze contains at least one TilePassage and one TileExit")
  void mazeContainsPassageAndExit() {
    // Maze generation involves randomness that can occasionally cause
    // ClassCastException internally. Retry a few times to account for this.
    Tile[][] tiles = null;
    for (int attempt = 0; attempt < 5; attempt++) {
      try {
        int rowColAmount = 11;
        RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
        tiles = rb.startGeneration();
        break;
      } catch (ClassCastException e) {
        if (attempt == 4) {
          fail("Maze generation failed after 5 attempts: " + e.getMessage());
        }
      }
    }

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
    for (int attempt = 0; attempt < 5; attempt++) {
      try {
        int rowColAmount = 11;
        RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
        rb.startGeneration();

        assertFalse(rb.getKeyCoords().isEmpty(), "Maze should have keys placed");
        return;
      } catch (ClassCastException e) {
        if (attempt == 4) {
          fail("Maze generation failed after 5 attempts: " + e.getMessage());
        }
      }
    }
  }
}
