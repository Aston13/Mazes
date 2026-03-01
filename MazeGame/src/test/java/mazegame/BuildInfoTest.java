package mazegame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BuildInfoTest {

  @Test
  @DisplayName("getVersion returns a non-null, non-blank string")
  void versionIsNotNull() {
    String version = BuildInfo.getVersion();
    assertNotNull(version, "Version should not be null");
    assertFalse(version.isBlank(), "Version should not be blank");
  }

  @Test
  @DisplayName("Version is either 'dev' or a semver pattern")
  void versionFormatIsValid() {
    String version = BuildInfo.getVersion();
    boolean isDev = "dev".equals(version);
    boolean isSemver = version.matches("\\d+\\.\\d+\\.\\d+.*");
    assertTrue(isDev || isSemver, "Version should be 'dev' or semver, got: " + version);
  }
}
