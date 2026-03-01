package mazegame;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Custom-painted level-selection screen matching the dark-themed aesthetic of {@link
 * MainMenuPanel}. Displays a scrollable grid of level cards with status indicators.
 */
public class LevelSelectionPanel extends JPanel {

  private static final Color BG_TOP = new Color(26, 26, 26);
  private static final Color BG_BOTTOM = new Color(34, 30, 28);
  private static final Color TITLE_COLOR = new Color(240, 236, 232);
  private static final Color TITLE_SHADOW = new Color(80, 60, 40);
  private static final Color CARD_BG = new Color(50, 44, 40);
  private static final Color CARD_BORDER = new Color(100, 85, 70);
  private static final Color CARD_HOVER_BG = new Color(65, 55, 48);
  private static final Color TEXT_PRIMARY = new Color(240, 236, 232);
  private static final Color TEXT_DIM = new Color(160, 145, 130);

  private static final Color STATUS_COMPLETED = new Color(0, 200, 100);
  private static final Color STATUS_CURRENT = new Color(220, 180, 30);
  private static final Color STATUS_LOCKED = new Color(100, 90, 80);

  private static final Color BTN_BG = new Color(50, 44, 40);
  private static final Color BTN_BORDER = new Color(196, 149, 106);
  private static final Color BTN_HOVER_BG = new Color(100, 75, 50);
  private static final Color BTN_TEXT = new Color(220, 216, 210);

  private static final int HEADER_HEIGHT = 70;
  private static final int HEADER_BTN_WIDTH = 110;
  private static final int HEADER_BTN_HEIGHT = 34;
  private static final int CARD_COLS = 5;
  private static final int CARD_PAD = 12;
  private static final int CARD_ARC = 10;
  private static final int TITLE_FONT_SIZE = 28;
  private static final int CARD_FONT_SIZE = 14;
  private static final int CARD_SMALL_FONT_SIZE = 11;
  private static final int SCROLL_SPEED = 30;

  private final String[] levelData;
  private final MazeGame game;
  private final Runnable onMainMenu;
  private final Runnable onReset;
  private int hoveredCard = -1;
  private int hoveredHeaderBtn = -1; // 0 = back, 1 = reset
  private int scrollOffset = 0;
  private int maxScroll = 0;
  private int firstIncompleteLevel = -1;

  /**
   * Creates a new level-selection panel.
   *
   * @param levelData the saved level data (index 0 is header row)
   * @param game the game instance for launching levels
   * @param onMainMenu callback to return to main menu
   * @param onReset callback to reset progress and refresh
   */
  public LevelSelectionPanel(
      String[] levelData, MazeGame game, Runnable onMainMenu, Runnable onReset) {
    this.levelData = levelData;
    this.game = game;
    this.onMainMenu = onMainMenu;
    this.onReset = onReset;
    setOpaque(true);
    setBackground(BG_TOP);
    setFocusable(true);

    // Find first incomplete level
    for (int i = 1; i < levelData.length; i++) {
      String[] parts = levelData[i].split(",");
      if (parts[1].equalsIgnoreCase("incomplete")) {
        firstIncompleteLevel = i;
        break;
      }
    }

    MouseAdapter mouseHandler =
        new MouseAdapter() {
          @Override
          public void mouseMoved(MouseEvent e) {
            updateHover(e.getX(), e.getY());
          }

          @Override
          public void mouseClicked(MouseEvent e) {
            handleClick(e.getX(), e.getY());
          }

          @Override
          public void mouseExited(MouseEvent e) {
            if (hoveredCard != -1 || hoveredHeaderBtn != -1) {
              hoveredCard = -1;
              hoveredHeaderBtn = -1;
              setCursor(Cursor.getDefaultCursor());
              repaint();
            }
          }

          @Override
          public void mouseWheelMoved(MouseWheelEvent e) {
            scrollOffset =
                Math.max(
                    0, Math.min(maxScroll, scrollOffset + e.getWheelRotation() * SCROLL_SPEED));
            repaint();
          }
        };
    addMouseListener(mouseHandler);
    addMouseMotionListener(mouseHandler);
    addMouseWheelListener(mouseHandler);

    // ESC key binding
    InputHandler.bindKey(this, KeyEvent.VK_ESCAPE, "Menu", false, evt -> onMainMenu.run());
  }

  @Override
  public Dimension getPreferredSize() {
    java.awt.Container parent = getParent();
    if (parent != null) {
      return parent.getSize();
    }
    return new Dimension(650, 650);
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    Graphics2D g = (Graphics2D) graphics;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    int w = getWidth();
    int h = getHeight();

    // Gradient background
    g.setPaint(new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM));
    g.fillRect(0, 0, w, h);

    // Grid lines
    g.setColor(new Color(255, 255, 255, 6));
    for (int x = 0; x < w; x += 40) {
      g.drawLine(x, 0, x, h);
    }
    for (int y = 0; y < h; y += 40) {
      g.drawLine(0, y, w, y);
    }

    paintHeader(g, w);

    // Clip card region
    g.clipRect(0, HEADER_HEIGHT, w, h - HEADER_HEIGHT);
    paintCards(g, w, h);
    g.setClip(null);
  }

  private void paintHeader(Graphics2D g, int panelWidth) {
    // Title
    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    g.setFont(titleFont);
    FontMetrics fm = g.getFontMetrics();
    String title = "Level Selection";
    int titleX = (panelWidth - fm.stringWidth(title)) / 2;
    int titleY = HEADER_HEIGHT / 2 + fm.getAscent() / 2 - 2;
    g.setColor(TITLE_SHADOW);
    g.drawString(title, titleX + 1, titleY + 1);
    g.setColor(TITLE_COLOR);
    g.drawString(title, titleX, titleY);

    // Header buttons: Back (left), Reset (right)
    Font btnFont = new Font("Dialog", Font.PLAIN, 13);
    g.setFont(btnFont);
    FontMetrics bfm = g.getFontMetrics();

    // Back button
    int backX = CARD_PAD;
    int btnY = (HEADER_HEIGHT - HEADER_BTN_HEIGHT) / 2;
    paintHeaderButton(g, "Back [Esc]", backX, btnY, bfm, hoveredHeaderBtn == 0);

    // Reset button
    int resetX = panelWidth - CARD_PAD - HEADER_BTN_WIDTH;
    paintHeaderButton(g, "Reset", resetX, btnY, bfm, hoveredHeaderBtn == 1);
  }

  private void paintHeaderButton(
      Graphics2D g, String label, int x, int y, FontMetrics fm, boolean hovered) {
    RoundRectangle2D.Double rect =
        new RoundRectangle2D.Double(x, y, HEADER_BTN_WIDTH, HEADER_BTN_HEIGHT, 8, 8);
    g.setColor(hovered ? BTN_HOVER_BG : BTN_BG);
    g.fill(rect);
    g.setColor(hovered ? TITLE_COLOR : BTN_BORDER);
    g.draw(rect);
    g.setColor(hovered ? Color.WHITE : BTN_TEXT);
    g.drawString(
        label,
        x + (HEADER_BTN_WIDTH - fm.stringWidth(label)) / 2,
        y + (HEADER_BTN_HEIGHT + fm.getAscent()) / 2 - 2);
  }

  private void paintCards(Graphics2D g, int panelWidth, int panelHeight) {
    int levelCount = levelData.length - 1;
    int cardW = (panelWidth - CARD_PAD * (CARD_COLS + 1)) / CARD_COLS;
    int cardH = cardW + 20; // slightly taller than wide
    int rows = (levelCount + CARD_COLS - 1) / CARD_COLS;
    int totalHeight = rows * (cardH + CARD_PAD) + CARD_PAD;
    maxScroll = Math.max(0, totalHeight - (panelHeight - HEADER_HEIGHT));

    Font numFont = new Font("Dialog", Font.BOLD, CARD_FONT_SIZE + 6);
    Font statusFont = new Font("Dialog", Font.PLAIN, CARD_SMALL_FONT_SIZE);
    Font timeFont = new Font("Dialog", Font.PLAIN, CARD_SMALL_FONT_SIZE);

    for (int i = 0; i < levelCount; i++) {
      int level = i + 1;
      int col = i % CARD_COLS;
      int row = i / CARD_COLS;
      int cx = CARD_PAD + col * (cardW + CARD_PAD);
      int cy = HEADER_HEIGHT + CARD_PAD + row * (cardH + CARD_PAD) - scrollOffset;

      // Parse level data
      String[] parts = levelData[level].split(",");
      String statusStr = parts[1];
      double bestTime = Double.parseDouble(parts[2]);

      int category; // 0 = locked, 1 = completed, 2 = current
      if (statusStr.equalsIgnoreCase("completed")) {
        category = 1;
      } else if (level == firstIncompleteLevel) {
        category = 2;
      } else {
        category = 0;
      }

      boolean hovered = (i == hoveredCard);
      Color borderColor;
      String statusLabel;

      switch (category) {
        case 1:
          borderColor = STATUS_COMPLETED;
          statusLabel = "\u2713 Completed";
          break;
        case 2:
          borderColor = STATUS_CURRENT;
          statusLabel = "\u25B6 Current";
          break;
        default:
          borderColor = STATUS_LOCKED;
          statusLabel = "\uD83D\uDD12 Locked";
          break;
      }

      // Card background
      RoundRectangle2D.Double card =
          new RoundRectangle2D.Double(cx, cy, cardW, cardH, CARD_ARC, CARD_ARC);
      g.setColor(hovered && category != 0 ? CARD_HOVER_BG : CARD_BG);
      g.fill(card);

      // Status accent line at top
      g.setColor(borderColor);
      g.fillRoundRect(cx, cy, cardW, 4, CARD_ARC, CARD_ARC);

      // Card border
      g.setColor(hovered && category != 0 ? borderColor : CARD_BORDER);
      g.draw(card);

      // Level number
      g.setFont(numFont);
      FontMetrics nfm = g.getFontMetrics();
      String numStr = String.valueOf(level);
      g.setColor(category == 0 ? TEXT_DIM : TEXT_PRIMARY);
      g.drawString(numStr, cx + (cardW - nfm.stringWidth(numStr)) / 2, cy + cardH / 2 - 4);

      // Status label
      g.setFont(statusFont);
      FontMetrics sfm = g.getFontMetrics();
      g.setColor(borderColor);
      g.drawString(
          statusLabel, cx + (cardW - sfm.stringWidth(statusLabel)) / 2, cy + cardH / 2 + 14);

      // Best time
      if (bestTime > 0) {
        g.setFont(timeFont);
        FontMetrics tfm = g.getFontMetrics();
        String timeStr = String.format("%.1fs", bestTime);
        g.setColor(TEXT_DIM);
        g.drawString(timeStr, cx + (cardW - tfm.stringWidth(timeStr)) / 2, cy + cardH - 10);
      }
    }
  }

  private void updateHover(int mx, int my) {
    int oldCard = hoveredCard;
    int oldHeader = hoveredHeaderBtn;

    hoveredCard = -1;
    hoveredHeaderBtn = -1;

    // Check header buttons
    int btnY = (HEADER_HEIGHT - HEADER_BTN_HEIGHT) / 2;
    if (my >= btnY && my <= btnY + HEADER_BTN_HEIGHT) {
      int backX = CARD_PAD;
      if (mx >= backX && mx <= backX + HEADER_BTN_WIDTH) {
        hoveredHeaderBtn = 0;
      }
      int resetX = getWidth() - CARD_PAD - HEADER_BTN_WIDTH;
      if (mx >= resetX && mx <= resetX + HEADER_BTN_WIDTH) {
        hoveredHeaderBtn = 1;
      }
    }

    // Check cards
    if (hoveredHeaderBtn == -1 && my > HEADER_HEIGHT) {
      int cardIdx = getCardIndex(mx, my);
      if (cardIdx >= 0) {
        hoveredCard = cardIdx;
      }
    }

    boolean interactable = hoveredHeaderBtn >= 0 || (hoveredCard >= 0 && isPlayable(hoveredCard));
    setCursor(
        interactable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

    if (oldCard != hoveredCard || oldHeader != hoveredHeaderBtn) {
      repaint();
    }
  }

  private void handleClick(int mx, int my) {
    // Header buttons
    int btnY = (HEADER_HEIGHT - HEADER_BTN_HEIGHT) / 2;
    if (my >= btnY && my <= btnY + HEADER_BTN_HEIGHT) {
      int backX = CARD_PAD;
      if (mx >= backX && mx <= backX + HEADER_BTN_WIDTH) {
        onMainMenu.run();
        return;
      }
      int resetX = getWidth() - CARD_PAD - HEADER_BTN_WIDTH;
      if (mx >= resetX && mx <= resetX + HEADER_BTN_WIDTH) {
        onReset.run();
        return;
      }
    }

    // Cards
    if (my > HEADER_HEIGHT) {
      int cardIdx = getCardIndex(mx, my);
      if (cardIdx >= 0 && isPlayable(cardIdx)) {
        int level = cardIdx + 1;
        game.setCurrentLevel(level);
        game.setGameState(true, "Level Select");
        game.playSelectedLevel();
      }
    }
  }

  private int getCardIndex(int mx, int my) {
    int w = getWidth();
    int levelCount = levelData.length - 1;
    int cardW = (w - CARD_PAD * (CARD_COLS + 1)) / CARD_COLS;
    int cardH = cardW + 20;

    int adjustedY = my + scrollOffset - HEADER_HEIGHT - CARD_PAD;
    int row = adjustedY / (cardH + CARD_PAD);
    int col = (mx - CARD_PAD) / (cardW + CARD_PAD);

    if (col < 0 || col >= CARD_COLS || row < 0) return -1;

    // Check if click is actually inside the card (not in padding)
    int cardX = CARD_PAD + col * (cardW + CARD_PAD);
    int cardY = HEADER_HEIGHT + CARD_PAD + row * (cardH + CARD_PAD) - scrollOffset;
    if (mx < cardX || mx > cardX + cardW || my < cardY || my > cardY + cardH) return -1;

    int idx = row * CARD_COLS + col;
    return idx < levelCount ? idx : -1;
  }

  private boolean isPlayable(int cardIndex) {
    int level = cardIndex + 1;
    String[] parts = levelData[level].split(",");
    return parts[1].equalsIgnoreCase("completed") || level == firstIncompleteLevel;
  }
}
