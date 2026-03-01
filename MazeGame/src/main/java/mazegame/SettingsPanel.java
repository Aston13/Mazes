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
  private boolean hoveredMuteToggle = false;
  private boolean hoveredMusicToggle = false;
  private final double[] particleX = new double[PARTICLE_COUNT];
  private final double[] particleY = new double[PARTICLE_COUNT];
  private final double[] particleSpeed = new double[PARTICLE_COUNT];
  private final double[] particleAlpha = new double[PARTICLE_COUNT];
  private final double[] particleSize = new double[PARTICLE_COUNT];
  private final Random particleRng = new Random();
  private Timer particleTimer;

  /**
   * Creates a new settings panel.
   *
   * @param settings the game settings to modify
   * @param assetManager the asset manager for skin preview images
   * @param audioManager the audio manager for mute control
   * @param onBack callback to return to the main menu
   * @param totalBones total bones collected (for skin unlock checks)
   */
  public SettingsPanel(
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
            if (hoveredCard != -1 || hoveredBack || hoveredMuteToggle || hoveredMusicToggle) {
              hoveredCard = -1;
              hoveredBack = false;
              hoveredMuteToggle = false;
              hoveredMusicToggle = false;
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
      boolean unlocked = GameSettings.isSkinUnlocked(skin, totalBones);
      g.setFont(descFont);
      FontMetrics dfm = g.getFontMetrics();
      if (!unlocked) {
        // Dark overlay covering the card
        Composite lockComp = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.setColor(new Color(20, 18, 16));
        g.fill(card);
        g.setComposite(lockComp);

        // Lock icon and message
        g.setFont(new Font("Dialog", Font.BOLD, 28));
        FontMetrics lockFm = g.getFontMetrics();
        String lockIcon = "\uD83D\uDD12";
        g.setColor(new Color(160, 145, 130));
        g.drawString(
            lockIcon,
            cx + (CARD_WIDTH - lockFm.stringWidth(lockIcon)) / 2,
            cardsY + CARD_HEIGHT / 2 - 10);
        g.setFont(new Font("Dialog", Font.PLAIN, 11));
        FontMetrics reqFm = g.getFontMetrics();
        int remaining = GameSettings.SASSO_UNLOCK_BONES - totalBones;
        String reqText = "Collect " + remaining + " more bones";
        g.setColor(new Color(196, 149, 106));
        g.drawString(
            reqText,
            cx + (CARD_WIDTH - reqFm.stringWidth(reqText)) / 2,
            cardsY + CARD_HEIGHT / 2 + 14);
      } else {
        String desc = selected ? "\u2713 Selected" : "Click to select";
        g.setColor(selected ? CARD_SELECTED_BORDER : TEXT_DIM);
        g.drawString(
            desc, cx + (CARD_WIDTH - dfm.stringWidth(desc)) / 2, cardsY + CARD_HEIGHT - 28);
      }
    }

    // Sound section label
    int soundSecY = cardsY + CARD_HEIGHT + 40;
    g.setFont(sectionFont);
    FontMetrics soundSecFm = g.getFontMetrics();
    String soundLabel = "Sound";
    int soundSecX = (w - soundSecFm.stringWidth(soundLabel)) / 2;
    g.setColor(SECTION_LABEL);
    g.drawString(soundLabel, soundSecX, soundSecY);

    // Mute toggle
    int toggleW = 200;
    int toggleH = 42;
    int toggleX = (w - toggleW) / 2;
    int toggleY = soundSecY + 15;
    RoundRectangle2D.Double muteRect =
        new RoundRectangle2D.Double(toggleX, toggleY, toggleW, toggleH, BTN_ARC, BTN_ARC);
    boolean muted = settings.isSoundMuted();
    g.setColor(hoveredMuteToggle ? CARD_HOVER_BG : CARD_BG);
    g.fill(muteRect);
    g.setColor(hoveredMuteToggle ? SECTION_LABEL : CARD_BORDER);
    g.draw(muteRect);

    g.setFont(new Font("Dialog", Font.PLAIN, 14));
    FontMetrics mfm = g.getFontMetrics();
    String muteLabel = muted ? "\uD83D\uDD07 Sound: OFF" : "\uD83D\uDD0A Sound: ON";
    g.setColor(muted ? TEXT_DIM : TEXT_PRIMARY);
    g.drawString(
        muteLabel,
        toggleX + (toggleW - mfm.stringWidth(muteLabel)) / 2,
        toggleY + (toggleH + mfm.getAscent()) / 2 - 2);

    // Music mute toggle
    int musicToggleY = toggleY + toggleH + 12;
    RoundRectangle2D.Double musicRect =
        new RoundRectangle2D.Double(toggleX, musicToggleY, toggleW, toggleH, BTN_ARC, BTN_ARC);
    boolean musicMuted = settings.isMusicMuted();
    g.setColor(hoveredMusicToggle ? CARD_HOVER_BG : CARD_BG);
    g.fill(musicRect);
    g.setColor(hoveredMusicToggle ? SECTION_LABEL : CARD_BORDER);
    g.draw(musicRect);

    g.setFont(new Font("Dialog", Font.PLAIN, 14));
    FontMetrics mmfm = g.getFontMetrics();
    String musicLabel = musicMuted ? "\uD83D\uDD07 Music: OFF" : "\u266A Music: ON";
    g.setColor(musicMuted ? TEXT_DIM : TEXT_PRIMARY);
    g.drawString(
        musicLabel,
        toggleX + (toggleW - mmfm.stringWidth(musicLabel)) / 2,
        musicToggleY + (toggleH + mmfm.getAscent()) / 2 - 2);

    // Browser audio notice
    int noticeY = musicToggleY + toggleH + 8;
    if ("true".equals(System.getProperty("cheerpj.browser"))) {
      g.setFont(new Font("Dialog", Font.ITALIC, 11));
      FontMetrics nfm2 = g.getFontMetrics();
      String notice = "\u26A0 Audio is not available in the browser";
      g.setColor(TEXT_DIM);
      g.drawString(notice, (w - nfm2.stringWidth(notice)) / 2, noticeY + nfm2.getAscent() + 2);
      noticeY += 20;
    }

    // Back button (rendered via shared UiTheme with spinning diamonds)
    int btnX = (w - BTN_WIDTH) / 2;
    int btnY = noticeY + 22;
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

    // Account for sound section + mute toggle + music toggle + notice above back button
    int soundSecY = cardsY + CARD_HEIGHT + 40;
    int toggleY = soundSecY + 15;
    int toggleH = 42;
    int musicToggleY = toggleY + toggleH + 12;
    int noticeY = musicToggleY + toggleH + 8;
    boolean inBrowser = "true".equals(System.getProperty("cheerpj.browser"));
    if (inBrowser) {
      noticeY += 20;
    }

    int btnX = (w - BTN_WIDTH) / 2;
    int btnY = noticeY + 22;

    return mx >= btnX && mx <= btnX + BTN_WIDTH && my >= btnY && my <= btnY + BTN_HEIGHT;
  }

  private boolean isOverMuteToggle(int mx, int my) {
    int w = getWidth();
    int h = getHeight();

    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    FontMetrics titleFm = getFontMetrics(titleFont);
    int titleY = h / 7 + titleFm.getAscent() / 2;
    int cardsY = titleY + 80;

    int soundSecY = cardsY + CARD_HEIGHT + 40;
    int toggleW = 200;
    int toggleH = 42;
    int toggleX = (w - toggleW) / 2;
    int toggleY = soundSecY + 15;

    return mx >= toggleX && mx <= toggleX + toggleW && my >= toggleY && my <= toggleY + toggleH;
  }

  private boolean isOverMusicToggle(int mx, int my) {
    int w = getWidth();
    int h = getHeight();

    Font titleFont = new Font("Dialog", Font.BOLD, TITLE_FONT_SIZE);
    FontMetrics titleFm = getFontMetrics(titleFont);
    int titleY = h / 7 + titleFm.getAscent() / 2;
    int cardsY = titleY + 80;

    int soundSecY = cardsY + CARD_HEIGHT + 40;
    int toggleW = 200;
    int toggleH = 42;
    int toggleX = (w - toggleW) / 2;
    int toggleY = soundSecY + 15;
    int musicToggleY = toggleY + toggleH + 12;

    return mx >= toggleX
        && mx <= toggleX + toggleW
        && my >= musicToggleY
        && my <= musicToggleY + toggleH;
  }

  private void updateHover(int mx, int my) {
    int oldCard = hoveredCard;
    boolean oldBack = hoveredBack;
    boolean oldMute = hoveredMuteToggle;
    boolean oldMusic = hoveredMusicToggle;

    hoveredCard = getCardIndex(mx, my);
    hoveredBack = isOverBackButton(mx, my);
    hoveredMuteToggle = isOverMuteToggle(mx, my);
    hoveredMusicToggle = isOverMusicToggle(mx, my);

    boolean interactable =
        hoveredCard >= 0 || hoveredBack || hoveredMuteToggle || hoveredMusicToggle;
    setCursor(
        interactable ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

    if (oldCard != hoveredCard
        || oldBack != hoveredBack
        || oldMute != hoveredMuteToggle
        || oldMusic != hoveredMusicToggle) {
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
    if (isOverMuteToggle(mx, my)) {
      audioManager.play(AudioManager.Sound.BUTTON_CLICK);
      boolean newMuted = !settings.isSoundMuted();
      settings.setSoundMuted(newMuted);
      audioManager.setMuted(newMuted);
      repaint();
      return;
    }
    if (isOverMusicToggle(mx, my)) {
      audioManager.play(AudioManager.Sound.BUTTON_CLICK);
      boolean newMusicMuted = !settings.isMusicMuted();
      settings.setMusicMuted(newMusicMuted);
      audioManager.setMusicMuted(newMusicMuted);
      repaint();
      return;
    }
    if (isOverBackButton(mx, my)) {
      audioManager.play(AudioManager.Sound.BUTTON_CLICK);
      onBack.run();
    }
  }
}
