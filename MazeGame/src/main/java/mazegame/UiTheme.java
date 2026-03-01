package mazegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

/**
 * Shared colour palette, font sizes, and button-rendering utilities used across all custom-painted
 * UI panels.
 */
public final class UiTheme {

  private UiTheme() {}

  // ---------------------------------------------------------------------------
  // Palette
  // ---------------------------------------------------------------------------

  public static final Color BG_TOP = new Color(26, 26, 26);
  public static final Color BG_BOTTOM = new Color(34, 30, 28);

  public static final Color TITLE_COLOR = new Color(240, 236, 232);
  public static final Color TITLE_SHADOW = new Color(80, 60, 40);
  public static final Color TITLE_GLOW = new Color(196, 149, 106, 60);

  public static final Color ACCENT_LINE = new Color(196, 149, 106);
  public static final Color ACCENT_LINE_FADE = new Color(196, 149, 106, 0);

  public static final Color SUBTITLE_COLOR = new Color(160, 145, 130);
  public static final Color TEXT_PRIMARY = new Color(220, 216, 210);
  public static final Color TEXT_DIM = new Color(130, 120, 110);

  public static final Color GRID_LINE = new Color(255, 255, 255, 6);

  // Button colours
  public static final Color BTN_BG = new Color(50, 44, 40);
  public static final Color BTN_BORDER = new Color(196, 149, 106);
  public static final Color BTN_HOVER_BG = new Color(100, 75, 50);
  public static final Color BTN_TEXT = new Color(220, 216, 210);
  public static final Color BTN_HOVER_TEXT = Color.WHITE;

  // Card colours (used for skin cards, level cards, toggle buttons)
  public static final Color CARD_BG = new Color(40, 36, 33);
  public static final Color CARD_BORDER = new Color(80, 70, 60);
  public static final Color CARD_HOVER_BG = new Color(55, 48, 42);

  // Standard button dimensions (used by MainMenu + ResultOverlay)
  public static final int STD_BTN_WIDTH = 240;
  public static final int STD_BTN_HEIGHT = 50;
  public static final int STD_BTN_ARC = 12;
  public static final int STD_BTN_GAP = 18;
  public static final int STD_BTN_FONT_SIZE = 18;

  // ---------------------------------------------------------------------------
  // Button painting
  // ---------------------------------------------------------------------------

  /**
   * Paints a standard button (rounded rect, fill, border, centred label, optional hint, optional
   * spinning diamond hover indicators).
   *
   * @param g graphics context (antialiasing should already be enabled)
   * @param x button left edge
   * @param y button top edge
   * @param w button width
   * @param h button height
   * @param arc corner arc diameter
   * @param label primary label text
   * @param hint secondary hint text (may be {@code null} or empty)
   * @param hovered whether the cursor is hovering
   * @param fontSize label font size
   * @param diamonds whether to draw spinning diamond accents on hover
   */
  public static void paintButton(
      Graphics2D g,
      int x,
      int y,
      int w,
      int h,
      int arc,
      String label,
      String hint,
      boolean hovered,
      int fontSize,
      boolean diamonds) {

    // Background
    RoundRectangle2D.Double rect = new RoundRectangle2D.Double(x, y, w, h, arc, arc);
    g.setColor(hovered ? BTN_HOVER_BG : BTN_BG);
    g.fill(rect);

    // Border
    g.setColor(hovered ? TITLE_COLOR : BTN_BORDER);
    g.draw(rect);

    // Label
    Font btnFont = new Font("Dialog", Font.PLAIN, fontSize);
    g.setFont(btnFont);
    FontMetrics fm = g.getFontMetrics();
    g.setColor(hovered ? BTN_HOVER_TEXT : BTN_TEXT);
    int textX = x + (w - fm.stringWidth(label)) / 2;
    int textY = y + (h + fm.getAscent()) / 2 - 2;

    boolean hasHint = hint != null && !hint.isEmpty();
    if (hasHint) {
      textY -= 6;
    }
    g.drawString(label, textX, textY);

    // Spinning diamonds
    if (diamonds && hovered) {
      drawHoverDiamonds(g, x, y, w, h);
    }

    // Hint text below label
    if (hasHint) {
      Font hintFont = new Font("Dialog", Font.PLAIN, 11);
      g.setFont(hintFont);
      FontMetrics hfm = g.getFontMetrics();
      g.setColor(SUBTITLE_COLOR);
      int hintX = x + (w - hfm.stringWidth(hint)) / 2;
      g.drawString(hint, hintX, textY + 14);
    }
  }

  /**
   * Convenience overload that uses the standard menu button dimensions and includes hover diamonds.
   */
  public static void paintStdButton(
      Graphics2D g, int x, int y, String label, String hint, boolean hovered) {
    paintButton(
        g,
        x,
        y,
        STD_BTN_WIDTH,
        STD_BTN_HEIGHT,
        STD_BTN_ARC,
        label,
        hint,
        hovered,
        STD_BTN_FONT_SIZE,
        true);
  }

  /**
   * Draws small spinning diamond accents on either side of a button.
   *
   * @param g the graphics context
   * @param btnX button left x
   * @param btnY button top y
   * @param btnW button width
   * @param btnH button height
   */
  public static void drawHoverDiamonds(Graphics2D g, int btnX, int btnY, int btnW, int btnH) {
    double angle = (System.currentTimeMillis() % 2000) / 2000.0 * Math.PI * 2;
    int cy = btnY + btnH / 2;
    int size = 5;
    g.setColor(ACCENT_LINE);

    // Left diamond
    int lx = btnX + 12;
    AffineTransform old = g.getTransform();
    g.rotate(angle, lx, cy);
    g.fillPolygon(
        new int[] {lx - size, lx, lx + size, lx}, new int[] {cy, cy - size, cy, cy + size}, 4);
    g.setTransform(old);

    // Right diamond
    int rx = btnX + btnW - 12;
    old = g.getTransform();
    g.rotate(-angle, rx, cy);
    g.fillPolygon(
        new int[] {rx - size, rx, rx + size, rx}, new int[] {cy, cy - size, cy, cy + size}, 4);
    g.setTransform(old);
  }
}
