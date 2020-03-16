package mazegame;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import javax.imageio.ImageIO;

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
    private BufferedImage wallImage = null;
    private BufferedImage passageImage = null;
    private BufferedImage keyImage = null;
    private BufferedImage lockedExitImage = null;
    private BufferedImage unlockedExitImage = null;
    private HashMap<String, BufferedImage> preloadedImages = new HashMap();
    
    private BufferedImage passage_0000  = null;
    private BufferedImage passage_0001  = null;
    private BufferedImage passage_0010  = null;
    private BufferedImage passage_0011 = null;
    private BufferedImage passage_0100 = null;
    private BufferedImage passage_0101 = null;
    private BufferedImage passage_0110 = null;
    private BufferedImage passage_0111 = null;
    private BufferedImage passage_1000 = null;
    private BufferedImage passage_1001 = null;
    private BufferedImage passage_1010 = null;
    private BufferedImage passage_1011 = null;
    private BufferedImage passage_1100 = null;
    private BufferedImage passage_1101 = null;
    private BufferedImage passage_1110 = null;
    private BufferedImage passage_1111 = null;
    
    BufferedImage dogEast0 = null;
    BufferedImage dogEast1 = null;
    BufferedImage dogEast2 = null;
    BufferedImage dogEast3 = null;
    BufferedImage dogEast4 = null;
    BufferedImage dogEast5 = null;
    BufferedImage dogEast6 = null;
    
    BufferedImage dogWest0 = null;
    BufferedImage dogWest1 = null;
    BufferedImage dogWest2 = null;
    BufferedImage dogWest3 = null;
    BufferedImage dogWest4 = null;
    BufferedImage dogWest5 = null;
    BufferedImage dogWest6 = null;
    
    BufferedImage dogNorth0 = null;
    BufferedImage dogNorth1 = null;
    BufferedImage dogNorth2 = null;
    BufferedImage dogNorth3 = null;
    BufferedImage dogNorth4 = null;
    BufferedImage dogNorth5 = null;
    
    BufferedImage dogSouth0 = null;
    BufferedImage dogSouth1 = null;
    BufferedImage dogSouth2 = null;
    BufferedImage dogSouth3 = null;
    BufferedImage dogSouth4 = null;
    BufferedImage dogSouth5 = null;
    
    private String playerMessage = "";
    private int tileWidth;
    private boolean displayMsg;
    private static final int DURATION = 5000;
    private long activatedAt = Long.MAX_VALUE;
    private int keyCount;
    private int keysRequired;
    
    private BufferedImage playerImg = null;
    Stack<BufferedImage> nextAnimation = new Stack<>();

    public int[] getPixels() {
        return pixels;
    }
    
    public Renderer(int screenHeight, int screenWidth, int rowColAmount, int tileWH)  {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.tileWidth = tileWH;
        screenWidthHalf = screenWidth/2;
        screenHeightHalf = screenHeight/2;
        this.rowColAmount = rowColAmount;
        keyCount = 0;
        keysRequired = (rowColAmount/10)*2;
        try { preloadImages();} catch (IOException e) {e.printStackTrace();}
        playerImg = dogEast0;
        
        // Create a BufferedImage that represents the view
        view = new BufferedImage(screenHeight, screenWidth, BufferedImage.TYPE_INT_RGB);

        // Create an array for pixels
        pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
    }
    
    public void preloadImages() throws IOException {
        ImageIO.setUseCache(false);
        passageImage = ImageIO.read(getClass().getResourceAsStream("Assets\\GrassTile.png"));
        wallImage = ImageIO.read(getClass().getResourceAsStream("Assets\\GrassTile.png"));
        keyImage = ImageIO.read(getClass().getResourceAsStream("Assets\\KeyOnly.png")); 
        lockedExitImage = ImageIO.read(getClass().getResourceAsStream("Assets\\ExitLocked.png"));
        unlockedExitImage = ImageIO.read(getClass().getResourceAsStream("Assets\\ExitUnlocked.png"));
        
        dogEast0 = ImageIO.read(getClass().getResourceAsStream("Assets\\right_0.png"));
        dogEast1 = ImageIO.read(getClass().getResourceAsStream("Assets\\right_1.png"));
        dogEast2 = ImageIO.read(getClass().getResourceAsStream("Assets\\right_2.png"));
        dogEast3 = ImageIO.read(getClass().getResourceAsStream("Assets\\right_3.png"));
        dogEast4 = ImageIO.read(getClass().getResourceAsStream("Assets\\right_4.png"));
        dogEast5 = ImageIO.read(getClass().getResourceAsStream("Assets\\right_5.png"));
        dogEast6 = ImageIO.read(getClass().getResourceAsStream("Assets\\right_6.png"));
        
        dogWest0 = ImageIO.read(getClass().getResourceAsStream("Assets\\left_0.png"));
        dogWest1 = ImageIO.read(getClass().getResourceAsStream("Assets\\left_1.png"));
        dogWest2 = ImageIO.read(getClass().getResourceAsStream("Assets\\left_2.png"));
        dogWest3 = ImageIO.read(getClass().getResourceAsStream("Assets\\left_3.png"));
        dogWest4 = ImageIO.read(getClass().getResourceAsStream("Assets\\left_4.png"));
        dogWest5 = ImageIO.read(getClass().getResourceAsStream("Assets\\left_5.png"));
        dogWest6 = ImageIO.read(getClass().getResourceAsStream("Assets\\left_6.png"));
        
        dogSouth0 = ImageIO.read(getClass().getResourceAsStream("Assets\\south_0.png"));
        dogSouth1 = ImageIO.read(getClass().getResourceAsStream("Assets\\south_1.png"));
        dogSouth2 = ImageIO.read(getClass().getResourceAsStream("Assets\\south_2.png"));
        dogSouth3 = ImageIO.read(getClass().getResourceAsStream("Assets\\south_3.png"));
        dogSouth4 = ImageIO.read(getClass().getResourceAsStream("Assets\\south_4.png"));
        dogSouth5 = ImageIO.read(getClass().getResourceAsStream("Assets\\south_5.png"));
        
        dogNorth0 = ImageIO.read(getClass().getResourceAsStream("Assets\\north_0.png"));
        dogNorth1 = ImageIO.read(getClass().getResourceAsStream("Assets\\north_1.png"));
        dogNorth2 = ImageIO.read(getClass().getResourceAsStream("Assets\\north_2.png"));
        dogNorth3 = ImageIO.read(getClass().getResourceAsStream("Assets\\north_3.png"));
        dogNorth4 = ImageIO.read(getClass().getResourceAsStream("Assets\\north_4.png"));
        dogNorth5 = ImageIO.read(getClass().getResourceAsStream("Assets\\north_5.png"));
        
        
        
        
        //Wall with passage in direction NESW | 0000
        
        passage_0000 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0000.png"));
        passage_0001 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0001.png"));
        passage_0010 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0010.png"));
        passage_0011 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0011.png"));
        passage_0100 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0100.png"));
        passage_0101 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0101.png"));
        passage_0110 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0110.png"));
        passage_0111 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_0111.png"));
        passage_1000 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1000.png"));
        passage_1001 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1001.png"));
        passage_1010 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1010.png"));
        passage_1011 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1011.png"));
        passage_1100 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1100.png"));
        passage_1101 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1101.png"));
        passage_1110 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1110.png"));
        passage_1111 = ImageIO.read(getClass().getResourceAsStream("Assets\\passage_1111.png"));
        
        preloadedImages.put("Passage", passageImage);
        preloadedImages.put("wall", wallImage);
        preloadedImages.put("Key", keyImage);
        preloadedImages.put("Locked Exit", lockedExitImage);
        preloadedImages.put("Open Exit", unlockedExitImage);
        
        preloadedImages.put("passage_0000", passage_0000);
        preloadedImages.put("passage_0001", passage_0001);
        preloadedImages.put("passage_0010", passage_0010);
        preloadedImages.put("passage_0011", passage_0011);
        preloadedImages.put("passage_0100", passage_0100);
        preloadedImages.put("passage_0101", passage_0101);
        preloadedImages.put("passage_0110", passage_0110);
        preloadedImages.put("passage_0111", passage_0111);
        preloadedImages.put("passage_1000", passage_1000);
        preloadedImages.put("passage_1001", passage_1001);
        preloadedImages.put("passage_1010", passage_1010);
        preloadedImages.put("passage_1011", passage_1011);
        preloadedImages.put("passage_1100", passage_1100);
        preloadedImages.put("passage_1101", passage_1101);
        preloadedImages.put("passage_1110", passage_1110);
        preloadedImages.put("passage_1111", passage_1111);
                
        
        
        
        
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
                    if(tile.getImageString() == "Key"){
                        g.drawImage(getImage("Key"), tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
                    }
                     g.drawImage(getImage("Passage"), tile.getMinX(), tile.getMinY(), tile.getSize(), tile.getSize(), null);
                    
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
        if(preloadedImages.containsKey(imageName)){
            return preloadedImages.get(imageName);
        }
        return null;
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
        if (nextAnimation.size() <= 10) {
            if(dir < 0) {
                    nextAnimation.push(dogEast0);
                    nextAnimation.push(dogEast1);
                    nextAnimation.push(dogEast2);
                    nextAnimation.push(dogEast3);
                    nextAnimation.push(dogEast4);
                    nextAnimation.push(dogEast5);
                    nextAnimation.push(dogEast6);
            } else {
                    nextAnimation.push(dogWest0);
                    nextAnimation.push(dogWest1);
                    nextAnimation.push(dogWest2);
                    nextAnimation.push(dogWest3);
                    nextAnimation.push(dogWest4);
                    nextAnimation.push(dogWest5);
                    nextAnimation.push(dogWest6);
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
        
        if (nextAnimation.size() <= 10) {
            if(dir > 0) {
                    nextAnimation.push(dogNorth0);
                    nextAnimation.push(dogNorth1);
                    nextAnimation.push(dogNorth2);
                    nextAnimation.push(dogNorth3);
                    nextAnimation.push(dogNorth4);
                    nextAnimation.push(dogNorth5);
            } else {
                    nextAnimation.push(dogSouth0);
                    nextAnimation.push(dogSouth1);
                    nextAnimation.push(dogSouth2);
                    nextAnimation.push(dogSouth3);
                    nextAnimation.push(dogSouth4);
                    nextAnimation.push(dogSouth5);
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
    
    public void updatePlayerFrame() {
        if(nextAnimation.size() > 1) {
            playerImg = nextAnimation.pop();
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
