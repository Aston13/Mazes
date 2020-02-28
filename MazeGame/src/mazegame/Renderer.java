package mazegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Renderer {
    private BufferedImage view;
    private int[] pixels;
    private Tile [][] tileArr;
    private RecursiveB rb1;


    private int screenWidth;
    private int screenHeight;
    
    private int startingX;
    private int startingY;

    public int[] getPixels() {
        return pixels;
    }
    
    public Renderer(int screenHeight, int screenWidth) {
        
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        
        // Create a BufferedImage that represents the view
        view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);

        // Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
    }
    
    public void render(Graphics g) {
        g.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);
    }
    
//    public void renderImage(BufferedImage img, int xPos, int yPos, int xZoom, int yZoom) {
//        int[] imagePixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
//                
//        for (int i = 0; i < img.getHeight(); i++) {
//            for (int j = 0; j < img.getWidth(); j++) {
//                for (int yZoomPos = 0; yZoomPos < yZoom; yZoomPos++){
//                    for (int xZoomPos = 0; xZoomPos < xZoom; xZoomPos++){
//                        pixels[((j*xZoom) + xPos + xZoomPos)+((i*yZoom) + yPos + yZoomPos) * view.getWidth()] = imagePixels[j+i * img.getWidth()];
//                    }
//                }
//            }
//        }
//    }
    
    public void generateMaze(int mazeWH, int tileWH, int tileBorder) {
        rb1 = new RecursiveB(mazeWH, tileWH, tileBorder, screenWidth, screenHeight);
        tileArr = rb1.startGeneration();
        startingX = tileArr[rb1.getStartingX()][rb1.getStartingY()].getMinY();
        startingY = tileArr[rb1.getStartingX()][rb1.getStartingY()].getMinY();
    }
    
    public int getStartingX() { 
        return startingX; 
    }
    
    public int getStartingY() {
        return startingY;
    }
    
    public void renderMaze(Graphics g, int numOfRowCol) {
        for(int i = 0; i < numOfRowCol; i++){    // No of rows/columns
            for (int x = 0; x < numOfRowCol; x++) {  // No of rows/columns
                Tile r1 = tileArr[x][i];
                    g.setColor(r1.getColor());
                    g.fillRect(r1.getMinX(), r1.getMinY(), r1.getSize(), r1.getSize());
            }
        }
    }
    
    
    
    public void moveMazeX(Graphics g, int numOfRowCol, int dir) {
        for(int i = 0; i < numOfRowCol; i++){    // No of rows/columns
            for (int x = 0; x < numOfRowCol; x++) {  // No of rows/columns
                Tile r1 = tileArr[x][i];
                    int currentX = r1.getMinX();
                    r1.setMinX(currentX+dir);
            }
        }
    }
    
    public void moveMazeY(Graphics g, int numOfRowCol, int dir) {
        for(int i = 0; i < numOfRowCol; i++){    // No of rows/columns
            for (int x = 0; x < numOfRowCol; x++) {  // No of rows/columns
                Tile r1 = tileArr[x][i];
                    int currentY = r1.getMinY();
                    r1.setMinY(currentY+dir);
            }
        }
    }
    
    
    
    public int[] getTile(int pX, int pY, int pSize, int mazeWH, int tileWH, int tileBorder) { 
        int x = pX+(pSize/2);
        int y = pY+(pSize/2);

        Tilemap tm1 = new Tilemap(mazeWH,tileWH,tileBorder, screenWidth, screenHeight);
        
        if ((tm1.getCurrentTile(x, y)) != null){
            int currentTile [] = tm1.getCurrentTile(x, y);  
            return currentTile;
        }
        return null;
    }
    
    public boolean checkCollision(int current []) {
        int currentX = current[0];
        int currentY = current[1];
    
        if (!(tileArr[currentX][currentY]).isWall()){
            // is not a wall
            tileArr[currentX][currentY].setPlayerExplored(true);
            return true;
            
        }
        
        // is a wall
        return false;
    }
    
    public void renderPlayer(Graphics g, Player p1) {
        g.setColor(Color.red);
        g.fillOval(getStartingX(),getStartingY(), p1.getSize(), p1.getSize());
    }
}
