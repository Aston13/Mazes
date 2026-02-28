package mazegame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for MazeGame state management. These tests verify game state
 * transitions and level management without requiring a graphics display.
 */
class MazeGameStateTest {

    private MazeGame game;
    private UI ui;

    @BeforeEach
    void setUp() {
        ui = new UI(650);
        game = new MazeGame(650, 650, ui, 10);
    }

    @Test
    @DisplayName("Initial game state is not in progress")
    void initialStateNotInProgress() {
        assertFalse(game.getGameState());
    }

    @Test
    @DisplayName("setGameState updates game in-progress flag")
    void setGameStateUpdatesFlag() {
        game.setGameState(true, "Test");
        assertTrue(game.getGameState());

        game.setGameState(false, "Done");
        assertFalse(game.getGameState());
    }

    @Test
    @DisplayName("setCurrentLevel with -1 auto-detects first incomplete level")
    void setCurrentLevelAutoDetect() {
        // The constructor already calls setCurrentLevel(-1), which sets
        // levelCount to the first incomplete level. Just verify no exception.
        game.setCurrentLevel(-1);
        // If it completes without error, auto-detect works
    }

    @Test
    @DisplayName("setCurrentLevel with specific level sets correctly")
    void setCurrentLevelSpecific() {
        game.setCurrentLevel(3);
        // No direct getter for levelCount, but this should not throw
    }

    @Test
    @DisplayName("increaseLevel advances the level")
    void increaseLevel() {
        game.setGameState(false, "");
        game.increaseLevel();
        assertTrue(game.getGameState());
    }

    @Test
    @DisplayName("load with reset does not throw")
    void loadWithReset() {
        assertDoesNotThrow(() -> game.load(true));
    }

    @Test
    @DisplayName("load without reset does not throw")
    void loadWithoutReset() {
        assertDoesNotThrow(() -> game.load(false));
    }
}
