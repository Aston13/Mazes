package mazegame;

import static org.junit.jupiter.api.Assertions.*;

import mazegame.GameSettings.DogSkin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GameSettingsTest {

  private GameSettings settings;

  @BeforeEach
  void setUp() {
    settings = new GameSettings();
  }

  @Test
  @DisplayName("Default skin is WESLEY")
  void defaultSkinIsWesley() {
    assertEquals(DogSkin.WESLEY, settings.getActiveSkin());
  }

  @Test
  @DisplayName("setActiveSkin changes the active skin")
  void setActiveSkinChangesSkin() {
    settings.setActiveSkin(DogSkin.SASSO);
    assertEquals(DogSkin.SASSO, settings.getActiveSkin());

    settings.setActiveSkin(DogSkin.WESLEY);
    assertEquals(DogSkin.WESLEY, settings.getActiveSkin());
  }

  @Test
  @DisplayName("getSpritePrefix returns prefix of active skin")
  void getSpritePrefixReturnsActivePrefix() {
    assertEquals("wesley", settings.getSpritePrefix());

    settings.setActiveSkin(DogSkin.SASSO);
    assertEquals("sasso", settings.getSpritePrefix());
  }

  @Test
  @DisplayName("DogSkin displayName returns human-readable name")
  void dogSkinDisplayName() {
    assertEquals("Wesley", DogSkin.WESLEY.displayName());
    assertEquals("Sasso", DogSkin.SASSO.displayName());
  }

  @Test
  @DisplayName("DogSkin prefix returns asset key prefix")
  void dogSkinPrefix() {
    assertEquals("wesley", DogSkin.WESLEY.prefix());
    assertEquals("sasso", DogSkin.SASSO.prefix());
  }

  @Test
  @DisplayName("Sound is not muted by default")
  void soundNotMutedByDefault() {
    assertFalse(settings.isSoundMuted());
  }

  @Test
  @DisplayName("setSoundMuted toggles sound mute state")
  void setSoundMutedToggles() {
    settings.setSoundMuted(true);
    assertTrue(settings.isSoundMuted());

    settings.setSoundMuted(false);
    assertFalse(settings.isSoundMuted());
  }

  @Test
  @DisplayName("Music is not muted by default")
  void musicNotMutedByDefault() {
    assertFalse(settings.isMusicMuted());
  }

  @Test
  @DisplayName("setMusicMuted toggles music mute state")
  void setMusicMutedToggles() {
    settings.setMusicMuted(true);
    assertTrue(settings.isMusicMuted());

    settings.setMusicMuted(false);
    assertFalse(settings.isMusicMuted());
  }

  @Test
  @DisplayName("Default music volume is 0.5")
  void defaultMusicVolume() {
    assertEquals(0.5f, settings.getMusicVolume());
  }

  @Test
  @DisplayName("setMusicVolume stores value within range")
  void setMusicVolumeInRange() {
    settings.setMusicVolume(0.7f);
    assertEquals(0.7f, settings.getMusicVolume(), 0.001f);
  }

  @Test
  @DisplayName("setMusicVolume clamps values below 0 to 0")
  void setMusicVolumeClampsBelowZero() {
    settings.setMusicVolume(-0.5f);
    assertEquals(0.0f, settings.getMusicVolume(), 0.001f);
  }

  @Test
  @DisplayName("setMusicVolume clamps values above 1 to 1")
  void setMusicVolumeClamsAboveOne() {
    settings.setMusicVolume(1.5f);
    assertEquals(1.0f, settings.getMusicVolume(), 0.001f);
  }

  @Test
  @DisplayName("setMusicVolume boundary values 0 and 1 are accepted")
  void setMusicVolumeBoundaryValues() {
    settings.setMusicVolume(0.0f);
    assertEquals(0.0f, settings.getMusicVolume(), 0.001f);

    settings.setMusicVolume(1.0f);
    assertEquals(1.0f, settings.getMusicVolume(), 0.001f);
  }

  @Test
  @DisplayName("Wesley is always unlocked regardless of bone count")
  void wesleyAlwaysUnlocked() {
    assertTrue(GameSettings.isSkinUnlocked(DogSkin.WESLEY, 0));
    assertTrue(GameSettings.isSkinUnlocked(DogSkin.WESLEY, 5));
    assertTrue(GameSettings.isSkinUnlocked(DogSkin.WESLEY, 30));
  }

  @Test
  @DisplayName("Sasso requires SASSO_UNLOCK_BONES bones to unlock")
  void sassoRequiresBones() {
    assertFalse(GameSettings.isSkinUnlocked(DogSkin.SASSO, 0));
    assertFalse(GameSettings.isSkinUnlocked(DogSkin.SASSO, GameSettings.SASSO_UNLOCK_BONES - 1));
    assertTrue(GameSettings.isSkinUnlocked(DogSkin.SASSO, GameSettings.SASSO_UNLOCK_BONES));
    assertTrue(GameSettings.isSkinUnlocked(DogSkin.SASSO, 30));
  }

  @Test
  @DisplayName("SASSO_UNLOCK_BONES constant is 10")
  void sassoUnlockThresholdIsTen() {
    assertEquals(10, GameSettings.SASSO_UNLOCK_BONES);
  }
}
