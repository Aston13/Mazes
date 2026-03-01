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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Custom-painted settings screen allowing the player to choose a dog skin. Displays a preview of
 * each available skin with a selection highlight.
 */
public class SettingsPanel extends JPanel {

  private static final Color BG_TOP = new Color(26, 26, 26);
  private static final Color BG_BOTTOM = new Color(34, 30, 28);
  private static final Color TITLE_COLOR = new Color(240, 236, 232);
  private static final Color TITLE_SHADOW = new Color(80, 60, 40);
  private static final Color SECTION_LABEL = new Color(196, 149, 106);
  private static final Color CARD_BG = new Color(50, 44, 40);
  private static final Color CARD_BORDER = new Color(100, 85, 70);
  private static final Color CARD_SELECTED_BORDER = new Color(196, 149, 106);
  private static final Color CARD_HOVER_BG = new Color(65, 55, 48);
  private static final Color TEXT_PRIMARY = new Color(240, 236, 232);
  private static final Color TEXT_DIM = new Color(160, 145, 130);
  private static final Color BTN_BG = new Color(50, 44, 40);
  private static final Color BTN_BORDER = new Color(196, 149, 106);
  private static final Color BTN_HOVER_BG = new Color(100, 75, 50);
  private static final Color GRID_LINE = new Color(255, 255, 255, 6);

  private static final int TITLE_FONT_SIZE = 36;
  private static final int SECTION_FONT_SIZE = 16;
  private static final int CARD_WIDTH = 200;
  private static final int CARD_HEIGHT = 220;
  private static final int CARD_ARC = 14;
  private static final int CARD_GAP = 30;
  private static final int PREVIEW_SIZE = 100;
  private static final int BTN_WIDTH = 140;
  private static final int BTN_HEIGHT = 42;
  private static final int BTN_ARC = 10;

  private final GameSettings settings;
  private final AssetManager assetManager;
  private final Runnable onBack;
  private final GameSettings.DogSkin[] skins = GameSettings.DogSkin.values();

  private int hoveredCard = -1;
  private boolean hoveredBack = false;

  /**
   * Creates a new settings panel.
   *
   * @param settings the game settings to modify
   * @param assetManager the asset manager for skin preview images
   * @param onBack callback to return to the main menu
   */
  public SettingsPanel(GameSettings settings, AssetManager assetManager, Runnable onBack) {
    this.settings = settings;
    this.assetManager = assetManager;
    this.onBack = onBack;
    setOpaque(true);
    setBackground(BG_TOP);
    setFocusable(true);

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
            if (hoveredCard != -1 || hoveredBack) {
              hoveredCard = -1;
              hoveredBack = false;
              setCursor(Cursor.getDefaultCursor());
              repaint();
            }
          }
        };
    addMouseListener(mouseHandler);
    addMouseMotionListener(mouseHandler);

    InputHandler.bindKey(this, KeyEvent.VK_ESCAPE, "Back", false, evt -> onBack.run());
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

    // Decorative grid lines
    g.setColor(GRID_LINE);
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
    String title = "Settings";
    int titleX = (w - titleFm.stringWidth(title)) / 2;
    int titleY = h / 7 + titleFm.getAscent() / 2;
    g.setColor(TITLE_SHADOW);
    g.drawString(title, titleX + 2, titleY + 2);
    g.setColor(TITLE_COLOR);
    g.drawString(title, titleX, titleY);

    // Section label
    Font sectionFont = new Font("Dialog", Font.BOLD, SECTION_FONT_SIZE);
    g.setFont(sectionFont);
    FontMetrics secFm = g.getFontMetrics();
    String sectionLabel = "Dog Skin";
    int secX = (w - secFm.stringWidth(sectionLabel)) / 2;
    int secY = titleY + 50;
    g.setColor(SECTION_LABEL);
    g.drawString(sectionLabel, secX, secY);

    // Skin cards (centered horizontally)
    int totalCardsWidth = skins.length * CARD_WIDTH + (skins.length - 1) * CARD_GAP;
    int cardsStartX = (w - totalCardsWidth) / 2;
    int cardsY = secY + 30;

    Font nameFont = new Font("Dialog", Font.BOLD, 16);
    Font descFont = new Font("Dialog", Font.PLAIN, 12);

    for (int i = 0; i < skins.length; i++) {
      GameSettings.DogSkin skin = skins[i];
      int cx = cardsStartX + i * (CARD_WIDTH + CARD_GAP);
      boolean selected = skin == settings.getActiveSkin();
      boolean hovered = (i == hoveredCard);

      // Card background
      RoundRectangle2D.Double card =
          new RoundRectangle2D.Double(cx, cardsY, CARD_WIDTH, CARD_HEIGHT, CARD_ARC, CARD_ARC);
      g.setColor(hovered ? CARD_HOVER_BG : CARD_BG);
      g.fill(card);

      // Card border (highlighted when selected)
      if (selected) {
        g.setColor(CARD_SELECTED_BORDER);
        g.setStroke(new java.awt.BasicStroke(2.5f));
        g.draw(card);
        g.setStroke(new java.awt.BasicStroke(1f));
      } else {
        g.setColor(hovered ? SECTION_LABEL : CARD_BORDER);
        g.draw(card);
      }

      // Preview image (centered in card, pixel-art upscale)
      BufferedImage preview = assetManager.getPreloadedImage(skin.prefix() + "East0");
      if (preview != null) {
        int imgX = cx + (CARD_WIDTH - PREVIEW_SIZE) / 2;
        int imgY = cardsY + 20;
        Composite oldComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(preview, imgX, imgY, PREVIEW_SIZE, PREVIEW_SIZE, null);
        g.setComposite(oldComposite);
      }

      // Skin name
      g.setFont(nameFont);
      FontMetrics nfm = g.getFontMetrics();
      String name = skin.displayName();
      g.setColor(TEXT_PRIMARY);
      g.drawString(name, cx + (CARD_WIDTH - nfm.stringWidth(name)) / 2, cardsY + CARD_HEIGHT - 50);

      // Selected indicator or hint
      g.setFont(descFont);
      FontMetrics dfm = g.getFontMetrics();
      String desc = selected ? "\u2713 Selected" : "Click to select";
      g.setColor(selected ? CARD_SELECTED_BORDER : TEXT_DIM);
      g.drawString(desc, cx + (CARD_WIDTH - dfm.stringWidth(desc)) / 2, cardsY + CARD_HEIGHT - 28);
    }

    // Back button
    int btnX = (w - BTN_WIDTH) / 2;
    int btnY = cardsY + CARD_HEIGHT + 50;
    RoundRectangle2D.Double backRect =
        new RoundRectangle2D.Double(btnX, btnY, BTN_WIDTH, BTN_HEIGHT, BTN_ARC, BTN_ARC);
    g.setColor(hoveredBack ? BTN_HOVER_BG : BTN_BG);
    g.fill(backRect);
    g.setColor(hoveredBack ? TITLE_COLOR : BTN_BORDER);
    g.draw(backRect);

    g.setFont(new Font("Dialog", Font.PLAIN, 16));
    FontMetrics bfm = g.getFontMetrics();
    String backLabel = "Back [Esc]";
    g.setColor(hoveredBack ? Color.WHITE : TEXT_PRIMARY);
    g.drawString(
        backLabel,
        btnX + (BTN_WIDTH - bfm.stringWidth(backLabel)) / 2,
        btnY + (BTN_HEIGHT + bfm.getAscent()) / 2 - 2);
  }

  // ---- Hit testing ----

  private int getCardIndex(int mx, int my) {
    int w = getWidth();
    int h = getHeight();

    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    FontMetrics titleFm = getFontMetrics(titleFont);
    int titleY = h / 7 + titleFm.getAscent() / 2;
    int cardsY = titleY + 80;

    int totalCardsWidth = skins.length * CARD_WIDTH + (skins.length - 1) * CARD_GAP;
    int cardsStartX = (w - totalCardsWidth) / 2;

    for (int i = 0; i < skins.length; i++) {
      int cx = cardsStartX + i * (CARD_WIDTH + CARD_GAP);
      if (mx >= cx && mx <= cx + CARD_WIDTH && my >= cardsY && my <= cardsY + CARD_HEIGHT) {
        return i;
      }
    }
    return -1;
  }

  private boolean isOverBackButton(int mx, int my) {
    int w = getWidth();
    int h = getHeight();

    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    FontMetrics titleFm = getFontMetrics(titleFont);
    int titleY = h / 7 + titleFm.getAscent() / 2;
    int cardsY = titleY + 80;
    int btnX = (w - BTN_WIDTH) / 2;
    int btnY = cardsY + CARD_HEIGHT + 50;

    return mx >= btnX && mx <= btnX + BTN_WIDTH && my >= btnY && my <= btnY + BTN_HEIGHT;
  }

  private void updateHover(int mx, int my) {
    int oldCard = hoveredCard;
    boolean oldBack = hoveredBack;

    hoveredCard = getCardIndex(mx, my);
    hoveredBack = isOverBackButton(mx, my);

    boolean interactable = hoveredCard >= 0 || hoveredBack;
    setCursor(
        interactable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

    if (oldCard != hoveredCard || oldBack != hoveredBack) {
      repaint();
    }
  }

  private void handleClick(int mx, int my) {
    int cardIdx = getCardIndex(mx, my);
    if (cardIdx >= 0 && cardIdx < skins.length) {
      settings.setActiveSkin(skins[cardIdx]);
      repaint();
      return;
    }
    if (isOverBackButton(mx, my)) {
      onBack.run();
    }
  }
}
