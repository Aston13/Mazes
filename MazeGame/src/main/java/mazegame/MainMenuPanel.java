package mazegame;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

/**
 * A custom-painted main menu panel with gradient background, animated title, and styled buttons
 * with hover effects. Replaces the old null-layout + plain JButton approach.
 */
public class MainMenuPanel extends JPanel {

  private static final Color BG_TOP = new Color(26, 26, 26);
  private static final Color BG_BOTTOM = new Color(34, 30, 28);
  private static final Color TITLE_COLOR = new Color(240, 236, 232);
  private static final Color TITLE_SHADOW = new Color(80, 60, 40);
  private static final Color BTN_BG = new Color(50, 44, 40);
  private static final Color BTN_BORDER = new Color(196, 149, 106);
  private static final Color BTN_HOVER_BG = new Color(100, 75, 50);
  private static final Color BTN_TEXT = new Color(220, 216, 210);
  private static final Color BTN_HOVER_TEXT = Color.WHITE;
  private static final Color SUBTITLE_COLOR = new Color(160, 145, 130);

  private static final int BTN_WIDTH = 240;
  private static final int BTN_HEIGHT = 50;
  private static final int BTN_ARC = 12;
  private static final int BTN_GAP = 18;
  private static final int TITLE_FONT_SIZE = 42;
  private static final int SUBTITLE_FONT_SIZE = 14;
  private static final int BTN_FONT_SIZE = 18;

  private final List<MenuButton> buttons = new ArrayList<>();
  private int hoveredIndex = -1;
  private BufferedImage decorationImage;

  /** Describes a clickable button on a custom-painted menu. */
  static class MenuButton {
    private final String label;
    private final String hint;
    private final Runnable action;

    MenuButton(String label, String hint, Runnable action) {
      this.label = label;
      this.hint = hint;
      this.action = action;
    }

    String label() {
      return label;
    }

    String hint() {
      return hint;
    }

    Runnable action() {
      return action;
    }
  }

  public MainMenuPanel() {
    setOpaque(true);
    setBackground(BG_TOP);

    MouseAdapter mouseHandler =
        new MouseAdapter() {
          @Override
          public void mouseMoved(MouseEvent e) {
            int idx = getButtonIndex(e.getX(), e.getY());
            if (idx != hoveredIndex) {
              hoveredIndex = idx;
              setCursor(
                  idx >= 0
                      ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                      : Cursor.getDefaultCursor());
              repaint();
            }
          }

          @Override
          public void mouseClicked(MouseEvent e) {
            int idx = getButtonIndex(e.getX(), e.getY());
            if (idx >= 0 && idx < buttons.size()) {
              buttons.get(idx).action().run();
            }
          }

          @Override
          public void mouseExited(MouseEvent e) {
            if (hoveredIndex != -1) {
              hoveredIndex = -1;
              setCursor(Cursor.getDefaultCursor());
              repaint();
            }
          }
        };
    addMouseListener(mouseHandler);
    addMouseMotionListener(mouseHandler);
  }

  /** Sets a decorative sprite image (e.g. Wesley) to display between the subtitle and buttons. */
  public void setDecorationImage(BufferedImage image) {
    this.decorationImage = image;
    repaint();
  }

  /** Adds a button to the menu. */
  public void addButton(String label, String hint, Runnable action) {
    buttons.add(new MenuButton(label, hint, action));
    repaint();
  }

  @Override
  public Dimension getPreferredSize() {
    // Return parent's preferred size (the frame will set this)
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

    // Decorative grid lines
    g.setColor(new Color(255, 255, 255, 6));
    for (int x = 0; x < w; x += 40) {
      g.drawLine(x, 0, x, h);
    }
    for (int y = 0; y < h; y += 40) {
      g.drawLine(0, y, w, y);
    }

    // Title
    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    g.setFont(titleFont);
    FontMetrics titleFm = g.getFontMetrics();
    String title = "Wesley's Way Out";
    int titleX = (w - titleFm.stringWidth(title)) / 2;
    int titleY = h / 5 + titleFm.getAscent() / 2;

    // Shadow
    g.setColor(TITLE_SHADOW);
    g.drawString(title, titleX + 2, titleY + 2);
    // Main title
    g.setColor(TITLE_COLOR);
    g.drawString(title, titleX, titleY);

    // Subtitle
    Font subtitleFont = new Font("Dialog", Font.PLAIN, SUBTITLE_FONT_SIZE);
    g.setFont(subtitleFont);
    FontMetrics subFm = g.getFontMetrics();
    String subtitle = "A procedurally generated maze adventure";
    g.setColor(SUBTITLE_COLOR);
    g.drawString(subtitle, (w - subFm.stringWidth(subtitle)) / 2, titleY + 30);

    // Wesley decoration sprite (pixel-art, centered between subtitle and buttons)
    if (decorationImage != null) {
      int imgSize = Math.max(80, h / 6);
      int imgX = (w - imgSize) / 2;
      int imgY = titleY + 40;
      Composite oldComposite = g.getComposite();
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
      g.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      g.drawImage(decorationImage, imgX, imgY, imgSize, imgSize, null);
      g.setComposite(oldComposite);
    }

    // Buttons
    int totalBtnHeight = buttons.size() * BTN_HEIGHT + (buttons.size() - 1) * BTN_GAP;
    int startY = (h / 2) + (h / 2 - totalBtnHeight) / 2 - 20;

    Font btnFont = new Font("Dialog", Font.PLAIN, BTN_FONT_SIZE);
    Font hintFont = new Font("Dialog", Font.PLAIN, 11);

    for (int i = 0; i < buttons.size(); i++) {
      MenuButton btn = buttons.get(i);
      int btnX = (w - BTN_WIDTH) / 2;
      int btnY = startY + i * (BTN_HEIGHT + BTN_GAP);
      boolean hovered = (i == hoveredIndex);

      // Button background
      RoundRectangle2D.Double rect =
          new RoundRectangle2D.Double(btnX, btnY, BTN_WIDTH, BTN_HEIGHT, BTN_ARC, BTN_ARC);
      g.setColor(hovered ? BTN_HOVER_BG : BTN_BG);
      g.fill(rect);

      // Button border
      g.setColor(hovered ? TITLE_COLOR : BTN_BORDER);
      g.draw(rect);

      // Button label
      g.setFont(btnFont);
      FontMetrics btnFm = g.getFontMetrics();
      g.setColor(hovered ? BTN_HOVER_TEXT : BTN_TEXT);
      int textX = btnX + (BTN_WIDTH - btnFm.stringWidth(btn.label())) / 2;
      int textY = btnY + (BTN_HEIGHT + btnFm.getAscent()) / 2 - 2;

      if (!btn.hint().isEmpty()) {
        // Shift label up slightly to make room for hint
        textY -= 6;
      }
      g.drawString(btn.label(), textX, textY);

      // Hint text (e.g. "[Space]")
      if (!btn.hint().isEmpty()) {
        g.setFont(hintFont);
        FontMetrics hintFm = g.getFontMetrics();
        g.setColor(SUBTITLE_COLOR);
        int hintX = btnX + (BTN_WIDTH - hintFm.stringWidth(btn.hint())) / 2;
        g.drawString(btn.hint(), hintX, textY + 14);
      }
    }

    // Version label (bottom-right corner)
    String versionText = "v" + BuildInfo.getVersion();
    Font versionFont = new Font("Dialog", Font.PLAIN, 11);
    g.setFont(versionFont);
    FontMetrics vFm = g.getFontMetrics();
    g.setColor(new Color(120, 110, 100));
    g.drawString(versionText, w - vFm.stringWidth(versionText) - 10, h - 10);
  }

  private int getButtonIndex(int mx, int my) {
    int w = getWidth();
    int h = getHeight();
    int totalBtnHeight = buttons.size() * BTN_HEIGHT + (buttons.size() - 1) * BTN_GAP;
    int startY = (h / 2) + (h / 2 - totalBtnHeight) / 2 - 20;

    for (int i = 0; i < buttons.size(); i++) {
      int btnX = (w - BTN_WIDTH) / 2;
      int btnY = startY + i * (BTN_HEIGHT + BTN_GAP);
      if (mx >= btnX && mx <= btnX + BTN_WIDTH && my >= btnY && my <= btnY + BTN_HEIGHT) {
        return i;
      }
    }
    return -1;
  }
}
