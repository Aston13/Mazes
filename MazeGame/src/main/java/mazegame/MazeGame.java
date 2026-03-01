package mazegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Main game class — owns the JFrame, delegates gameplay to {@link GameLoop}, input to {@link
 * InputHandler}, rendering to {@link Renderer}, and menu screens to {@link MenuManager}.
 *
 * <p>A single JFrame is reused for the lifetime of the application; screens are swapped via content
 * pane replacement rather than disposing and recreating frames.
 */
public class MazeGame extends JFrame implements GameLoop.Callbacks, InputHandler.Listener {

  private static final int TILE_SIZE = 100;
  private static final int TILE_BORDER = 0;
  private static final int MOVEMENT_SPEED = 5;
  private static final int INITIAL_GRID_SIZE = 10;
  private static final int PAUSE_OVERLAY_ALPHA = 200;
  private static final int PAUSE_TITLE_FONT_SIZE = 40;
  private static final int PAUSE_BUTTON_FONT_SIZE = 20;
  private static final int PAUSE_BUTTON_WIDTH = 200;
  private static final int PAUSE_BUTTON_HEIGHT = 45;

  private final GamePanel gameView = new GamePanel();
  private final int windowWidth;
  private final int windowHeight;
  private final UI ui;
  private final AssetManager assetManager;
  private final MenuManager menuManager;
  private final InputHandler inputHandler;

  private volatile boolean gameInProgress;
  private volatile boolean paused;
  private volatile String pauseAction;
  private Rectangle resumeBtn;
  private Rectangle restartBtn;
  private Rectangle menuBtn;
  private Player player;
  private Renderer renderer;
  private JPanel pane = new JPanel(new GridLayout());
  private int levelCount = 1;
  private int rowColAmount;
  private String stateChange;
  private String[] levelData;
  private GameLoop gameLoop;

  // Always render to an offscreen buffer at the logical resolution, then scale to the canvas.
  private BufferedImage offscreenBuffer = null;

  /**
   * Creates a new game instance.
   *
   * @param windowHeight the window height in pixels
   * @param windowWidth the window width in pixels
   * @param ui the UI factory for menu components
   * @param rowColAmount the initial maze grid size
   */
  public MazeGame(int windowHeight, int windowWidth, UI ui, int rowColAmount) {
    this.windowWidth = windowWidth;
    this.windowHeight = windowHeight;
    this.ui = ui;
    if (rowColAmount % 2 == 0) {
      rowColAmount += 1;
    }
    this.rowColAmount = rowColAmount;
    this.assetManager = new AssetManager();
    this.menuManager = new MenuManager(this, ui, this);
    this.inputHandler = new InputHandler(this);
    load(false);
    setCurrentLevel(-1);
  }

  // ---------------------------------------------------------------------------
  // Level / save management
  // ---------------------------------------------------------------------------

  /**
   * Sets the current level. Pass {@code -1} to auto-detect the first incomplete level from saved
   * data.
   *
   * @param level the level number, or -1 for auto-detect
   */
  public void setCurrentLevel(int level) {
    if (level == -1) {
      for (int i = 1; i < levelData.length; i++) {
        String[] lineWords = levelData[i].split(",");
        if (lineWords[1].equalsIgnoreCase("incomplete")) {
          levelCount = i;
          rowColAmount += ((i - 1) * 2);
          break;
        }
      }
    } else {
      levelCount = level;
      int rc = INITIAL_GRID_SIZE + ((level - 1) * 2);
      if (rc % 2 == 0) {
        rc += 1;
      }
      rowColAmount = rc;
    }
  }

  /** Saves current level progress to disk. */
  public void save() {
    try {
      assetManager.saveLevelData(levelData);
    } catch (IOException ex) {
      System.err.println("Save failed: " + ex.getMessage());
    }
  }

  /** Loads level data from disk or classpath resource. */
  public void load(boolean reset) {
    try {
      levelData = assetManager.loadLevelData(reset);
    } catch (IOException ex) {
      System.err.println("Load failed: " + ex.getMessage());
    }
  }

  /** Returns the current level data array. */
  public String[] getLevelData() {
    return levelData;
  }

  /** Records a level completion, updating best time if improved. */
  public void recordLevelCompletion(int level, double timeTaken) {
    String[] lineWords = levelData[level].split(",");
    double bestTime = Double.parseDouble(lineWords[2]);
    if (timeTaken < bestTime || bestTime == -1) {
      bestTime = timeTaken;
    }
    levelData[level] = level + ",completed," + bestTime;
    save();
  }

  /** Advances to the next level (up to 30). */
  public void increaseLevel() {
    if (levelCount < 30) {
      levelCount += 1;
      rowColAmount += 2;
    }
  }

  // ---------------------------------------------------------------------------
  // Frame setup
  // ---------------------------------------------------------------------------

  /** Configures the JFrame: resizable, exit-on-close, centred. */
  public void setUpFrame() {
    setResizable(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setContentPane(pane);
    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  // ---------------------------------------------------------------------------
  // State management
  // ---------------------------------------------------------------------------

  /**
   * Sets the game state and reason for the state change.
   *
   * @param inProgress true if a level is actively being played
   * @param reason the reason for the state change
   */
  public void setGameState(boolean inProgress, String reason) {
    gameInProgress = inProgress;
    stateChange = reason;
  }

  public boolean getGameState() {
    return gameInProgress;
  }

  // ---------------------------------------------------------------------------
  // Level launching (reuses single JFrame — no dispose/recreate)
  // ---------------------------------------------------------------------------

  /**
   * Starts (or restarts) a level. Reuses this JFrame — stops any running game loop, sets up the
   * canvas, generates the maze, and starts a new loop.
   */
  public void startLevel() {
    // Stop any existing game loop
    if (gameLoop != null) {
      gameLoop.stop();
      gameLoop.join();
    }

    // Reset pause state
    paused = false;
    pauseAction = null;

    // Set up content
    pane = new JPanel(new GridLayout());
    setUpFrame();

    inputHandler.bindMovementKeys(pane);
    inputHandler.installGlobalDispatcher();

    pane.add(gameView);
    gameView.setFocusable(true);
    validate(); // Force layout so gameView has non-zero dimensions before first render

    renderer = new Renderer(windowWidth, windowHeight, rowColAmount, TILE_SIZE, assetManager, this);
    renderer.generateMaze(TILE_SIZE, TILE_BORDER);
    renderer.centerMaze();
    player = new Player(renderer.getStartingX(), renderer.getStartingY(), TILE_SIZE);
    renderer.beginTimer();

    setGameState(true, "");
    render();

    inputHandler.bindPauseMouseClicks(gameView);

    gameLoop = new GameLoop(this);
    gameLoop.setOnComplete(
        () -> {
          inputHandler.removeGlobalDispatcher();
          cleanUpGameView();
          handleLevelEnd();
        });
    gameLoop.start();
  }

  /** Starts a specific level from the level-selection screen. */
  public void playSelectedLevel() {
    startLevel();
  }

  private void cleanUpGameView() {
    renderBackground();
    if (renderer != null) {
      renderer.stopTimer();
    }
  }

  private void handleLevelEnd() {
    if ("Level Failed".equalsIgnoreCase(stateChange)) {
      menuManager.showGameOverScreen(levelCount);
    } else if ("Next Level".equalsIgnoreCase(stateChange)) {
      double timeInMs = renderer.getTimeTaken();
      menuManager.showCompletionScreen(levelCount, timeInMs);
    } else if ("Restart".equalsIgnoreCase(stateChange)) {
      startLevel();
    } else if ("Menu".equalsIgnoreCase(stateChange)) {
      menuManager.showMainMenu();
    }
  }

  // ---------------------------------------------------------------------------
  // Menu delegation
  // ---------------------------------------------------------------------------

  /** Shows the main menu (reuses this frame). */
  public void runMenu() {
    // Stop any running game loop
    if (gameLoop != null) {
      gameLoop.stop();
      gameLoop.join();
    }

    try {
      super.remove(gameView);
      pane.removeAll();
    } catch (Exception e) {
      // Ignore cleanup errors
    }

    pane = new JPanel(new GridLayout());
    setUpFrame();
    menuManager.showMainMenu();
  }

  /** Shows the level-selection screen. */
  public void runLevelSelection() {
    menuManager.showLevelSelection();
  }

  // ---------------------------------------------------------------------------
  // GameLoop.Callbacks implementation
  // ---------------------------------------------------------------------------

  @Override
  public void onUpdate() {
    update();
  }

  @Override
  public void onRender() {
    render();
  }

  @Override
  public void onAnimationTick() {
    renderer.updateFrames();
  }

  @Override
  public boolean isGameInProgress() {
    return gameInProgress;
  }

  @Override
  public boolean isPaused() {
    return paused;
  }

  @Override
  public boolean handlePauseFrame() {
    String action = pauseAction;
    if ("resume".equals(action)) {
      pauseAction = null;
      paused = false;
      renderer.beginTimer();
      return true;
    } else if ("restart".equals(action)) {
      pauseAction = null;
      setGameState(false, "Restart");
      return true;
    } else if ("menu".equals(action)) {
      pauseAction = null;
      setGameState(false, "Menu");
      return true;
    } else {
      renderPauseScreen();
      return false;
    }
  }

  // ---------------------------------------------------------------------------
  // InputHandler.Listener implementation
  // ---------------------------------------------------------------------------

  @Override
  public void onPauseRequested() {
    paused = true;
    if (renderer != null) {
      renderer.stopTimer();
    }
    if (player != null) {
      player.setMoveN(false);
      player.setMoveE(false);
      player.setMoveS(false);
      player.setMoveW(false);
    }
  }

  @Override
  public void onResumeRequested() {
    pauseAction = "resume";
  }

  @Override
  public void onRestartRequested() {
    pauseAction = "restart";
  }

  @Override
  public void onMenuRequested() {
    pauseAction = "menu";
  }

  @Override
  public Player getPlayer() {
    return player;
  }

  @Override
  public Rectangle getResumeBtn() {
    return resumeBtn;
  }

  @Override
  public Rectangle getRestartBtn() {
    return restartBtn;
  }

  @Override
  public Rectangle getMenuBtn() {
    return menuBtn;
  }

  // ---------------------------------------------------------------------------
  // Rendering
  // ---------------------------------------------------------------------------

  /**
   * Returns graphics for the offscreen buffer at the fixed logical resolution. All game rendering
   * happens at this resolution; the result is scaled to the canvas in {@link #showBuffer()}.
   */
  private Graphics getGameGraphics() {
    if (offscreenBuffer == null
        || offscreenBuffer.getWidth() != windowWidth
        || offscreenBuffer.getHeight() != windowHeight) {
      offscreenBuffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
    }
    return offscreenBuffer.getGraphics();
  }

  /**
   * Pushes the offscreen buffer to the game panel and triggers an immediate repaint. Since the game
   * loop now runs on the EDT via {@link javax.swing.Timer}, we can call {@code paintImmediately}
   * directly for reliable rendering in CheerpJ.
   */
  private void showBuffer() {
    if (offscreenBuffer == null) return;
    gameView.setBuffer(offscreenBuffer);
    int w = gameView.getWidth();
    int h = gameView.getHeight();
    if (w > 0 && h > 0) {
      gameView.paintImmediately(0, 0, w, h);
    }
  }

  /**
   * Converts canvas-space coordinates to logical-space coordinates, accounting for the current
   * scale factor and letterbox offset.
   */
  public int[] toLogicalCoords(int canvasX, int canvasY) {
    int cw = gameView.getWidth();
    int ch = gameView.getHeight();
    if (cw <= 0 || ch <= 0) return new int[] {canvasX, canvasY};

    double scale = Math.min((double) cw / windowWidth, (double) ch / windowHeight);
    int scaledW = (int) (windowWidth * scale);
    int scaledH = (int) (windowHeight * scale);
    int offsetX = (cw - scaledW) / 2;
    int offsetY = (ch - scaledH) / 2;

    return new int[] {(int) ((canvasX - offsetX) / scale), (int) ((canvasY - offsetY) / scale)};
  }

  public void update() {
    int halfPlayer = player.getSize() / 2;
    Graphics g = getGameGraphics();
    if (g == null) return;

    if (player.getMoveN()) {
      int[] nextTile =
          renderer.getTile(
              player.getX(),
              player.getY() - (halfPlayer + 1),
              player.getSize(),
              TILE_SIZE,
              TILE_BORDER);
      if (renderer.checkCollision(nextTile, this)) {
        renderer.moveMazeY(g, rowColAmount, MOVEMENT_SPEED);
        player.setY(player.getY() - MOVEMENT_SPEED);
      }
    }
    if (player.getMoveE()) {
      int[] nextTile =
          renderer.getTile(
              player.getX() + (halfPlayer + 1),
              player.getY(),
              player.getSize(),
              TILE_SIZE,
              TILE_BORDER);
      if (renderer.checkCollision(nextTile, this)) {
        renderer.moveMazeX(g, rowColAmount, -MOVEMENT_SPEED);
        player.setX(player.getX() + MOVEMENT_SPEED);
      }
    }
    if (player.getMoveS()) {
      int[] nextTile =
          renderer.getTile(
              player.getX(),
              player.getY() + (halfPlayer + 1),
              player.getSize(),
              TILE_SIZE,
              TILE_BORDER);
      if (renderer.checkCollision(nextTile, this)) {
        renderer.moveMazeY(g, rowColAmount, -MOVEMENT_SPEED);
        player.setY(player.getY() + MOVEMENT_SPEED);
      }
    }
    if (player.getMoveW()) {
      int[] nextTile =
          renderer.getTile(
              player.getX() - (halfPlayer + 1),
              player.getY(),
              player.getSize(),
              TILE_SIZE,
              TILE_BORDER);
      if (renderer.checkCollision(nextTile, this)) {
        renderer.moveMazeX(g, rowColAmount, MOVEMENT_SPEED);
        player.setX(player.getX() - MOVEMENT_SPEED);
      }
    }
    g.dispose();
  }

  public void render() {
    Graphics g = getGameGraphics();
    if (g == null) return;
    renderer.renderBackground(g);
    renderer.renderMaze(g, TILE_SIZE);
    renderer.renderPlayer(g, player, TILE_SIZE);
    renderer.renderHUD(g, player, levelCount);
    g.dispose();
    showBuffer();
  }

  public void renderBackground() {
    Graphics g = getGameGraphics();
    if (g == null) return;
    renderer.renderBackground(g);
    g.dispose();
    showBuffer();
  }

  private void renderPauseScreen() {
    Graphics g = getGameGraphics();
    if (g == null) return;

    // Dark overlay
    g.setColor(new Color(0, 0, 0, PAUSE_OVERLAY_ALPHA));
    g.fillRect(0, 0, windowWidth, windowHeight);

    // Title
    g.setColor(Color.CYAN);
    g.setFont(new Font("Dialog", Font.PLAIN, PAUSE_TITLE_FONT_SIZE));
    FontMetrics fmTitle = g.getFontMetrics();
    String title = "Paused";
    g.drawString(title, (windowWidth - fmTitle.stringWidth(title)) / 2, windowHeight / 4);

    // Buttons
    g.setFont(new Font("Dialog", Font.PLAIN, PAUSE_BUTTON_FONT_SIZE));
    FontMetrics fm = g.getFontMetrics();

    int btnX = (windowWidth - PAUSE_BUTTON_WIDTH) / 2;
    int btnGap = PAUSE_BUTTON_HEIGHT + 15;

    int resumeY = windowHeight / 2 - PAUSE_BUTTON_HEIGHT - btnGap / 2;
    resumeBtn = new Rectangle(btnX, resumeY, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
    g.setColor(Color.DARK_GRAY);
    g.fillRect(resumeBtn.x, resumeBtn.y, resumeBtn.width, resumeBtn.height);
    g.setColor(Color.WHITE);
    g.drawRect(resumeBtn.x, resumeBtn.y, resumeBtn.width, resumeBtn.height);
    String rText = "Resume [Space/Esc]";
    g.drawString(rText, btnX + (PAUSE_BUTTON_WIDTH - fm.stringWidth(rText)) / 2, resumeY + 30);

    int restartY = resumeY + btnGap;
    restartBtn = new Rectangle(btnX, restartY, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
    g.setColor(Color.DARK_GRAY);
    g.fillRect(restartBtn.x, restartBtn.y, restartBtn.width, restartBtn.height);
    g.setColor(Color.WHITE);
    g.drawRect(restartBtn.x, restartBtn.y, restartBtn.width, restartBtn.height);
    String rstText = "Restart Level [R]";
    g.drawString(rstText, btnX + (PAUSE_BUTTON_WIDTH - fm.stringWidth(rstText)) / 2, restartY + 30);

    int menuY = restartY + btnGap;
    menuBtn = new Rectangle(btnX, menuY, PAUSE_BUTTON_WIDTH, PAUSE_BUTTON_HEIGHT);
    g.setColor(Color.DARK_GRAY);
    g.fillRect(menuBtn.x, menuBtn.y, menuBtn.width, menuBtn.height);
    g.setColor(Color.WHITE);
    g.drawRect(menuBtn.x, menuBtn.y, menuBtn.width, menuBtn.height);
    String mText = "Main Menu";
    g.drawString(mText, btnX + (PAUSE_BUTTON_WIDTH - fm.stringWidth(mText)) / 2, menuY + 30);

    g.dispose();
    showBuffer();
  }

  // ---------------------------------------------------------------------------
  // Backwards-compat helpers used by UI (level selection)
  // ---------------------------------------------------------------------------

  public void setLevel(int level) {
    levelCount += level - 1;
    rowColAmount += ((level - 1) * 2);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(windowWidth, windowHeight);
  }
}
