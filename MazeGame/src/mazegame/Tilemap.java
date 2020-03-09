package mazegame;

public class Tilemap {
    
    private final int tileWH;
    private Tile tile;
    private int yCount, xCount = 0;
    private final int rowColAmount;
    private int xCoord, yCoord = 0;
    private int pyCount, pxCount;
    private int tileBorder = 0;
    private final Tile tiles[][];    // No of rows/columns
    
    public Tilemap(int tileWH, int tileBorder, int rowColAmount) {
        this.tileWH = tileWH;
        this.tileBorder = tileWH-tileBorder;
        this.rowColAmount = rowColAmount;
        tiles = new Tile[rowColAmount][rowColAmount];
    }
    
    public Tile[][] getTileArr(){
        
        for(int y = 0; y < rowColAmount; y+=1) {  // Increments amount of tiles   
            for (int x = 0; x < rowColAmount; x+=1) {
                tile = new Tile(tileBorder, xCoord, yCoord);
                tiles[yCount][xCount] = tile;
                xCount++;
                xCoord += tileWH;
                
            }
            xCoord = 0;
            xCount = 0;
            yCount++;
            yCoord += tileWH;
        }
        
        return tiles;
    }
    
    public int getPassageCount(Tile tiles[][]) {
        int passages = 0;
        Tile t;
        
        for (Tile[] tile1 : tiles) {
            for (int y = 0; y < tiles.length; y++) {
                t = tile1[y];
                if (!t.isWall()){
                    passages++;
                }
            }
        }
        return passages;
    }
 
    public int[] getCurrentTile(int playerX, int playerY) {
        int currentTile[] = new int[2];
        pxCount = 0;
        pyCount = 0;
        
        for(int y = 0; y < rowColAmount*tileWH; y+=tileWH) {  // Increments amount of tiles   
            for (int x = 0; x < rowColAmount*tileWH; x+=tileWH) {
                
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
    
    public int getTileWH() {
        return tileWH;
    }
}