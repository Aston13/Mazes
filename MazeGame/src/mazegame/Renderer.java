/*
 * Aston Turner created this.
 */
package mazegame;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 * @author Aston Turner <16052488 @ herts.ac.uk>
 */
public class Renderer {
    private BufferedImage view;
    private int[] pixels;
    
    public Renderer(int screenHeight, int screenWidth) {
        
        // Create a BufferedImage that represents the view
        view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);
        
        // Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
    }
    
    public void render(Graphics g) {
        
        for(int i = 0; i < pixels.length; i++) {
            pixels[i] = (int) (Math.random() * 0xFFFFFF);
        }
        g.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);
    }
}
