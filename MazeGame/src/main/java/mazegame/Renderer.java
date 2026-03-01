package mazegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Stack;
import javax.swing.Timer;

/**
 * Handles all in-game rendering: background, maze tiles, player sprite, HUD, collision detection,
 * and animation frames.
 */
public class Renderer {

  private static final int HUD_HEIGHT = 50;
  private static final int HUD_FONT_SIZE = 15;
  private static final int MESSAGE_FONT_SIZE = 20;
  private static final int MESSAGE_DURATION_MS = 5000;
  private static final int TIMER_CHECK_MS = 100;
  private static final double KEY_REMOVAL_INTERVAL = 5.00;
  private static final int MAX_ANIMATION_STACK_SIZE = 10;

  private final BufferedImage view;
  private final int screenWidth;
  private final int screenHeight;
  private final int screenWidthHalf;
  private final int screenHeightHalf;
  private final int rowColAmount;
  private final int keysRequired;
  private final AssetManager assetManager;

  private Tile[][] tileArr;
  private RecursiveBacktracker mazeGenerator;
  private int startingX;
  private int startingY;
  private int tileWidth;

  private int keyRemovalTimer;
  private Timer gameTimer;
  private Stack<Tile> keysOnMap = new Stack<>();

  private String playerMessage = "";
  private boolean displayMsg;
  private long activatedAt = Long.MAX_VALUE;
  private int keyCount;

  private double timeTaken;
  private double timeUntilKeyRemoval = KEY_REMOVAL_INTERVAL;
  private long wallClockAtResumeMs;
  private double accumulatedGameSec;
  private double gameSecAtLastRemoval;
  private BufferedImage playerImg;
  private Stack<BufferedImage> nextPlayerAnimation = new Stack<>();

  /**
   * Creates a new renderer for the game view.
   *
   * @param screenHeight the window height in pixels
   * @param screenWidth the window width in pixels
   * @param rowColAmount the maze grid size (rows and columns)
   * @param tileWH the pixel size of each tile
   * @param assetManager the shared asset manager for image lookup
   * @param game the game instance for state callbacks
   */
  public Renderer(
      int screenHeight,
      int screenWidth,
      int rowColAmount,
      int tileWH,
      AssetManager assetManager,
      MazeGame game) {
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.tileWidth = tileWH;
    this.rowColAmount = rowColAmount;
    this.assetManager = assetManager;

    screenWidthHalf = screenWidth / 2;
    screenHeightHalf = screenHeight / 2;

    keyCount = 0;
    keysRequired = (rowColAmount / 10) * 2;
    keyRemovalTimer = keysRequired;

    try {
      assetManager.preloadImages();
    } catch (IOException e) {
      e.printStackTrace();
    }
    playerImg = assetManager.getPreloadedImage("dogEast0");

    view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);

    gameTimer =
        new Timer(
            TIMER_CHECK_MS,
            (ActionEvent e) -> {
              long nowMs = System.currentTimeMillis();
              double totalGameSec = accumulatedGameSec + (nowMs - wallClockAtResumeMs) / 1000.0;
              timeTaken = totalGameSec;

              double sinceLastRemoval = totalGameSec - gameSecAtLastRemoval;
              timeUntilKeyRemoval = KEY_REMOVAL_INTERVAL - sinceLastRemoval;

              if (timeUntilKeyRemoval <= 0) {
                gameSecAtLastRemoval = totalGameSec;
                keyRemovalTimer--;
                TilePassage removedKey = (TilePassage) keysOnMap.pop();
                removedKey.setItem(false);

                if (keysOnMap.size() < (keysRequired - keyCount)) {
                  game.setGameState(false, "Level Failed");
                  gameTimer.stop();
                }
              }
            });
  }

  /** Starts (or resumes) the in-game timer using wall-clock tracking. */
  public void beginTimer() {
    wallClockAtResumeMs = System.currentTimeMillis();
    gameTimer.start();
  }

  /** Pauses the in-game timer, accumulating elapsed game time. */
  public void stopTimer() {
    if (gameTimer.isRunning()) {
      accumulatedGameSec += (System.currentTimeMillis() - wallClockAtResumeMs) / 1000.0;
    }
    timeTaken = accumulatedGameSec;
    gameTimer.stop();
  }

  /**
   * Returns the elapsed time since the level started.
   *
   * @return time in seconds, rounded to two decimal places
   */
  public double getTimeTaken() {
    DecimalFormat df = new DecimalFormat("#.##");
    return Double.parseDouble(df.format(timeTaken));
  }

  /** Renders the black background. */
  public void renderBackground(Graphics g) {
    g.drawImage(view, 0, 0, screenWidth, screenHeight, null);
  }

  /**
   * Generates a new maze using the recursive backtracker algorithm.
   *
   * @param tileWH pixel size of each tile
   * @param tileBorder border inset
   */
  public void generateMaze(int tileWH, int tileBorder) {
    mazeGenerator = new RecursiveBacktracker(tileWH, tileBorder, rowColAmount);
    tileArr = mazeGenerator.startGeneration();
    startingX = tileArr[mazeGenerator.getStartingX()][mazeGenerator.getStartingY()].getMinX();
    startingY = tileArr[mazeGenerator.getStartingX()][mazeGenerator.getStartingY()].getMinY();

    for (TilePassage keyTile : mazeGenerator.getKeyCoords()) {
      keysOnMap.push(keyTile);
    }
  }

  /** Centres the maze view on the player's starting tile. */
  public void centerMaze() {
    Tile centerTile = tileArr[mazeGenerator.getStartingX()][mazeGenerator.getStartingY()];
    int offsetX = screenWidth / 2 - centerTile.getMinX();
    int offsetY = screenHeight / 2 - centerTile.getMinY();

    for (int row = 0; row < rowColAmount; row++) {
      for (int col = 0; col < rowColAmount; col++) {
        Tile tile = tileArr[col][row];
        tile.setMinX(tile.getMinX() + offsetX);
        tile.setMinY(tile.getMinY() + offsetY);
      }
    }
  }

  /**
   * Renders all visible maze tiles, including grass backgrounds, key items, wall/exit sprites, and
   * exit accessibility checks.
   *
   * @param g the graphics context
   * @param tileWH pixel size of each tile
   */
  public void renderMaze(Graphics g, int tileWH) {
    for (int row = 0; row < rowColAmount; row++) {
      for (int col = 0; col < rowColAmount; col++) {
        Tile tile = tileArr[col][row];

        // Frustum culling — only render visible tiles
        if (tile.getMinX() <= -tileWH
            || tile.getMaxX() >= screenWidth + tileWH
            || tile.getMinY() <= -tileWH
            || tile.getMaxY() >= screenHeight + tileWH) {
          continue;
        }

        // Grass background
        String grassVariant =
            (col % 2 == 0) ? "GrassPassage_" + tile.getPassageImageId() : "GrassPassage_0";
        g.drawImage(
            getImage(grassVariant),
            tile.getMinX(),
            tile.getMinY(),
            tile.getSize(),
            tile.getSize(),
            null);

        // Key item animation
        if ("Key".equals(tile.getImageString())) {
          BufferedImage keyFrame =
              (keysOnMap.peek() == tile)
                  ? assetManager.getBlinkingKeyFrame()
                  : assetManager.getKeyFrame();
          if (keyFrame != null) {
            g.drawImage(
                keyFrame, tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
          }
        }

        // Tile sprite (wall, exit, or passage — passage returns null)
        BufferedImage tileImage = getImage(tile.getImageString());
        if (tileImage != null) {
          g.drawImage(
              tileImage, tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
        }

        // Unlock exit when all keys collected
        if (tile instanceof TileExit && keyCount >= keysRequired) {
          ((TileExit) tile).setAccessible(true);
        }
      }
    }
  }

  /**
   * Looks up a preloaded image by name.
   *
   * @param imageName the cache key
   * @return the image, or {@code null}
   */
  public BufferedImage getImage(String imageName) {
    return assetManager.getPreloadedImage(imageName);
  }

  /**
   * Renders a modern heads-up display showing key count, level, elapsed time, and key-removal
   * countdown with gradient background and accent styling.
   *
   * @param g the graphics context
   * @param p the player (reserved for future HUD info)
   * @param level the current level number
   */
  public void renderHUD(Graphics g, Player p, int level) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    int hudH = HUD_HEIGHT;

    // Gradient background
    g2.setPaint(
        new GradientPaint(0, 0, new Color(12, 12, 35, 240), 0, hudH, new Color(22, 22, 50, 240)));
    g2.fillRect(0, 0, screenWidth, hudH);

    // Bottom accent line
    g2.setColor(new Color(0, 200, 200, 150));
    g2.fillRect(0, hudH - 2, screenWidth, 2);

    int pad = 20;
    int topY = 19;
    int botY = 39;
    Font labelFont = new Font("Dialog", Font.PLAIN, 11);
    Font valueFont = new Font("Dialog", Font.BOLD, 16);
    Color labelColor = new Color(140, 140, 160);

    // ---- Left: Keys ----
    g2.setFont(labelFont);
    g2.setColor(labelColor);
    g2.drawString("KEYS", pad, topY);
    g2.setFont(valueFont);
    g2.setColor(new Color(0, 230, 230));
    g2.drawString(keyCount + " / " + keysRequired, pad, botY);

    // ---- Center: Level ----
    g2.setFont(labelFont);
    g2.setColor(labelColor);
    FontMetrics lfm = g2.getFontMetrics();
    String lvlLabel = "LEVEL";
    g2.drawString(lvlLabel, (screenWidth - lfm.stringWidth(lvlLabel)) / 2, topY);
    g2.setFont(valueFont);
    g2.setColor(Color.WHITE);
    FontMetrics vfm = g2.getFontMetrics();
    String lvlVal = String.valueOf(level);
    g2.drawString(lvlVal, (screenWidth - vfm.stringWidth(lvlVal)) / 2, botY);

    // ---- Right area ----
    DecimalFormat df = new DecimalFormat("0.0");

    // Key-removal countdown (far right)
    String countdownVal = df.format(Math.max(0, timeUntilKeyRemoval)) + "s";
    double ratio = timeUntilKeyRemoval / KEY_REMOVAL_INTERVAL;
    Color countdownColor;
    if (ratio < 0.3) {
      countdownColor = new Color(255, 80, 80);
    } else if (ratio < 0.6) {
      countdownColor = new Color(255, 220, 80);
    } else {
      countdownColor = new Color(0, 230, 230);
    }

    g2.setFont(labelFont);
    FontMetrics sfm = g2.getFontMetrics();
    String cdLabel = "KEY TIMER";
    int cdLabelX = screenWidth - pad - sfm.stringWidth(cdLabel);
    g2.setColor(labelColor);
    g2.drawString(cdLabel, cdLabelX, topY);
    g2.setFont(valueFont);
    g2.setColor(countdownColor);
    FontMetrics cvfm = g2.getFontMetrics();
    g2.drawString(countdownVal, screenWidth - pad - cvfm.stringWidth(countdownVal), botY);

    // Countdown progress bar
    int barW = 80;
    int barH = 3;
    int barX = screenWidth - pad - barW;
    int barY = botY + 4;
    g2.setColor(new Color(40, 40, 60));
    g2.fillRect(barX, barY, barW, barH);
    int filledW = (int) (barW * Math.max(0, Math.min(1, ratio)));
    g2.setColor(countdownColor);
    g2.fillRect(barX, barY, filledW, barH);

    // Elapsed time (left of key timer)
    g2.setFont(labelFont);
    String timeLabel = "TIME";
    int timeX = cdLabelX - 110;
    g2.setColor(labelColor);
    g2.drawString(timeLabel, timeX, topY);
    g2.setFont(valueFont);
    g2.setColor(new Color(200, 200, 220));
    g2.drawString(df.format(timeTaken) + "s", timeX, botY);
  }

  /**
   * Scrolls the maze horizontally and queues the appropriate player walking animation.
   *
   * @param g the graphics context
   * @param numOfRowCol grid size
   * @param dir scroll direction (negative = east, positive = west)
   */
  public void moveMazeX(Graphics g, int numOfRowCol, int dir) {
    for (int row = 0; row < numOfRowCol; row++) {
      for (int col = 0; col < numOfRowCol; col++) {
        Tile tile = tileArr[col][row];
        tile.setMinX(tile.getMinX() + dir);
      }
    }

    if (nextPlayerAnimation.size() <= MAX_ANIMATION_STACK_SIZE) {
      String direction = (dir < 0) ? "dogEast" : "dogWest";
      int frameCount = 7;
      for (int i = 0; i < frameCount; i++) {
        nextPlayerAnimation.push(getImage(direction + i));
      }
    }
  }

  /**
   * Scrolls the maze vertically and queues the appropriate player walking animation.
   *
   * @param g the graphics context
   * @param numOfRowCol grid size
   * @param dir scroll direction (positive = north, negative = south)
   */
  public void moveMazeY(Graphics g, int numOfRowCol, int dir) {
    for (int row = 0; row < numOfRowCol; row++) {
      for (int col = 0; col < numOfRowCol; col++) {
        Tile tile = tileArr[col][row];
        tile.setMinY(tile.getMinY() + dir);
      }
    }

    if (nextPlayerAnimation.size() <= MAX_ANIMATION_STACK_SIZE) {
      String direction = (dir > 0) ? "dogNorth" : "dogSouth";
      int frameCount = 6;
      for (int i = 0; i < frameCount; i++) {
        nextPlayerAnimation.push(getImage(direction + i));
      }
    }
  }

  /**
   * Determines which tile the player is currently standing on.
   *
   * @param playerX player x-coordinate
   * @param playerY player y-coordinate
   * @param playerSize player sprite size
   * @param tileWH tile width/height
   * @param tileBorder tile border inset
   * @return a two-element array {@code [row, col]}, or {@code null}
   */
  public int[] getTile(int playerX, int playerY, int playerSize, int tileWH, int tileBorder) {
    Tilemap lookup = new Tilemap(tileWH, tileBorder, rowColAmount);
    int centreX = playerX + (playerSize / 2);
    int centreY = playerY + (playerSize / 2);

    return lookup.getCurrentTile(centreX, centreY);
  }

  /**
   * Checks collision at the given tile position and handles game events (key pickup, exit check,
   * wall blocking).
   *
   * @param current the tile position as {@code [row, col]}
   * @param game the game instance for state callbacks
   * @return true if the player can move to this tile
   */
  public boolean checkCollision(int[] current, MazeGame game) {
    Tile tile = tileArr[current[0]][current[1]];

    if (tile instanceof TileWall) {
      return false;
    } else if (tile instanceof TileExit) {
      if (((TileExit) tile).getAccessible()) {
        game.setGameState(false, "Next Level");
      } else {
        int remaining = keysRequired - keyCount;
        playerMessage = "The door is locked. Find " + remaining + " more keys.";
        activatedAt = System.currentTimeMillis();
        setPlayerMessage(true);
      }
    } else {
      TilePassage passage = (TilePassage) tile;
      if (passage.hasItem()) {
        passage.setItem(false);
        keyCount++;
      }
    }
    return true;
  }

  /** Advances the player sprite animation by one frame. */
  public void updateFrames() {
    if (nextPlayerAnimation.size() > 1) {
      playerImg = nextPlayerAnimation.pop();
    }
  }

  /**
   * Renders the player sprite at the centre of the screen, plus any active player message.
   *
   * @param g the graphics context
   * @param player the player instance
   * @param spriteSize the sprite rendering size
   */
  public void renderPlayer(Graphics g, Player player, int spriteSize) {
    g.drawImage(playerImg, screenWidthHalf, screenHeightHalf, spriteSize, spriteSize, null);

    if (isMessageVisible()) {
      g.setColor(Color.WHITE);
      g.setFont(new Font("Serif", Font.PLAIN, MESSAGE_FONT_SIZE));
      FontMetrics fm = g.getFontMetrics();
      int halfTextWidth = fm.stringWidth(playerMessage) / 2;
      g.drawString(
          playerMessage, (screenWidthHalf - halfTextWidth) + tileWidth / 2, screenHeightHalf);
    }
  }

  /** Sets whether a player message should be displayed. */
  public void setPlayerMessage(boolean displayMsg) {
    this.displayMsg = displayMsg;
  }

  /** Returns true if the player message is still within its display duration. */
  public boolean isMessageVisible() {
    long activeFor = System.currentTimeMillis() - activatedAt;
    return activeFor >= 0 && activeFor <= MESSAGE_DURATION_MS;
  }

  /** Returns the player's starting x-coordinate. */
  public int getStartingX() {
    return startingX;
  }

  /** Returns the player's starting y-coordinate. */
  public int getStartingY() {
    return startingY;
  }
}
