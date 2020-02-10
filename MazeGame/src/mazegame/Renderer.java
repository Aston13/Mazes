/*
 * Aston Turner created this.
 */
package mazegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 * @author Aston Turner <16052488 @ herts.ac.uk>
 */
public class Renderer {
    private BufferedImage view;
    private int[] pixels;
    private Rectangle [][] tileArr;
    //Tilemap t1;
    //private Point pixelRow[];
    
    public int[] getPixels() {
        return pixels;
    }
    
    public Renderer(int screenHeight, int screenWidth) {
        
        // Create a BufferedImage that represents the view

        //t1 = new Tilemap(screenHeight, screenWidth);
        view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);

        // Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
    }
    
    public void render(Graphics g) {

        
//        for(int i = 0; i < pixels.length; i++) {
//            pixels[i] = (int) (Math.random() * 0xFFFFFF);
//        }
        
        g.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);
    }
    
    public void renderImage(BufferedImage img, int xPos, int yPos, int xZoom, int yZoom) {
        int[] imagePixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
                
        for (int i = 0; i < img.getHeight(); i++) {
            for (int j = 0; j < img.getWidth(); j++) {
                for (int yZoomPos = 0; yZoomPos < yZoom; yZoomPos++){
                    for (int xZoomPos = 0; xZoomPos < xZoom; xZoomPos++){
                        pixels[((j*xZoom) + xPos + xZoomPos)+((i*yZoom) + yPos + yZoomPos) * view.getWidth()] = imagePixels[j+i * img.getWidth()];
                    }
                }
            }
        }
    }
    
    public void generateMaze() {
        Tile t1 = new Tile();
        Rectangle [][] tileArr = t1.getTileArr();
        Algo a1 = new Algo();
        tileArr = a1.carvePassage(0, 0, tileArr);
        
        this.tileArr = tileArr;
    }
    
    public void renderMaze(Graphics g) {
        for(int i = 0; i < 30; i++){
            for (int x = 0; x < 30; x++) {
                Rectangle r1 = tileArr[x][i];
                    g.setColor(r1.getColor());
                    g.fillRect(r1.getX(), r1.getY(), r1.getSize(), r1.getSize());
            }
        }
    }
    
    public void renderTiles(Graphics g) {
        Tile t1 = new Tile();
        Rectangle [][] tileArr = t1.getTileArr();

        for(int i = 0; i < 30; i++){
            for (int x = 0; x < 30; x++) {
                Rectangle r1 = tileArr[x][i];
                if (x%2 == 0 && i%2 == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(r1.getX(), r1.getY(), r1.getSize(), r1.getSize());
                } else {
                    g.setColor(Color.black);
                    g.drawRect(r1.getX(), r1.getY(), r1.getSize(), r1.getSize());
                }
                  
            }
        }
    }
}
