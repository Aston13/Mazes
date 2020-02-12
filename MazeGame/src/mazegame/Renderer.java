package mazegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Renderer {
    private BufferedImage view;
    private int[] pixels;
    private Tile [][] tileArr;

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
    
    public void generateMaze(int mazeWH, int tileWH, int tileBorder) {
        RecursiveB a1 = new RecursiveB(mazeWH, tileWH, tileBorder);
        tileArr = a1.startGeneration();
    }
    
    public void renderMaze(Graphics g, int numOfRowCol) {
        for(int i = 0; i < numOfRowCol; i++){    // No of rows/columns
            for (int x = 0; x < numOfRowCol; x++) {  // No of rows/columns
                Tile r1 = tileArr[x][i];
                    g.setColor(r1.getColor());
                    g.fillRect(r1.getX(), r1.getY(), r1.getSize(), r1.getSize());
            }
        }
    }
}
