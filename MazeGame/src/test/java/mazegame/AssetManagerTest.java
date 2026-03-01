package mazegame;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AssetManagerTest {

  @Test
  @DisplayName("loadLevelData returns non-null data from classpath resource")
  void loadLevelDataFromClasspath() throws IOException {
    AssetManager am = new AssetManager();
    String[] data = am.loadLevelData(false);

    assertNotNull(data);
    assertTrue(data.length > 0, "Level data should have at least one line");
  }

  @Test
  @DisplayName("loadLevelData with reset returns reset data")
  void loadResetData() throws IOException {
    AssetManager am = new AssetManager();
    String[] data = am.loadLevelData(true);

    assertNotNull(data);
    assertTrue(data.length > 0, "Reset data should have at least one line");
  }

  @Test
  @DisplayName("Level data has expected format (comma-separated)")
  void levelDataFormat() throws IOException {
    AssetManager am = new AssetManager();
    String[] data = am.loadLevelData(true);

    // First line is header, subsequent lines are level data
    for (int i = 1; i < data.length; i++) {
      String[] parts = data[i].split(",");
      assertTrue(
          parts.length >= 3,
          "Level line " + i + " should have at least 3 comma-separated values: " + data[i]);
    }
  }

  @Test
  @DisplayName("saveLevelData writes to external file")
  void saveLevelData() throws IOException {
    AssetManager am = new AssetManager();
    String[] lines = {"header", "1,incomplete,-1", "2,incomplete,-1"};

    // Save to current dir
    am.saveLevelData(lines);

    File saved = new File("LevelData.txt");
    try {
      assertTrue(saved.exists(), "LevelData.txt should be created");
      String content = Files.readString(saved.toPath());
      assertTrue(content.contains("header"));
      assertTrue(content.contains("1,incomplete,-1"));
    } finally {
      // Clean up
      saved.delete();
    }
  }

  @Test
  @DisplayName("getPreloadedImage returns null before preload")
  void getImageBeforePreload() {
    AssetManager am = new AssetManager();
    assertNull(am.getPreloadedImage("nonexistent"));
  }

  @Test
  @DisplayName("preloadImages loads all expected image keys")
  void preloadImages() throws IOException {
    AssetManager am = new AssetManager();
    am.preloadImages();

    // Grass variants
    for (int i = 0; i < 4; i++) {
      assertNotNull(
          am.getPreloadedImage("GrassPassage_" + i), "GrassPassage_" + i + " should be loaded");
    }

    // Exit tiles
    assertNotNull(am.getPreloadedImage("Locked Exit"));
    assertNotNull(am.getPreloadedImage("Open Exit"));

    // Key frames
    for (int i = 0; i < 20; i++) {
      assertNotNull(am.getPreloadedImage("Key_" + i), "Key_" + i + " should be loaded");
    }

    // Dog frames
    for (int i = 0; i < 6; i++) {
      assertNotNull(am.getPreloadedImage("dogNorth" + i));
      assertNotNull(am.getPreloadedImage("dogSouth" + i));
    }
    for (int i = 0; i < 7; i++) {
      assertNotNull(am.getPreloadedImage("dogEast" + i));
      assertNotNull(am.getPreloadedImage("dogWest" + i));
    }

    // Wall variants (all 16 NESW combinations)
    for (int n = 0; n <= 1; n++) {
      for (int e = 0; e <= 1; e++) {
        for (int s = 0; s <= 1; s++) {
          for (int w = 0; w <= 1; w++) {
            String key = "wall_" + n + e + s + w;
            assertNotNull(am.getPreloadedImage(key), key + " should be loaded");
          }
        }
      }
    }
  }

  @Test
  @DisplayName("getKeyFrame returns a non-null image after preload")
  void getKeyFrame() throws IOException {
    AssetManager am = new AssetManager();
    am.preloadImages();

    // Should return a key frame image
    assertNotNull(am.getKeyFrame());
  }
}
