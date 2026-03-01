package mazegame;

/**
 * Stores user-configurable game settings such as the active dog skin. Settings are held in memory
 * for the lifetime of the application (no persistence needed).
 */
public class GameSettings {

  /** Available dog character skins. */
  public enum DogSkin {
    WESLEY("wesley", "Wesley"),
    SASSO("sasso", "Sasso");

    private final String prefix;
    private final String displayName;

    DogSkin(String prefix, String displayName) {
      this.prefix = prefix;
      this.displayName = displayName;
    }

    /** Returns the asset-key prefix for this skin (e.g. "wesley" → "wesleyEast0"). */
    public String prefix() {
      return prefix;
    }

    /** Returns the human-readable name shown in the UI. */
    public String displayName() {
      return displayName;
    }
  }

  private DogSkin activeSkin = DogSkin.WESLEY;

  /** Returns the currently selected dog skin. */
  public DogSkin getActiveSkin() {
    return activeSkin;
  }

  /** Sets the active dog skin. */
  public void setActiveSkin(DogSkin skin) {
    this.activeSkin = skin;
  }

  /**
   * Returns the sprite asset-key prefix for the active skin (e.g. "wesley"). Used by {@link
   * Renderer} to look up directional animation frames like {@code <prefix>East0}.
   */
  public String getSpritePrefix() {
    return activeSkin.prefix();
  }
}
