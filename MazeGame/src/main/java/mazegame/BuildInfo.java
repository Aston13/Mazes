package mazegame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Provides the application version baked in at build time via {@code version.properties}. */
public final class BuildInfo {

  private static final String VERSION;

  static {
    String v = "dev";
    try (InputStream in = BuildInfo.class.getResourceAsStream("/mazegame/version.properties")) {
      if (in != null) {
        Properties props = new Properties();
        props.load(in);
        v = props.getProperty("version", "dev");
      }
    } catch (IOException ignored) {
      // fall back to "dev"
    }
    VERSION = v;
  }

  private BuildInfo() {}

  /** Returns the project version (e.g. "1.0.0") or "dev" if unavailable. */
  public static String getVersion() {
    return VERSION;
  }
}
