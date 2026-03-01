package mazegame;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TileTest {

  // ─── TilePassage: bounds & position ───────────────────────────────

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

  // ─── TilePassage: item toggle ─────────────────────────────────────

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
  @DisplayName("setItem(false) when already false is safe (no-op)")
  void setItemFalseIdempotent() {
    TilePassage tp = new TilePassage(100, 0, 0);
    assertFalse(tp.hasItem());

    tp.setItem(false);
    assertFalse(tp.hasItem());
    assertEquals("Passage", tp.getImageString());
    assertEquals(Color.BLACK, tp.getColor());
  }

  @Test
  @DisplayName("setItem(true) sets color to DARK_GRAY")
  void setItemTrueColor() {
    TilePassage tp = new TilePassage(100, 0, 0);
    tp.setItem(true);
    assertEquals(Color.DARK_GRAY, tp.getColor());
  }

  // ─── TilePassage: color state transitions ─────────────────────────

  @Test
  @DisplayName("New TilePassage default color is BLACK (from setItem(false) in ctor)")
  void tilePassageDefaultColor() {
    TilePassage tp = new TilePassage(100, 0, 0);
    assertEquals(Color.BLACK, tp.getColor());
  }

  @Test
  @DisplayName("setCheckedExitPath sets flag and turns color ORANGE")
  void checkedExitPathColor() {
    TilePassage tp = new TilePassage(100, 0, 0);
    assertFalse(tp.getCheckedExitPath());

    tp.setCheckedExitPath(true);
    assertTrue(tp.getCheckedExitPath());
    assertEquals(Color.ORANGE, tp.getColor());
  }

  @Test
  @DisplayName("setPlayerExplored sets flag and turns color DARK_GRAY")
  void playerExploredColor() {
    TilePassage tp = new TilePassage(100, 0, 0);
    assertFalse(tp.getPlayerExplored());

    tp.setPlayerExplored(true);
    assertTrue(tp.getPlayerExplored());
    assertEquals(Color.DARK_GRAY, tp.getColor());
  }

  // ─── TilePassage: row/col ─────────────────────────────────────────

  @Test
  @DisplayName("TilePassage row and column can be set and retrieved")
  void tilePassageRowCol() {
    TilePassage tp = new TilePassage(100, 0, 0);
    tp.setRowNo(3);
    tp.setColNo(7);

    assertEquals(3, tp.getRowNo());
    assertEquals(7, tp.getColNo());
  }

  // ─── TilePassage: passageImageId ──────────────────────────────────

  @Test
  @DisplayName("TilePassage passageImageId is 0-3")
  void tilePassageImageIdRange() {
    for (int i = 0; i < 50; i++) {
      TilePassage tp = new TilePassage(100, 0, 0);
      int id = Integer.parseInt(tp.getPassageImageId());
      assertTrue(id >= 0 && id <= 3, "passageImageId should be 0-3, got " + id);
    }
  }

  // ─── TileWall ─────────────────────────────────────────────────────

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
  @DisplayName("TileWall default color is CYAN")
  void tileWallDefaultColor() {
    TileWall tw = new TileWall(100, 0, 0);
    assertEquals(Color.CYAN, tw.getColor());
  }

  @Test
  @DisplayName("TileWall bounds are computed from position and size")
  void tileWallBounds() {
    TileWall tw = new TileWall(80, 20, 30);
    assertEquals(20, tw.getMinX());
    assertEquals(30, tw.getMinY());
    assertEquals(100, tw.getMaxX());
    assertEquals(110, tw.getMaxY());
    assertEquals(80, tw.getSize());
  }

  @Test
  @DisplayName("TileWall position can be updated")
  void tileWallPositionUpdate() {
    TileWall tw = new TileWall(50, 0, 0);
    tw.setMinX(10);
    tw.setMinY(20);
    assertEquals(10, tw.getMinX());
    assertEquals(20, tw.getMinY());
    assertEquals(60, tw.getMaxX());
    assertEquals(70, tw.getMaxY());
  }

  @Test
  @DisplayName("TileWall color can be changed")
  void tileWallColorChange() {
    TileWall tw = new TileWall(50, 0, 0);
    tw.setColor(Color.MAGENTA);
    assertEquals(Color.MAGENTA, tw.getColor());
  }

  @Test
  @DisplayName("TileWall passageImageId is 0-3")
  void tileWallImageIdRange() {
    for (int i = 0; i < 50; i++) {
      TileWall tw = new TileWall(100, 0, 0);
      int id = Integer.parseInt(tw.getPassageImageId());
      assertTrue(id >= 0 && id <= 3, "passageImageId should be 0-3, got " + id);
    }
  }

  // ─── TileExit ─────────────────────────────────────────────────────

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
  @DisplayName("TileExit locked color is RED, unlocked is GREEN")
  void tileExitColors() {
    TileExit te = new TileExit(100, 0, 0);
    assertEquals(Color.RED, te.getColor());

    te.setAccessible(true);
    assertEquals(Color.GREEN, te.getColor());

    te.setAccessible(false);
    assertEquals(Color.RED, te.getColor());
  }

  @Test
  @DisplayName("TileExit bounds are computed from position and size")
  void tileExitBounds() {
    TileExit te = new TileExit(60, 15, 25);
    assertEquals(15, te.getMinX());
    assertEquals(25, te.getMinY());
    assertEquals(75, te.getMaxX());
    assertEquals(85, te.getMaxY());
    assertEquals(60, te.getSize());
  }

  @Test
  @DisplayName("TileExit row and column can be set and retrieved")
  void tileExitRowCol() {
    TileExit te = new TileExit(100, 0, 0);
    te.setRowNo(5);
    te.setColNo(9);

    assertEquals(5, te.getRowNo());
    assertEquals(9, te.getColNo());
  }

  @Test
  @DisplayName("TileExit passageImageId is 0-3")
  void tileExitImageIdRange() {
    for (int i = 0; i < 50; i++) {
      TileExit te = new TileExit(100, 0, 0);
      int id = Integer.parseInt(te.getPassageImageId());
      assertTrue(id >= 0 && id <= 3, "passageImageId should be 0-3, got " + id);
    }
  }

  @Test
  @DisplayName("TileExit position can be updated")
  void tileExitPositionUpdate() {
    TileExit te = new TileExit(50, 0, 0);
    te.setMinX(10);
    te.setMinY(20);
    assertEquals(10, te.getMinX());
    assertEquals(20, te.getMinY());
    assertEquals(60, te.getMaxX());
    assertEquals(70, te.getMaxY());
  }
}
