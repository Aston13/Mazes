package mazegame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Renderer {
    private final BufferedImage view;
    private final int[] pixels;
    private Tile [][] tileArr;
    private RecursiveBacktracker rb1;
    private final int screenWidth;
    private final int screenHeight;
    private int startingX;
    private int startingY;
    private int visitedTiles = 0;
    private final int rowColAmount;
    private BufferedImage wallImage = null;
    private BufferedImage passageImage = null;
    private BufferedImage exitImage = null;
    private BufferedImage keyImage = null;

    public int[] getPixels() {
        return pixels;
    }
    
    public Renderer(int screenHeight, int screenWidth, int rowColAmount)  {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.rowColAmount = rowColAmount;
        try { preloadImages();} catch (IOException e) {e.printStackTrace();}
        
        // Create a BufferedImage that represents the view
        view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);

        // Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
    }
    
    public void preloadImages() throws IOException {
        ImageIO.setUseCache(false);
        passageImage = ImageIO.read(getClass().getResourceAsStream("Assets\\DirtTile.png"));
        wallImage = ImageIO.read(getClass().getResourceAsStream("Assets\\GrassTile.png"));
        exitImage = ImageIO.read(getClass().getResourceAsStream("Assets\\ExitLocked.png"));
        keyImage = ImageIO.read(getClass().getResourceAsStream("Assets\\DirtTileKey.png")); 
    }
    
    public void renderBackground(Graphics g) {
        // Sets background colour to black.
        g.drawImage(view, 0, 0, view.getWidth(), view.getHeight(), null);
    }
    
    public void generateMaze(int tileWH, int tileBorder) {
        rb1 = new RecursiveBacktracker(tileWH, tileBorder, rowColAmount);
        tileArr = rb1.startGeneration();
        startingX = tileArr[rb1.getStartingX()][rb1.getStartingY()].getMinX();
        startingY = tileArr[rb1.getStartingX()][rb1.getStartingY()].getMinY();
    }
    
    public void centerMaze() {
        Tile centerTile = tileArr[rb1.getStartingX()][rb1.getStartingY()];
        int centerTileX = centerTile.getMinX();
        int centerTileY = centerTile.getMinY();
        int centerX = screenWidth/2;
        int centerY = screenHeight/2;
        int startingCenterDifferenceX;
        int startingCenterDifferenceY;
        
        startingCenterDifferenceX = centerX-centerTileX;
        startingCenterDifferenceY = centerY-centerTileY;
        
        for(int i = 0; i < rowColAmount; i++){    // No of rows/columns
            for (int x = 0; x < rowColAmount; x++) {  // No of rows/columns 
                Tile tile = tileArr[x][i];
                tile.setMinX(tile.getMinX()+startingCenterDifferenceX);
                tile.setMinY(tile.getMinY()+startingCenterDifferenceY);
            }
        }
    }
    
    Color c2 = Color.GREEN;
    
    public void renderMaze(Graphics g, int tileWH) {
        
        for(int i = 0; i < rowColAmount; i++){    // No of rows/columns
            for (int x = 0; x < rowColAmount; x++) {  // No of rows/columns
                Tile tile = tileArr[x][i];
                if((tile.getMinX() > -tileWH) && (tile.getMaxX() < screenWidth+tileWH) && (tile.getMinY() > -tileWH) && (tile.getMaxY() < screenHeight+tileWH)) {
                    BufferedImage img = getImage(tile.getImageString());
                    g.drawImage(img, tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
                    //g.setColor(tile.getColor());
                    //g.fillRect(tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize());
                }
            }
        }
    }
    
    public BufferedImage getImage(String imageName) {
        switch (imageName){
            case("Passage"):
                return passageImage;
            case("Wall"):
                return wallImage;
            case("Exit"):
                return exitImage;
            case("Key"):
                return keyImage;
            default:
                return null;
        }
    }
    
    public void renderPlayer(Graphics g, Player p1) {
        g.setColor(Color.red);
        g.fillOval(screenWidth/2, screenHeight/2, p1.getSize(), p1.getSize());
    }
    
    public void renderHUD(Graphics g, Player p1, int level) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, screenWidth, 50);
        g.setColor(Color.WHITE);
        g.drawString("Moves Taken: " +  visitedTiles, 25, 25);
        g.drawString("Level: " + level, 25, 40);
        
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
    
    public int[] getTile(int pX, int pY, int pSize, int tileWH, int tileBorder) {
        
        Tilemap tm1 = new Tilemap(tileWH, tileBorder, rowColAmount);
        int x = pX+(pSize/2);
        int y = pY+(pSize/2);
        
        if ((tm1.getCurrentTile(x, y)) != null) {
            int currentTile [] = tm1.getCurrentTile(x, y);
            return currentTile;
        }
        return null;
    }
    
    public boolean checkCollision(int current [], MazeGame game) {
        int currentX = current[0];
        int currentY = current[1];
    
        if (!(tileArr[currentX][currentY] instanceof TileWall)){
            if (tileArr[currentX][currentY] instanceof TileExit){            
                game.setGameState(false);
            } 
//            else if (tileArr[currentX][currentY] instanceof TilePassage) {
//                TilePassage e = (TilePassage) tileArr[currentX][currentY];
//                e.setPlayerExplored(true);
//                visitedTiles++;
//            }
            return true; // Is not a wall.
        }
        return false; // Is a wall.
    }
    
    public int getStartingX() {
        return startingX;
    }
    
    public int getStartingY() {
        return startingY;
    }
}
