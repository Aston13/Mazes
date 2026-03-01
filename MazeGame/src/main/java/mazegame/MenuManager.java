package mazegame;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * Manages all non-gameplay screens (main menu, level selection, game over, level completion).
 * Reuses a single JFrame — panels are swapped via {@link JFrame#setContentPane} instead of creating
 * new frames.
 */
public class MenuManager {

  private final JFrame frame;
  private final UI ui;
  private final MazeGame game;

  public MenuManager(JFrame frame, UI ui, MazeGame game) {
    this.frame = frame;
    this.ui = ui;
    this.game = game;
  }

  /** Shows the main menu with custom-painted panel. */
  public void showMainMenu() {
    MainMenuPanel menuPanel = new MainMenuPanel(game.getAudioManager());

    // Use the Wesley pixel-art image as the menu decoration
    BufferedImage dogSprite = game.getAssetManager().getPreloadedImage("wesleyPixel");
    if (dogSprite == null) {
      // Fallback to active dog skin walking frame
      String spriteKey = game.getSettings().getSpritePrefix() + "East0";
      dogSprite = game.getAssetManager().getPreloadedImage(spriteKey);
    }
    if (dogSprite != null) {
      menuPanel.setDecorationImage(dogSprite);
    }

    String playLabel = game.hasProgress() ? "Continue" : "Play";
    menuPanel.addButton(playLabel, "[Space]", game::startLevel);
    menuPanel.addButton("Level Selection", "", this::showLevelSelection);
    menuPanel.addButton("Settings", "", this::showSettings);

    boolean inBrowser = "true".equals(System.getProperty("cheerpj.browser"));
    if (!inBrowser) {
      menuPanel.addButton("Quit", "[Esc]", frame::dispose);
    }

    // Keyboard shortcuts
    InputHandler.bindKey(
        menuPanel, KeyEvent.VK_SPACE, "Next Level", false, evt -> game.startLevel());
    InputHandler.bindKey(
        menuPanel,
        KeyEvent.VK_ESCAPE,
        "Exit",
        false,
        evt -> {
          if (!inBrowser) frame.dispose();
        });

    swapContent(menuPanel);
  }

  /** Shows the game-over screen for the current level. */
  public void showGameOverScreen(int level) {
    ResultOverlayPanel overlay =
        new ResultOverlayPanel("You failed level " + level + "!", game.getAudioManager());
    overlay.addButton("Retry Level", "[Space]", game::startLevel);
    overlay.addButton("Main Menu", "[Esc]", this::showMainMenu);

    InputHandler.bindKey(
        overlay, KeyEvent.VK_SPACE, "Retry Level", false, evt -> game.startLevel());
    InputHandler.bindKey(overlay, KeyEvent.VK_ESCAPE, "Menu", false, evt -> showMainMenu());

    swapContent(overlay);
  }

  /** Shows the level-completion screen. */
  public void showCompletionScreen(int level, double timeTaken) {
    game.recordLevelCompletion(level, timeTaken);

    ResultOverlayPanel overlay =
        new ResultOverlayPanel("Completed Level " + level + "!", game.getAudioManager());
    overlay.addButton(
        "Next Level",
        "[Space]",
        () -> {
          game.increaseLevel();
          game.startLevel();
        });
    overlay.addButton("Main Menu", "[Esc]", this::showMainMenu);

    InputHandler.bindKey(
        overlay,
        KeyEvent.VK_SPACE,
        "Next Level",
        false,
        evt -> {
          game.increaseLevel();
          game.startLevel();
        });
    InputHandler.bindKey(overlay, KeyEvent.VK_ESCAPE, "Menu", false, evt -> showMainMenu());

    swapContent(overlay);
  }

  /** Shows the level-selection screen with custom-painted cards. */
  public void showLevelSelection() {
    String[] levelData = game.getLevelData();

    LevelSelectionPanel panel =
        new LevelSelectionPanel(
            levelData,
            game,
            this::showMainMenu,
            () -> {
              game.load(true);
              game.save();
              showLevelSelection(); // refresh after reset
            },
            game.getAudioManager());

    swapContent(panel);
    panel.requestFocusInWindow();
  }

  /** Shows the settings screen (skin selection, etc.). */
  public void showSettings() {
    SettingsPanel panel =
        new SettingsPanel(
            game.getSettings(), game.getAssetManager(), game.getAudioManager(), this::showMainMenu);
    swapContent(panel);
    panel.requestFocusInWindow();
  }

  /** Replaces the frame's content pane and refreshes. */
  private void swapContent(javax.swing.JComponent component) {
    frame.setContentPane(component);
    frame.revalidate();
    frame.repaint();
  }
}
