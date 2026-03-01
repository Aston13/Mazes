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
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Dedicated skins / collection screen showing total bone progress, unlock bars, and skin-selection
 * cards. Accessible from the main menu.
 */
public class SkinsPanel extends JPanel {

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
  private static final Color GRID_LINE = new Color(255, 255, 255, 6);
  private static final Color PROGRESS_BG = new Color(60, 52, 46);
  private static final Color PROGRESS_FILL = new Color(196, 149, 106);

  private static final int TITLE_FONT_SIZE = 36;
  private static final int CARD_WIDTH = 200;
  private static final int CARD_HEIGHT = 220;
  private static final int CARD_ARC = 14;
  private static final int CARD_GAP = 30;
  private static final int PREVIEW_SIZE = 100;
  private static final int BTN_WIDTH = 140;
  private static final int BTN_HEIGHT = 42;
  private static final int BTN_ARC = 10;
  private static final int PARTICLE_COUNT = 20;
  private static final int PARTICLE_TICK_MS = 50;

  private final GameSettings settings;
  private final AssetManager assetManager;
  private final AudioManager audioManager;
  private final Runnable onBack;
  private final GameSettings.DogSkin[] skins = GameSettings.DogSkin.values();
  private final int totalBones;

  private int hoveredCard = -1;
  private boolean hoveredBack = false;
  private final double[] particleX = new double[PARTICLE_COUNT];
  private final double[] particleY = new double[PARTICLE_COUNT];
  private final double[] particleSpeed = new double[PARTICLE_COUNT];
  private final double[] particleAlpha = new double[PARTICLE_COUNT];
  private final double[] particleSize = new double[PARTICLE_COUNT];
  private final Random particleRng = new Random();
  private Timer particleTimer;

  /**
   * Creates a new skins panel.
   *
   * @param settings the game settings to modify
   * @param assetManager the asset manager for skin preview images
   * @param audioManager the audio manager for UI sounds
   * @param onBack callback to return to the main menu
   * @param totalBones total bones collected (for skin unlock checks)
   */
  public SkinsPanel(
      GameSettings settings,
      AssetManager assetManager,
      AudioManager audioManager,
      Runnable onBack,
      int totalBones) {
    this.settings = settings;
    this.assetManager = assetManager;
    this.audioManager = audioManager;
    this.onBack = onBack;
    this.totalBones = totalBones;
    setOpaque(true);
    setBackground(BG_TOP);
    setFocusable(true);

    // Initialise floating particles
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      resetParticle(i, true);
    }
    particleTimer =
        new Timer(
            PARTICLE_TICK_MS,
            e -> {
              for (int i = 0; i < PARTICLE_COUNT; i++) {
                particleY[i] -= particleSpeed[i];
                particleAlpha[i] -= 0.004;
                if (particleY[i] < -10 || particleAlpha[i] <= 0) {
                  resetParticle(i, false);
                }
              }
              repaint();
            });
    particleTimer.start();

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

    // Floating particles
    Composite particleOrig = g.getComposite();
    for (int i = 0; i < PARTICLE_COUNT; i++) {
      float alpha = (float) Math.max(0, Math.min(1, particleAlpha[i]));
      g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
      g.setColor(CARD_SELECTED_BORDER);
      double px = particleX[i] / 800.0 * w;
      double py = particleY[i] / 800.0 * h;
      int sz = (int) particleSize[i];
      g.fillOval((int) px, (int) py, sz, sz);
    }
    g.setComposite(particleOrig);

    // Title
    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    g.setFont(titleFont);
    FontMetrics titleFm = g.getFontMetrics();
    String title = "Collection";
    int titleX = (w - titleFm.stringWidth(title)) / 2;
    int titleY = h / 8 + titleFm.getAscent() / 2;
    g.setColor(TITLE_SHADOW);
    g.drawString(title, titleX + 2, titleY + 2);
    g.setColor(TITLE_COLOR);
    g.drawString(title, titleX, titleY);

    // Bone counter (large, centred)
    Font boneCountFont = new Font("Dialog", Font.BOLD, 22);
    g.setFont(boneCountFont);
    FontMetrics bcFm = g.getFontMetrics();
    String boneStr = "\uD83E\uDDB4  " + totalBones + " / 30  golden bones";
    int boneX = (w - bcFm.stringWidth(boneStr)) / 2;
    int boneY = titleY + 40;
    g.setColor(new Color(235, 210, 170));
    g.drawString(boneStr, boneX, boneY);

    // Overall progress bar
    int barW = 300;
    int barH = 14;
    int barX = (w - barW) / 2;
    int barY = boneY + 14;
    double progress = Math.min(1.0, totalBones / 30.0);

    g.setColor(PROGRESS_BG);
    g.fillRoundRect(barX, barY, barW, barH, barH, barH);
    if (progress > 0) {
      g.setColor(PROGRESS_FILL);
      g.fillRoundRect(barX, barY, (int) (barW * progress), barH, barH, barH);
    }
    g.setColor(CARD_BORDER);
    g.drawRoundRect(barX, barY, barW, barH, barH, barH);

    // Section label
    Font sectionFont = new Font("Dialog", Font.BOLD, 16);
    g.setFont(sectionFont);
    FontMetrics secFm = g.getFontMetrics();
    String sectionLabel = "Dog Skins";
    int secX = (w - secFm.stringWidth(sectionLabel)) / 2;
    int secY = barY + barH + 35;
    g.setColor(SECTION_LABEL);
    g.drawString(sectionLabel, secX, secY);

    // Skin cards (centred horizontally)
    int totalCardsWidth = skins.length * CARD_WIDTH + (skins.length - 1) * CARD_GAP;
    int cardsStartX = (w - totalCardsWidth) / 2;
    int cardsY = secY + 25;

    Font nameFont = new Font("Dialog", Font.BOLD, 16);
    Font descFont = new Font("Dialog", Font.PLAIN, 12);

    for (int i = 0; i < skins.length; i++) {
      GameSettings.DogSkin skin = skins[i];
      int cx = cardsStartX + i * (CARD_WIDTH + CARD_GAP);
      boolean selected = skin == settings.getActiveSkin();
      boolean hovered = (i == hoveredCard);
      boolean unlocked = GameSettings.isSkinUnlocked(skin, totalBones);

      // Card background
      RoundRectangle2D.Double card =
          new RoundRectangle2D.Double(cx, cardsY, CARD_WIDTH, CARD_HEIGHT, CARD_ARC, CARD_ARC);
      g.setColor(hovered ? CARD_HOVER_BG : CARD_BG);
      g.fill(card);

      // Card border
      if (selected && unlocked) {
        g.setColor(CARD_SELECTED_BORDER);
        g.setStroke(new java.awt.BasicStroke(2.5f));
        g.draw(card);
        g.setStroke(new java.awt.BasicStroke(1f));
      } else {
        g.setColor(hovered ? SECTION_LABEL : CARD_BORDER);
        g.draw(card);
      }

      // Preview image
      BufferedImage preview = assetManager.getPreloadedImage(skin.prefix() + "East0");
      if (preview != null) {
        int imgX = cx + (CARD_WIDTH - PREVIEW_SIZE) / 2;
        int imgY = cardsY + 20;
        Composite oldComp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(preview, imgX, imgY, PREVIEW_SIZE, PREVIEW_SIZE, null);
        g.setComposite(oldComp);
      }

      // Skin name
      g.setFont(nameFont);
      FontMetrics nfm = g.getFontMetrics();
      String name = skin.displayName();
      g.setColor(TEXT_PRIMARY);
      g.drawString(name, cx + (CARD_WIDTH - nfm.stringWidth(name)) / 2, cardsY + CARD_HEIGHT - 50);

      // Unlock status / selection indicator
      if (!unlocked) {
        // Dark overlay
        Composite lockComp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.setColor(new Color(20, 18, 16));
        g.fill(card);
        g.setComposite(lockComp);

        // Lock icon
        g.setFont(new Font("Dialog", Font.BOLD, 28));
        FontMetrics lockFm = g.getFontMetrics();
        String lockIcon = "\uD83D\uDD12";
        g.setColor(TEXT_DIM);
        g.drawString(
            lockIcon,
            cx + (CARD_WIDTH - lockFm.stringWidth(lockIcon)) / 2,
            cardsY + CARD_HEIGHT / 2 - 16);

        // Progress bar inside the locked card
        int unlockBarW = CARD_WIDTH - 40;
        int unlockBarH = 10;
        int unlockBarX = cx + 20;
        int unlockBarY = cardsY + CARD_HEIGHT / 2 + 4;
        double unlockProgress =
            Math.min(1.0, (double) totalBones / GameSettings.SASSO_UNLOCK_BONES);

        g.setColor(PROGRESS_BG);
        g.fillRoundRect(unlockBarX, unlockBarY, unlockBarW, unlockBarH, unlockBarH, unlockBarH);
        if (unlockProgress > 0) {
          g.setColor(PROGRESS_FILL);
          g.fillRoundRect(
              unlockBarX,
              unlockBarY,
              (int) (unlockBarW * unlockProgress),
              unlockBarH,
              unlockBarH,
              unlockBarH);
        }
        g.setColor(CARD_BORDER);
        g.drawRoundRect(unlockBarX, unlockBarY, unlockBarW, unlockBarH, unlockBarH, unlockBarH);

        // Requirement text below bar
        g.setFont(new Font("Dialog", Font.PLAIN, 11));
        FontMetrics reqFm = g.getFontMetrics();
        String reqText = totalBones + "/" + GameSettings.SASSO_UNLOCK_BONES + " bones collected";
        g.setColor(SECTION_LABEL);
        g.drawString(
            reqText,
            cx + (CARD_WIDTH - reqFm.stringWidth(reqText)) / 2,
            unlockBarY + unlockBarH + 16);
      } else {
        g.setFont(descFont);
        FontMetrics dfm = g.getFontMetrics();
        String desc = selected ? "\u2713 Selected" : "Click to select";
        g.setColor(selected ? CARD_SELECTED_BORDER : TEXT_DIM);
        g.drawString(
            desc, cx + (CARD_WIDTH - dfm.stringWidth(desc)) / 2, cardsY + CARD_HEIGHT - 28);
      }
    }

    // Back button
    int btnX = (w - BTN_WIDTH) / 2;
    int btnY = cardsY + CARD_HEIGHT + 35;
    UiTheme.paintButton(
        g, btnX, btnY, BTN_WIDTH, BTN_HEIGHT, BTN_ARC, "Back [Esc]", null, hoveredBack, 16, true);
  }

  // ---- Particles ----

  private void resetParticle(int i, boolean randomY) {
    particleX[i] = particleRng.nextDouble() * 800;
    particleY[i] = randomY ? particleRng.nextDouble() * 800 : 780 + particleRng.nextDouble() * 40;
    particleSpeed[i] = 0.3 + particleRng.nextDouble() * 0.7;
    particleAlpha[i] = 0.10 + particleRng.nextDouble() * 0.20;
    particleSize[i] = 2 + particleRng.nextDouble() * 3;
  }

  // ---- Hit testing ----

  private int getCardIndex(int mx, int my) {
    int w = getWidth();
    int h = getHeight();

    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    FontMetrics titleFm = getFontMetrics(titleFont);
    int titleY = h / 8 + titleFm.getAscent() / 2;
    int boneY = titleY + 40;
    int barY = boneY + 14;
    int secY = barY + 14 + 35;
    int cardsY = secY + 25;

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
    int titleY = h / 8 + titleFm.getAscent() / 2;
    int boneY = titleY + 40;
    int barY = boneY + 14;
    int secY = barY + 14 + 35;
    int cardsY = secY + 25;

    int btnX = (w - BTN_WIDTH) / 2;
    int btnY = cardsY + CARD_HEIGHT + 35;

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
      if (!GameSettings.isSkinUnlocked(skins[cardIdx], totalBones)) {
        return; // locked skin — ignore click
      }
      audioManager.play(AudioManager.Sound.BUTTON_CLICK);
      settings.setActiveSkin(skins[cardIdx]);
      repaint();
      return;
    }
    if (isOverBackButton(mx, my)) {
      audioManager.play(AudioManager.Sound.BUTTON_CLICK);
      onBack.run();
    }
  }
}
