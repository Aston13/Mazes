package mazegame;

public class Tilemap {
    
    private int tileWH;
    private Tile r1;
    private int yCount, xCount = 0;
    private int mazeWH;
    private int screenCenterH;
    private int screenCenterW;
    
    private int pyCount, pxCount;
    private int tileBorder = 0;
    private Tile tiles[][];    // No of rows/columns
    
    public Tilemap(int mazeWH, int tileWH, int tileBorder, int screenWidth, int screenHeight) {
        this.tileWH = tileWH;
        this.mazeWH = mazeWH;
        this.tileBorder = tileWH-tileBorder;
        this.screenCenterH = (screenHeight/2)-tileWH;
        this.screenCenterW = (screenWidth/2)-tileWH;
        
        double mz = new Double(mazeWH);
        double tz = new Double(tileWH);
        double rowColSize = 0.0;

        rowColSize = Math.ceil(mz/tz);
        tiles = new Tile[(int)rowColSize][(int)rowColSize];
    }
    
    public Tile[][] getTileArr(){
        
        for(int y = 0+screenCenterH; y < mazeWH+screenCenterH; y+=tileWH) {  // Increments amount of tiles   
            for (int x = 0+screenCenterW; x < mazeWH+screenCenterW; x+=tileWH) {
                
                r1 = new Tile(tileBorder, x, y);
                tiles[yCount][xCount] = r1;
                xCount++;
            }
            
            xCount = 0;
            yCount++;
        }
        
        return tiles;
    }
 
    public int[] getCurrentTile(int playerX, int playerY) {
        int currentTile[] = new int[2];
        pxCount = 0;
        pyCount = 0;
        
        for(int y = 0; y < mazeWH; y+=tileWH) {  // Increments amount of tiles   
            for (int x = 0; x < mazeWH; x+=tileWH) {
                
                if ((playerY >= y) && (playerY <= y+tileWH)){
                    if ((playerX >= x) && (playerX <= x+tileWH)){ 
                        currentTile[0] = pyCount;
                        currentTile[1] = pxCount;
                        return currentTile;
                    } 
                }
                pxCount++;
            }
            pxCount = 0;
            pyCount++;
        }
        return null;
    }
    
    public int getMazeWH() {
        return mazeWH;
    }
    
    public int getTileWH() {
        return tileWH;
    }
}