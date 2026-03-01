package mazegame;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
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
import java.util.Random;
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

  // RuneScape-style overhead speech bubble
  private static final int QUIP_DURATION_MS = 3500;
  private static final int QUIP_MIN_INTERVAL_MS = 12_000;
  private static final int QUIP_MAX_INTERVAL_MS = 25_000;
  private static final Color QUIP_COLOR = new Color(255, 255, 0); // RS yellow
  private static final int QUIP_FONT_SIZE = 14;

  /** Idle quips Wesley says while exploring. Dog-themed, slightly humorous. */
  private static final String[] IDLE_QUIPS = {
    "*sniff sniff* I smell a key!",
    "Dad Aston would be so proud of me right now",
    "Aston said there'd be treats...",
    "These walls all look the same to me",
    "I'm a good boy on a mission",
    "Who built this maze anyway?!",
    "My paws are getting tired...",
    "Is that... a squirrel?! Oh wait, just a wall.",
    "Aston — that's Dad — better have snacks when I get out",
    "*tail wagging intensifies*",
    "Left or right? Eh, I'll follow my nose",
    "Aston owes me belly rubs for this",
    "I bet Sasso couldn't do this",
    "This maze smells like adventure",
    "Are we there yet?",
    "I could really go for a nap right now",
    "Note to self: don't chase tail in a maze",
    "*panting* Water break?",
  };

  private static final String[] KEY_PICKUP_QUIPS = {
    "Ooh shiny! Dad Aston loves shiny things",
    "Got one! I'm basically a treasure hunter",
    "*happy bark*",
    "Keys collected, belly rubs pending",
    "One step closer to freedom!",
    "Aston would be impressed",
    "That's what a good boy does",
  };

  private static final String[] DOOR_LOCKED_QUIPS = {
    "Hmm, needs more keys. Classic.",
    "The door said no. Rude.",
    "Locked?! Who does that to a dog?",
    "Aston never locks me out... well, sometimes",
  };

  private static final String[] ALL_KEYS_QUIPS = {
    "I got them all! Where's the exit?!",
    "Full key collection! Dad Aston would cry tears of joy",
    "Time to find that door!",
  };

  /** Quips Wesley says when a new level starts. */
  private static final String[] LEVEL_START_QUIPS = {
    "Alright, new maze! Let's do this!",
    "*sniff sniff* Fresh maze smell!",
    "Dad Aston believes in me. I think.",
    "Okay paws, don't fail me now",
    "Another one?! I'm not even tired... much",
    "Woof! Adventure time!",
    "I was born for this. Literally. I'm a dog.",
    "This one looks tricky... said no good boy ever",
    "Let's gooo! *tail helicopter*",
    "Aston if you're watching — this one's for you!",
  };

  // Confetti particle system
  private static final int CONFETTI_COUNT = 60;
  private static final int CONFETTI_DURATION_MS = 1800;
  private static final Color[] CONFETTI_COLORS = {
    new Color(255, 215, 0), // gold
    new Color(196, 149, 106), // warm accent
    new Color(255, 120, 80), // coral
    new Color(120, 220, 160), // mint
    new Color(100, 180, 255), // sky blue
    new Color(255, 255, 255), // white
  };

  private final BufferedImage view;
  private final int screenWidth;
  private final int screenHeight;
  private final int screenWidthHalf;
  private final int screenHeightHalf;
  private final int rowColAmount;
  private final int keysRequired;
  private final AssetManager assetManager;
  private final AudioManager audioManager;
  private final String skinPrefix;

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

  // Key-collection visual feedback
  private long keyCollectFlashStart = Long.MIN_VALUE;
  private static final int KEY_FLASH_DURATION_MS = 600;
  private static final Color KEY_FLASH_COLOR = new Color(196, 149, 106, 80);

  // Locked-door visual feedback
  private long lockedDoorFlashStart = Long.MIN_VALUE;
  private static final int LOCKED_FLASH_DURATION_MS = 300;
  private static final Color LOCKED_FLASH_COLOR = new Color(255, 80, 80, 50);

  private double timeTaken;
  private double timeUntilKeyRemoval = KEY_REMOVAL_INTERVAL;
  private long wallClockAtResumeMs;
  private double accumulatedGameSec;
  private double gameSecAtLastRemoval;
  private BufferedImage playerImg;
  private Stack<BufferedImage> nextPlayerAnimation = new Stack<>();

  // Speech bubble state
  private final Random quipRng = new Random();
  private String currentQuip = "";
  private long quipActivatedAt = Long.MIN_VALUE;
  private long nextQuipTime;

  // Confetti state
  private long confettiStartTime = Long.MIN_VALUE;
  private double[] confettiX;
  private double[] confettiY;
  private double[] confettiVx;
  private double[] confettiVy;
  private int[] confettiColorIdx;
  private double[] confettiRotation;

  /**
   * Creates a new renderer for the game view.
   *
   * @param screenHeight the window height in pixels
   * @param screenWidth the window width in pixels
   * @param rowColAmount the maze grid size (rows and columns)
   * @param tileWH the pixel size of each tile
   * @param assetManager the shared asset manager for image lookup
   * @param audioManager the audio manager for sound effects
   * @param settings the game settings (used for skin prefix)
   * @param game the game instance for state callbacks
   */
  public Renderer(
      int screenHeight,
      int screenWidth,
      int rowColAmount,
      int tileWH,
      AssetManager assetManager,
      AudioManager audioManager,
      GameSettings settings,
      MazeGame game) {
    this.screenWidth = screenWidth;
    this.screenHeight = screenHeight;
    this.tileWidth = tileWH;
    this.rowColAmount = rowColAmount;
    this.assetManager = assetManager;
    this.audioManager = audioManager;
    this.skinPrefix = settings.getSpritePrefix();

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
    playerImg = assetManager.getPreloadedImage(skinPrefix + "East0");

    view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);

    gameTimer =
        new Timer(
            TIMER_CHECK_MS,
            (ActionEvent e) -> {
              long nowMs = System.currentTimeMillis();
              double totalGameSec = accumulatedGameSec + (nowMs - wallClockAtResumeMs) / 1000.0;
              timeTaken = totalGameSec;

              // All keys collected — nothing left to remove
              if (keyCount >= keysRequired || keysOnMap.isEmpty()) {
                return;
              }

              double sinceLastRemoval = totalGameSec - gameSecAtLastRemoval;
              timeUntilKeyRemoval = KEY_REMOVAL_INTERVAL - sinceLastRemoval;

              if (timeUntilKeyRemoval <= 0) {
                gameSecAtLastRemoval = totalGameSec;
                keyRemovalTimer--;
                TilePassage removedKey = (TilePassage) keysOnMap.pop();
                removedKey.setItem(false);
                audioManager.play(AudioManager.Sound.KEY_VANISHED);

                if (keysOnMap.size() < (keysRequired - keyCount)) {
                  audioManager.play(AudioManager.Sound.LEVEL_FAILED);
                  game.setGameState(false, "Level Failed");
                  gameTimer.stop();
                }
              } else if (timeUntilKeyRemoval <= 1.5 && timeUntilKeyRemoval > 1.3) {
                audioManager.play(AudioManager.Sound.LOW_TIME_WARNING);
              }
            });

    // Schedule first idle quip
    nextQuipTime =
        System.currentTimeMillis()
            + QUIP_MIN_INTERVAL_MS
            + quipRng.nextInt(QUIP_MAX_INTERVAL_MS - QUIP_MIN_INTERVAL_MS);
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
        new GradientPaint(0, 0, new Color(26, 26, 26, 240), 0, hudH, new Color(34, 30, 28, 240)));
    g2.fillRect(0, 0, screenWidth, hudH);

    // Bottom accent line
    g2.setColor(new Color(196, 149, 106, 150));
    g2.fillRect(0, hudH - 2, screenWidth, 2);

    int pad = 20;
    int topY = 19;
    int botY = 39;
    Font labelFont = new Font("Dialog", Font.PLAIN, 11);
    Font valueFont = new Font("Dialog", Font.BOLD, 16);
    Color labelColor = new Color(160, 145, 130);

    // ---- Left: Keys ----
    g2.setFont(labelFont);
    g2.setColor(labelColor);
    g2.drawString("KEYS", pad, topY);
    g2.setFont(valueFont);
    g2.setColor(new Color(196, 149, 106));
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
      countdownColor = new Color(196, 149, 106);
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
    g2.setColor(new Color(50, 44, 40));
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
    g2.setColor(new Color(220, 216, 210));
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
      String direction = (dir < 0) ? skinPrefix + "East" : skinPrefix + "West";
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
      String direction = (dir > 0) ? skinPrefix + "North" : skinPrefix + "South";
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
        spawnConfetti();
        audioManager.play(AudioManager.Sound.DOOR_OPEN);
        game.setGameState(false, "Next Level");
      } else {
        int remaining = keysRequired - keyCount;
        playerMessage = "The door is locked. Find " + remaining + " more keys.";
        activatedAt = System.currentTimeMillis();
        setPlayerMessage(true);
        lockedDoorFlashStart = System.currentTimeMillis();
        triggerQuip(randomQuip(DOOR_LOCKED_QUIPS));
        audioManager.play(AudioManager.Sound.LOCKED_DOOR);
      }
    } else {
      TilePassage passage = (TilePassage) tile;
      if (passage.hasItem()) {
        passage.setItem(false);
        keyCount++;
        keyCollectFlashStart = System.currentTimeMillis();
        audioManager.play(AudioManager.Sound.KEY_PICKUP);
        if (keyCount >= keysRequired) {
          triggerQuip(randomQuip(ALL_KEYS_QUIPS));
        } else {
          triggerQuip(randomQuip(KEY_PICKUP_QUIPS));
        }
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

    renderActionFeedback(g);

    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Confetti burst (renders behind speech bubbles)
    renderConfetti(g2);

    // Idle speech bubble tick & render
    tickIdleQuip();
    renderQuip(g2);

    if (isMessageVisible()) {

      long elapsed = System.currentTimeMillis() - activatedAt;
      float fadeAlpha = 1f;
      int fadeStart = MESSAGE_DURATION_MS - 800;
      if (elapsed > fadeStart) {
        fadeAlpha = 1f - (float) (elapsed - fadeStart) / 800f;
        fadeAlpha = Math.max(0f, Math.min(1f, fadeAlpha));
      }

      Font msgFont = new Font("Dialog", Font.BOLD, MESSAGE_FONT_SIZE);
      g2.setFont(msgFont);
      FontMetrics fm = g2.getFontMetrics();
      int textW = fm.stringWidth(playerMessage);
      int textH = fm.getHeight();

      int pillPadX = 18;
      int pillPadY = 10;
      int pillW = textW + pillPadX * 2;
      int pillH = textH + pillPadY * 2;
      int pillX = screenWidthHalf + tileWidth / 2 - pillW / 2;
      int pillY = screenHeightHalf - pillH - 8;
      int arc = 12;

      // Pill background shadow
      Composite oldComp = g2.getComposite();
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha * 0.35f));
      g2.setColor(Color.BLACK);
      g2.fillRoundRect(pillX + 2, pillY + 2, pillW, pillH, arc, arc);

      // Pill background
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha * 0.85f));
      g2.setColor(new Color(30, 26, 24));
      g2.fillRoundRect(pillX, pillY, pillW, pillH, arc, arc);

      // Accent border
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha * 0.7f));
      g2.setColor(new Color(196, 149, 106));
      g2.setStroke(new java.awt.BasicStroke(1.5f));
      g2.drawRoundRect(pillX, pillY, pillW, pillH, arc, arc);
      g2.setStroke(new java.awt.BasicStroke(1f));

      // Text
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
      int textX = pillX + pillPadX;
      int textY = pillY + pillPadY + fm.getAscent();
      g2.setColor(new Color(240, 236, 232));
      g2.drawString(playerMessage, textX, textY);

      g2.setComposite(oldComp);
    }
  }

  /**
   * Renders visual feedback overlays for key collection (golden flash + floating "+1") and locked
   * door attempts (red flash).
   */
  private void renderActionFeedback(Graphics g) {
    long now = System.currentTimeMillis();
    Graphics2D g2 = (Graphics2D) g;

    // Key collection: golden flash overlay + floating "+1 Key" text
    long keyElapsed = now - keyCollectFlashStart;
    if (keyElapsed >= 0 && keyElapsed < KEY_FLASH_DURATION_MS) {
      float progress = (float) keyElapsed / KEY_FLASH_DURATION_MS;
      float alpha = 1.0f - progress; // fade out

      // Screen flash
      g2.setColor(
          new Color(
              KEY_FLASH_COLOR.getRed() / 255f,
              KEY_FLASH_COLOR.getGreen() / 255f,
              KEY_FLASH_COLOR.getBlue() / 255f,
              alpha * 0.45f));
      g2.fillRect(0, 0, screenWidth, screenHeight);

      // Bright ring around the player tile
      int ringX = screenWidthHalf - 4;
      int ringY = screenHeightHalf - 4;
      int ringSize = tileWidth + 8;
      g2.setColor(new Color(255, 220, 160, Math.max(0, Math.min(255, (int) (alpha * 200)))));
      g2.setStroke(new java.awt.BasicStroke(3f));
      g2.drawRoundRect(ringX, ringY, ringSize, ringSize, 8, 8);
      g2.setStroke(new java.awt.BasicStroke(1f));

      // Floating "+1 Key" text rising upward
      int floatOffset = (int) (progress * 40);
      int textAlpha = (int) (alpha * 255);
      g2.setFont(new Font("Dialog", Font.BOLD, 20));
      g2.setColor(new Color(255, 220, 160, Math.max(0, Math.min(255, textAlpha))));
      String collectText = "+1 Key";
      FontMetrics fm = g2.getFontMetrics();
      int textX = screenWidthHalf + tileWidth / 2 - fm.stringWidth(collectText) / 2;
      int textY = screenHeightHalf - 12 - floatOffset;
      // Text shadow
      g2.setColor(new Color(0, 0, 0, Math.max(0, Math.min(255, textAlpha / 2))));
      g2.drawString(collectText, textX + 1, textY + 1);
      g2.setColor(new Color(255, 220, 160, Math.max(0, Math.min(255, textAlpha))));
      g2.drawString(collectText, textX, textY);
    }

    // Locked door: red flash overlay
    long lockElapsed = now - lockedDoorFlashStart;
    if (lockElapsed >= 0 && lockElapsed < LOCKED_FLASH_DURATION_MS) {
      float progress = (float) lockElapsed / LOCKED_FLASH_DURATION_MS;
      float alpha = 1.0f - progress;

      g2.setColor(
          new Color(
              LOCKED_FLASH_COLOR.getRed() / 255f,
              LOCKED_FLASH_COLOR.getGreen() / 255f,
              LOCKED_FLASH_COLOR.getBlue() / 255f,
              alpha * 0.3f));
      g2.fillRect(0, 0, screenWidth, screenHeight);
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

  // ---------------------------------------------------------------------------
  // Speech bubble helpers
  // ---------------------------------------------------------------------------

  /** Shows a RuneScape-style overhead quip above Wesley. */
  private void triggerQuip(String text) {
    currentQuip = text;
    quipActivatedAt = System.currentTimeMillis();
  }

  /** Triggers a random level-start quip (called externally when a new level begins). */
  public void triggerStartQuip() {
    triggerQuip(randomQuip(LEVEL_START_QUIPS));
  }

  /** Picks a random quip from the given array. */
  private String randomQuip(String[] pool) {
    return pool[quipRng.nextInt(pool.length)];
  }

  /** Checks whether it's time for an idle quip and triggers one if so. */
  private void tickIdleQuip() {
    long now = System.currentTimeMillis();
    if (now >= nextQuipTime && now - quipActivatedAt > QUIP_DURATION_MS) {
      triggerQuip(randomQuip(IDLE_QUIPS));
      nextQuipTime =
          now + QUIP_MIN_INTERVAL_MS + quipRng.nextInt(QUIP_MAX_INTERVAL_MS - QUIP_MIN_INTERVAL_MS);
    }
  }

  /** Returns true if the overhead quip text is still visible. */
  private boolean isQuipVisible() {
    long elapsed = System.currentTimeMillis() - quipActivatedAt;
    return elapsed >= 0 && elapsed <= QUIP_DURATION_MS;
  }

  /**
   * Renders the overhead quip in RuneScape style: yellow text with a black drop shadow, no
   * background. Text floats above the player sprite and fades out near the end.
   */
  private void renderQuip(Graphics2D g2) {
    if (!isQuipVisible()) return;

    long elapsed = System.currentTimeMillis() - quipActivatedAt;
    float fadeAlpha = 1f;
    int fadeStart = QUIP_DURATION_MS - 600;
    if (elapsed > fadeStart) {
      fadeAlpha = 1f - (float) (elapsed - fadeStart) / 600f;
      fadeAlpha = Math.max(0f, Math.min(1f, fadeAlpha));
    }

    // Slight upward drift over lifetime
    int floatOffset = (int) ((elapsed / (double) QUIP_DURATION_MS) * 8);

    Font quipFont = new Font("Dialog", Font.BOLD, QUIP_FONT_SIZE);
    g2.setFont(quipFont);
    FontMetrics fm = g2.getFontMetrics();
    int textW = fm.stringWidth(currentQuip);
    int textX = screenWidthHalf + tileWidth / 2 - textW / 2;
    int textY = screenHeightHalf - 14 - floatOffset;

    Composite oldComp = g2.getComposite();

    // Black drop shadow (1px offset)
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeAlpha));
    g2.setColor(Color.BLACK);
    g2.drawString(currentQuip, textX + 1, textY + 1);

    // Yellow main text
    g2.setColor(QUIP_COLOR);
    g2.drawString(currentQuip, textX, textY);

    g2.setComposite(oldComp);
  }

  // ---------------------------------------------------------------------------
  // Confetti helpers
  // ---------------------------------------------------------------------------

  /** Spawns a burst of confetti particles centred on the player. */
  private void spawnConfetti() {
    confettiStartTime = System.currentTimeMillis();
    confettiX = new double[CONFETTI_COUNT];
    confettiY = new double[CONFETTI_COUNT];
    confettiVx = new double[CONFETTI_COUNT];
    confettiVy = new double[CONFETTI_COUNT];
    confettiColorIdx = new int[CONFETTI_COUNT];
    confettiRotation = new double[CONFETTI_COUNT];
    Random rng = quipRng;
    for (int i = 0; i < CONFETTI_COUNT; i++) {
      confettiX[i] = screenWidthHalf + tileWidth / 2.0;
      confettiY[i] = screenHeightHalf + tileWidth / 2.0;
      double angle = rng.nextDouble() * 2 * Math.PI;
      double speed = 2.0 + rng.nextDouble() * 5.0;
      confettiVx[i] = Math.cos(angle) * speed;
      confettiVy[i] = Math.sin(angle) * speed - 3.0; // bias upward
      confettiColorIdx[i] = rng.nextInt(CONFETTI_COLORS.length);
      confettiRotation[i] = rng.nextDouble() * 360;
    }
  }

  /** Renders active confetti particles with gravity and fade-out. */
  private void renderConfetti(Graphics2D g2) {
    long elapsed = System.currentTimeMillis() - confettiStartTime;
    if (elapsed < 0 || elapsed > CONFETTI_DURATION_MS || confettiX == null) return;

    float progress = (float) elapsed / CONFETTI_DURATION_MS;
    float alpha = Math.max(0f, 1f - progress * progress); // quadratic fade
    double gravity = 0.12;
    double t = elapsed / 16.0; // time in ~frames

    Composite oldComp = g2.getComposite();
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

    for (int i = 0; i < CONFETTI_COUNT; i++) {
      double px = confettiX[i] + confettiVx[i] * t;
      double py = confettiY[i] + confettiVy[i] * t + 0.5 * gravity * t * t;
      g2.setColor(CONFETTI_COLORS[confettiColorIdx[i]]);
      // Small rectangle rotated to look like confetti
      int size = 4 + (i % 3);
      g2.fillRect((int) px, (int) py, size, size / 2 + 1);
    }

    g2.setComposite(oldComp);
  }
}
