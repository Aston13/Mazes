package mazegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * A JPanel that displays the game's offscreen buffer, scaled to fit with aspect-ratio-preserving
 * letterboxing. Uses Swing's {@code paintComponent} pipeline rather than direct {@code
 * Canvas.getGraphics()}, which ensures compatibility with CheerpJ's rendering bridge.
 */
public class GamePanel extends JPanel {

  private volatile BufferedImage buffer;

  public GamePanel() {
    setOpaque(true);
    setBackground(Color.BLACK);
    setDoubleBuffered(true);
  }

  /** Sets the offscreen buffer to display. Thread-safe. */
  public void setBuffer(BufferedImage buffer) {
    this.buffer = buffer;
  }

  @Override
  protected void paintComponent(Graphics graphics) {
    super.paintComponent(graphics);
    BufferedImage img = buffer;
    if (img == null) return;

    Graphics2D g = (Graphics2D) graphics;
    int cw = getWidth();
    int ch = getHeight();
    int imgW = img.getWidth();
    int imgH = img.getHeight();

    // Uniform scale preserving aspect ratio
    double scale = Math.min((double) cw / imgW, (double) ch / imgH);
    int scaledW = (int) (imgW * scale);
    int scaledH = (int) (imgH * scale);
    int offsetX = (cw - scaledW) / 2;
    int offsetY = (ch - scaledH) / 2;

    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(img, offsetX, offsetY, scaledW, scaledH, null);
  }
}
