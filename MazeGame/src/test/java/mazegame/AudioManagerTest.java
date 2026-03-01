package mazegame;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AudioManagerTest {

  @Test
  @DisplayName("AudioManager constructs without throwing")
  void constructsSuccessfully() {
    assertDoesNotThrow(AudioManager::new);
  }

  @Test
  @DisplayName("Not muted by default")
  void notMutedByDefault() {
    AudioManager am = new AudioManager();
    assertFalse(am.isMuted());
  }

  @Test
  @DisplayName("setMuted changes muted state")
  void setMutedChangesState() {
    AudioManager am = new AudioManager();
    am.setMuted(true);
    assertTrue(am.isMuted());

    am.setMuted(false);
    assertFalse(am.isMuted());
  }

  @Test
  @DisplayName("toggleMute flips muted state and returns new value")
  void toggleMuteFlips() {
    AudioManager am = new AudioManager();
    assertFalse(am.isMuted());

    boolean result = am.toggleMute();
    assertTrue(result, "toggleMute should return true after first toggle");
    assertTrue(am.isMuted());

    result = am.toggleMute();
    assertFalse(result, "toggleMute should return false after second toggle");
    assertFalse(am.isMuted());
  }

  @Test
  @DisplayName("Music not muted by default")
  void musicNotMutedByDefault() {
    AudioManager am = new AudioManager();
    assertFalse(am.isMusicMuted());
  }

  @Test
  @DisplayName("setMusicMuted changes music muted state")
  void setMusicMutedChangesState() {
    AudioManager am = new AudioManager();
    am.setMusicMuted(true);
    assertTrue(am.isMusicMuted());

    am.setMusicMuted(false);
    assertFalse(am.isMusicMuted());
  }

  @Test
  @DisplayName("Default music volume is 0.5")
  void defaultMusicVolume() {
    AudioManager am = new AudioManager();
    assertEquals(0.5f, am.getMusicVolume(), 0.001f);
  }

  @Test
  @DisplayName("setMusicVolume stores value within range")
  void setMusicVolumeInRange() {
    AudioManager am = new AudioManager();
    am.setMusicVolume(0.8f);
    assertEquals(0.8f, am.getMusicVolume(), 0.001f);
  }

  @Test
  @DisplayName("setMusicVolume clamps below 0 to 0")
  void setMusicVolumeClampsBelowZero() {
    AudioManager am = new AudioManager();
    am.setMusicVolume(-1f);
    assertEquals(0.0f, am.getMusicVolume(), 0.001f);
  }

  @Test
  @DisplayName("setMusicVolume clamps above 1 to 1")
  void setMusicVolumeClamsAboveOne() {
    AudioManager am = new AudioManager();
    am.setMusicVolume(2f);
    assertEquals(1.0f, am.getMusicVolume(), 0.001f);
  }

  // Bug 4 regression: music continued after quitting to menu
  @Test
  @DisplayName("stopMusic is safe when no music is playing")
  void stopMusicWhenNothingPlaying() {
    AudioManager am = new AudioManager();
    assertDoesNotThrow(am::stopMusic);
  }

  @Test
  @DisplayName("stopMusic called twice does not throw")
  void doubleStopDoesNotThrow() {
    AudioManager am = new AudioManager();
    assertDoesNotThrow(am::stopMusic);
    assertDoesNotThrow(am::stopMusic);
  }

  @Test
  @DisplayName("play with muted flag does not throw")
  void playWhileMutedIsNoOp() {
    AudioManager am = new AudioManager();
    am.setMuted(true);
    assertDoesNotThrow(() -> am.play(AudioManager.Sound.KEY_PICKUP));
    assertDoesNotThrow(() -> am.play(AudioManager.Sound.LEVEL_COMPLETE));
  }

  @Test
  @DisplayName("play with sound effects does not throw when unmuted")
  void playUnmutedDoesNotThrow() {
    AudioManager am = new AudioManager();
    // May fail silently if no audio device; the contract is it should not throw
    assertDoesNotThrow(() -> am.play(AudioManager.Sound.BUTTON_CLICK));
  }
}
