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
  private boolean soundMuted;
  private boolean musicMuted;
  private float musicVolume = 0.5f;

  /** Returns the currently selected dog skin. */
  public DogSkin getActiveSkin() {
    return activeSkin;
  }

  /** Sets the active dog skin. */
  public void setActiveSkin(DogSkin skin) {
    this.activeSkin = skin;
  }

  /** Returns whether sound effects are muted. */
  public boolean isSoundMuted() {
    return soundMuted;
  }

  /** Sets whether sound effects are muted. */
  public void setSoundMuted(boolean muted) {
    this.soundMuted = muted;
  }

  /** Returns whether music is muted. */
  public boolean isMusicMuted() {
    return musicMuted;
  }

  /** Sets whether music is muted. */
  public void setMusicMuted(boolean muted) {
    this.musicMuted = muted;
  }

  /** Returns the music volume (0.0 – 1.0). */
  public float getMusicVolume() {
    return musicVolume;
  }

  /** Sets the music volume (clamped to 0.0 – 1.0). */
  public void setMusicVolume(float volume) {
    this.musicVolume = Math.max(0f, Math.min(1f, volume));
  }

  /**
   * Returns the sprite asset-key prefix for the active skin (e.g. "wesley"). Used by {@link
   * Renderer} to look up directional animation frames like {@code <prefix>East0}.
   */
  public String getSpritePrefix() {
    return activeSkin.prefix();
  }
}
