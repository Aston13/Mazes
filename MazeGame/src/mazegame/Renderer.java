package mazegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Stack;

public class Renderer {
    private final BufferedImage view;
    private final int[] pixels;
    private Tile [][] tileArr;
    private RecursiveBacktracker rb1;
    private final int screenWidth;
    private final int screenHeight;
    private final int screenWidthHalf;
    private final int screenHeightHalf;
    
    private int startingX;
    private int startingY;
    private final int rowColAmount;

    
    private String playerMessage = "";
    private int tileWidth;
    private boolean displayMsg;
    private static final int DURATION = 5000;
    private long activatedAt = Long.MAX_VALUE;
    private int keyCount;
    private int keysRequired;
    AssetManager am;
    
    private BufferedImage playerImg = null;
    Stack<BufferedImage> nextPlayerAnimation = new Stack<>();

    public int[] getPixels() {
        return pixels;
    }
    
    public Renderer(int screenHeight, int screenWidth, int rowColAmount, int tileWH, AssetManager am)  {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.tileWidth = tileWH;
        screenWidthHalf = screenWidth/2;
        screenHeightHalf = screenHeight/2;
        this.rowColAmount = rowColAmount;
        keyCount = 0;
        keysRequired = (rowColAmount/10)*2;
        this.am = am;
        try {am.preloadImages();} catch (IOException e) {e.printStackTrace();}
        playerImg = am.getPreloadedImage("dogEast0");
        
        // Create a BufferedImage that represents the view
        view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);

        // Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
    }
    
    public void renderBackground(Graphics g) {
        // Sets background colour to black.
        g.drawImage(view, 0, 0, screenWidth, screenHeight, null);
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
                    if (x%2 == 0) {
                        g.drawImage(getImage("GrassPassage_" + tile.getPassageImageId()), tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
                    } else {
                        g.drawImage(getImage("GrassPassage_0"), tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
                    }
                     
                    if(tile.getImageString() == "Key"){
                        g.drawImage(am.getKeyFrame(), tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
                    }
                    g.drawImage(img, tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
                    
                    if (tile instanceof TileExit){
                        if (keyCount >= keysRequired) {
                            ((TileExit)tile).setAccessible(true);
                        }
                    }
                    
                    //g.setColor(tile.getColor());
                    //Sg.fillRect(tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize());
                }
            }
        }
    }
    
    public BufferedImage getImage(String imageName) {
        return am.getPreloadedImage(imageName);
    }
    
    public void renderHUD(Graphics g, Player p1, int level) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, screenWidth, 50);
        g.setColor(Color.WHITE);
                    Font overheadFont = (new Font("Serif", Font.PLAIN, 15));
            g.setFont(overheadFont);
        g.drawString("Keys: " +  String.valueOf(keyCount) + "/" + String.valueOf(keysRequired), 25, 20);
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

        //String dog = "dogEast";
        //g.drawImage(dogEast_0, screenWidthHalf, screenHeightHalf, null);
        if (nextPlayerAnimation.size() <= 10) {
            if(dir < 0) {
                    nextPlayerAnimation.push(getImage("dogEast0"));
                    nextPlayerAnimation.push(getImage("dogEast1"));
                    nextPlayerAnimation.push(getImage("dogEast2"));
                    nextPlayerAnimation.push(getImage("dogEast3"));
                    nextPlayerAnimation.push(getImage("dogEast4"));
                    nextPlayerAnimation.push(getImage("dogEast5"));
                    nextPlayerAnimation.push(getImage("dogEast6"));
            } else {
                    nextPlayerAnimation.push(getImage("dogWest0"));
                    nextPlayerAnimation.push(getImage("dogWest1"));
                    nextPlayerAnimation.push(getImage("dogWest2"));
                    nextPlayerAnimation.push(getImage("dogWest3"));
                    nextPlayerAnimation.push(getImage("dogWest4"));
                    nextPlayerAnimation.push(getImage("dogWest5"));
                    nextPlayerAnimation.push(getImage("dogWest6"));
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
        
        if (nextPlayerAnimation.size() <= 10) {
            if(dir > 0) {
                    nextPlayerAnimation.push(getImage("dogNorth0"));
                    nextPlayerAnimation.push(getImage("dogNorth1"));
                    nextPlayerAnimation.push(getImage("dogNorth2"));
                    nextPlayerAnimation.push(getImage("dogNorth3"));
                    nextPlayerAnimation.push(getImage("dogNorth4"));
                    nextPlayerAnimation.push(getImage("dogNorth5"));
            } else {
                    nextPlayerAnimation.push(getImage("dogSouth0"));
                    nextPlayerAnimation.push(getImage("dogSouth1"));
                    nextPlayerAnimation.push(getImage("dogSouth2"));
                    nextPlayerAnimation.push(getImage("dogSouth3"));
                    nextPlayerAnimation.push(getImage("dogSouth4"));
                    nextPlayerAnimation.push(getImage("dogSouth5"));
            }
        }
    }
    
    public int[] getTile(int pX, int pY, int pSize, int tileWH, int tileBorder) {
        
        Tilemap tm1 = new Tilemap(tileWH, tileBorder, rowColAmount);
        int x = pX +(pSize/2);
        int y = pY +(pSize/2);
        
        if ((tm1.getCurrentTile(x, y)) != null) {
            int currentTile [] = tm1.getCurrentTile(x, y);
            return currentTile;
        }
        return null;
    }
    
    public boolean checkCollision(int current [], MazeGame game) {
        Tile t = tileArr[current[0]][current[1]];
        
        if (t instanceof TileWall) {
            return false;
        } else if (t instanceof TileExit) {
            if (((TileExit)t).getAccessible()) {
                game.setGameState(false);
            } else {
                playerMessage = "The door is locked. Find " + (keysRequired-keyCount) + " more keys.";
                activatedAt = System.currentTimeMillis();
                setPlayerMessage(true);
            }
        } else {
            if (((TilePassage)t).hasItem()) {
                ((TilePassage)t).setItem(false);
                keyCount++;
                
            }
        }
        return true;
    }
    
    public void updateFrames() {
        if(nextPlayerAnimation.size() > 1) {
            playerImg = nextPlayerAnimation.pop();
        }
    }
    
    public void renderPlayer(Graphics g, Player p1, int size) {
         
//        g.setColor(p1.getColor());
//        g.fillOval(screenWidthHalf, screenHeightHalf, p1.getSize(), p1.getSize());
        g.drawImage(playerImg, screenWidthHalf, screenHeightHalf, size, size, null);

        if (displayMessage()) {
            g.setColor(Color.WHITE);
            Font overheadFont = (new Font("Serif", Font.PLAIN, 20));
            g.setFont(overheadFont);
                        FontMetrics fm = g.getFontMetrics();
            int halfTxtWidth = (fm.stringWidth(playerMessage)/2);

            g.drawString(playerMessage, (screenWidthHalf-halfTxtWidth)+tileWidth/2, screenHeightHalf);
        }
    }

    public void setPlayerMessage(boolean displayMsg) {
        this.displayMsg =  displayMsg;
    }
    
    public boolean displayMessage() {
        long activeFor = System.currentTimeMillis() - activatedAt;
        return activeFor >= 0 && activeFor <= DURATION;
    }
    
    public int getStartingX() {
        return startingX;
    }
    
    public int getStartingY() {
        return startingY;
    }
}
