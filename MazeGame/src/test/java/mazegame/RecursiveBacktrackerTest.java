package mazegame;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class RecursiveBacktrackerTest {

  // ─── Starting coordinate ──────────────────────────────────────────

  @RepeatedTest(20)
  @DisplayName("Generated starting coordinate is odd and within valid range")
  void startingCoordIsOddAndInRange() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    int coord = rb.getRandomStartingCoord();

    assertTrue(coord % 2 != 0, "Starting coordinate should be odd, got " + coord);
    assertTrue(
        coord >= 1 && coord <= rowColAmount - 2,
        "Coordinate should be within [1, " + (rowColAmount - 2) + "], got " + coord);
  }

  // ─── Basic generation ─────────────────────────────────────────────

  @Test
  @DisplayName("Maze generation produces a non-null tile grid of correct size")
  void mazeGenerationProducesGrid() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    Tile[][] tiles = rb.startGeneration();

    assertNotNull(tiles, "Generated tile grid should not be null");
    assertEquals(rowColAmount, tiles.length, "Grid rows should match rowColAmount");
    assertEquals(rowColAmount, tiles[0].length, "Grid columns should match rowColAmount");
  }

  @Test
  @DisplayName("Generated maze contains at least one TilePassage and one TileExit")
  void mazeContainsPassageAndExit() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    Tile[][] tiles = rb.startGeneration();

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

  // ─── Key placement ────────────────────────────────────────────────

  @Test
  @DisplayName("Keys are placed in the maze")
  void keysArePlaced() {
    int rowColAmount = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(100, 0, rowColAmount);
    rb.startGeneration();

    assertFalse(rb.getKeyCoords().isEmpty(), "Maze should have keys placed");
  }

  @Test
  @DisplayName("Key count matches formula (rowColAmount / 10) * 4")
  void keyCountMatchesFormula() {
    for (int size : new int[] {11, 21, 31}) {
      RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, size);
      rb.startGeneration();

      int expected = (size / 10) * 4;
      assertEquals(
          expected,
          rb.getKeyCoords().size(),
          "For grid size " + size + " expected " + expected + " keys");
    }
  }

  @Test
  @DisplayName("No duplicate key tiles")
  void noDuplicateKeys() {
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, 21);
    rb.startGeneration();

    ArrayList<TilePassage> keys = rb.getKeyCoords();
    Set<TilePassage> unique = new HashSet<>(keys);
    assertEquals(keys.size(), unique.size(), "All key tiles should be distinct objects");
  }

  @Test
  @DisplayName("Every key tile reports hasItem() true")
  void keyTilesHaveItem() {
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, 21);
    rb.startGeneration();

    for (TilePassage keyTile : rb.getKeyCoords()) {
      assertTrue(keyTile.hasItem(), "Key tile should have item flag set");
    }
  }

  // ─── Border walls ─────────────────────────────────────────────────

  @Test
  @DisplayName("All border tiles are walls")
  void borderTilesAreWalls() {
    int size = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, size);
    Tile[][] tiles = rb.startGeneration();

    for (int i = 0; i < size; i++) {
      assertInstanceOf(TileWall.class, tiles[0][i], "Top border [0][" + i + "] should be wall");
      assertInstanceOf(
          TileWall.class, tiles[size - 1][i], "Bottom border [" + (size - 1) + "][" + i + "]");
      assertInstanceOf(TileWall.class, tiles[i][0], "Left border [" + i + "][0] should be wall");
      assertInstanceOf(
          TileWall.class, tiles[i][size - 1], "Right border [" + i + "][" + (size - 1) + "]");
    }
  }

  // ─── Starting position ────────────────────────────────────────────

  @Test
  @DisplayName("Starting position is a passable tile")
  void startingPositionIsPassage() {
    int size = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, size);
    Tile[][] tiles = rb.startGeneration();
    int sy = rb.getStartingY();
    int sx = rb.getStartingX();

    assertTrue(
        tiles[sy][sx] instanceof TilePassage || tiles[sy][sx] instanceof TileExit,
        "Starting tile should be passable (TilePassage or TileExit)");
  }

  @Test
  @DisplayName("Starting coordinates are odd")
  void startingCoordsAreOdd() {
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, 11);
    rb.startGeneration();

    assertTrue(rb.getStartingX() % 2 != 0, "startingX should be odd");
    assertTrue(rb.getStartingY() % 2 != 0, "startingY should be odd");
  }

  // ─── Exactly one exit ─────────────────────────────────────────────

  @Test
  @DisplayName("Generated maze contains exactly one TileExit")
  void exactlyOneExit() {
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, 11);
    Tile[][] tiles = rb.startGeneration();

    int exitCount = 0;
    for (Tile[] row : tiles) {
      for (Tile tile : row) {
        if (tile instanceof TileExit) exitCount++;
      }
    }
    assertEquals(1, exitCount, "Maze should have exactly one exit");
  }

  // ─── Connectivity ─────────────────────────────────────────────────

  @Test
  @DisplayName("All passage/exit tiles are reachable from start via BFS")
  void allPassagesReachable() {
    int size = 11;
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, size);
    Tile[][] tiles = rb.startGeneration();
    int sy = rb.getStartingY();
    int sx = rb.getStartingX();

    boolean[][] visited = new boolean[size][size];
    Queue<int[]> queue = new LinkedList<>();
    queue.add(new int[] {sy, sx});
    visited[sy][sx] = true;
    int reachable = 0;

    int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    while (!queue.isEmpty()) {
      int[] pos = queue.poll();
      reachable++;
      for (int[] d : dirs) {
        int nr = pos[0] + d[0];
        int nc = pos[1] + d[1];
        if (nr >= 0
            && nr < size
            && nc >= 0
            && nc < size
            && !visited[nr][nc]
            && !(tiles[nr][nc] instanceof TileWall)) {
          visited[nr][nc] = true;
          queue.add(new int[] {nr, nc});
        }
      }
    }

    // Count total passable tiles
    int totalPassable = 0;
    for (Tile[] row : tiles) {
      for (Tile tile : row) {
        if (!(tile instanceof TileWall)) totalPassable++;
      }
    }

    assertEquals(totalPassable, reachable, "All passable tiles should be reachable from start");
  }

  // ─── Wall IDs ─────────────────────────────────────────────────────

  @Test
  @DisplayName("All wall tiles have valid 4-character binary neighbour strings")
  void wallIdsAreValid() {
    RecursiveBacktracker rb = new RecursiveBacktracker(50, 0, 11);
    Tile[][] tiles = rb.startGeneration();

    for (Tile[] row : tiles) {
      for (Tile tile : row) {
        if (tile instanceof TileWall tw) {
          String bits = tw.getPassageNeighbours();
          assertNotNull(bits);
          assertEquals(4, bits.length(), "Neighbour string should be 4 chars: " + bits);
          assertTrue(bits.matches("[01]{4}"), "Should be binary: " + bits);
        }
      }
    }
  }

  // ─── Multiple grid sizes ──────────────────────────────────────────

  @Test
  @DisplayName("Maze generation succeeds for various odd grid sizes")
  void variousGridSizes() {
    for (int size : new int[] {5, 11, 15, 21, 31}) {
      RecursiveBacktracker rb = new RecursiveBacktracker(30, 0, size);
      Tile[][] tiles = rb.startGeneration();

      assertNotNull(tiles, "Grid should not be null for size " + size);
      assertEquals(size, tiles.length, "Row count mismatch for size " + size);
      assertEquals(size, tiles[0].length, "Col count mismatch for size " + size);
    }
  }
}
