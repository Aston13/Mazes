package mazegame;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
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
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * A timed splash/intro screen shown once on game launch. Displays the mask image, the iconic quote
 * "Somebody stop me!", and developer credits. Fades in, holds, then auto-advances to the main menu.
 * Click or press any key to skip.
 */
public class SplashPanel extends JPanel {

  private static final Color BG_TOP = new Color(10, 10, 10);
  private static final Color BG_BOTTOM = new Color(20, 18, 16);
  private static final Color QUOTE_COLOR = new Color(80, 220, 80);
  private static final Color QUOTE_SHADOW = new Color(0, 60, 0);
  private static final Color CREDIT_COLOR = new Color(196, 149, 106);
  private static final Color CREDIT_DIM = new Color(120, 110, 100);
  private static final Color SKIP_HINT = new Color(100, 95, 90);

  /** Total splash duration in milliseconds (fade-in + hold + fade-out). */
  private static final int TOTAL_DURATION_MS = 4000;

  /** Fade-in phase duration. */
  private static final int FADE_IN_MS = 800;

  /** Fade-out phase duration (at the end). */
  private static final int FADE_OUT_MS = 600;

  /** Timer tick rate for animation. */
  private static final int TICK_MS = 30;

  private final BufferedImage maskImage;
  private final Runnable onFinished;
  private final long startTime;
  private Timer animTimer;
  private boolean finished;

  /**
   * Creates the splash panel.
   *
   * @param maskImage the mask image to display (may be null — text-only fallback)
   * @param onFinished callback invoked when splash completes or is skipped
   */
  public SplashPanel(BufferedImage maskImage, Runnable onFinished) {
    this.maskImage = maskImage;
    this.onFinished = onFinished;
    this.startTime = System.currentTimeMillis();
    setOpaque(true);
    setBackground(BG_TOP);
    setFocusable(true);

    // Animation timer — drives fade and auto-advance
    animTimer =
        new Timer(
            TICK_MS,
            e -> {
              long elapsed = System.currentTimeMillis() - startTime;
              if (elapsed >= TOTAL_DURATION_MS) {
                finish();
              } else {
                repaint();
              }
            });
    animTimer.start();

    // Click to skip
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            finish();
          }
        });

    // Any key to skip
    InputHandler.bindKey(this, KeyEvent.VK_SPACE, "SkipSplash", false, evt -> finish());
    InputHandler.bindKey(this, KeyEvent.VK_ESCAPE, "SkipSplashEsc", false, evt -> finish());
    InputHandler.bindKey(this, KeyEvent.VK_ENTER, "SkipSplashEnter", false, evt -> finish());
  }

  /** Ends the splash and invokes the callback (idempotent). */
  private void finish() {
    if (finished) return;
    finished = true;
    if (animTimer != null) {
      animTimer.stop();
    }
    onFinished.run();
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

    // Calculate master alpha for fade-in / fade-out
    long elapsed = System.currentTimeMillis() - startTime;
    float masterAlpha;
    if (elapsed < FADE_IN_MS) {
      masterAlpha = (float) elapsed / FADE_IN_MS;
    } else if (elapsed > TOTAL_DURATION_MS - FADE_OUT_MS) {
      masterAlpha = (float) (TOTAL_DURATION_MS - elapsed) / FADE_OUT_MS;
    } else {
      masterAlpha = 1.0f;
    }
    masterAlpha = Math.max(0f, Math.min(1f, masterAlpha));

    Composite origComposite = g.getComposite();
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, masterAlpha));

    // --- Mask image (centred, upper portion) ---
    int imgDisplaySize = Math.min(w, h) * 2 / 5;
    int imgX = (w - imgDisplaySize) / 2;
    int imgY = h / 6;
    if (maskImage != null) {
      g.setRenderingHint(
          RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(maskImage, imgX, imgY, imgDisplaySize, imgDisplaySize, null);
    }

    // --- Quote: "Somebody stop me!" ---
    int quoteY = imgY + imgDisplaySize + 40;
    Font quoteFont = new Font("Dialog", Font.BOLD | Font.ITALIC, 28);
    g.setFont(quoteFont);
    FontMetrics qfm = g.getFontMetrics();
    String quote = "\"Somebody stop me!\"";
    int quoteX = (w - qfm.stringWidth(quote)) / 2;

    // Shadow
    g.setColor(QUOTE_SHADOW);
    g.drawString(quote, quoteX + 2, quoteY + 2);
    // Main text — green "Mask" style
    g.setColor(QUOTE_COLOR);
    g.drawString(quote, quoteX, quoteY);

    // --- Credits ---
    int creditY = quoteY + 50;
    Font creditFont = new Font("Dialog", Font.BOLD, 16);
    g.setFont(creditFont);
    FontMetrics cfm = g.getFontMetrics();
    String credit = "An Aston13 Production";
    int creditX = (w - cfm.stringWidth(credit)) / 2;
    g.setColor(CREDIT_COLOR);
    g.drawString(credit, creditX, creditY);

    // Developer line
    Font devFont = new Font("Dialog", Font.PLAIN, 13);
    g.setFont(devFont);
    FontMetrics dfm = g.getFontMetrics();
    String devLine = "Developed by Aston13";
    int devX = (w - dfm.stringWidth(devLine)) / 2;
    g.setColor(CREDIT_DIM);
    g.drawString(devLine, devX, creditY + 26);

    g.setComposite(origComposite);

    // --- Skip hint (always visible, doesn't fade) ---
    Font hintFont = new Font("Dialog", Font.PLAIN, 11);
    g.setFont(hintFont);
    FontMetrics hfm = g.getFontMetrics();
    String hint = "Click or press any key to skip";
    g.setColor(SKIP_HINT);
    g.drawString(hint, (w - hfm.stringWidth(hint)) / 2, h - 20);
  }
}
