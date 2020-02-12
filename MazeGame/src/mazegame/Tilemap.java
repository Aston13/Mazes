package mazegame;

public class Tilemap {
    
    private int tileWH;
    private Tile r1;
    private int yCount, xCount = 0;
    private int mazeWH;
    private int tileBorder = 0;
    private Tile tiles[][];    // No of rows/columns
    
    public Tilemap(int mazeWH, int tileWH, int tileBorder) {
        this.tileWH = tileWH;
        this.mazeWH = mazeWH;
        this.tileBorder = tileWH-tileBorder;
        
        double mz = new Double(mazeWH);
        double tz = new Double(tileWH);
        double rowColSize = Math.ceil(mz/tz);
        tiles = new Tile[(int)rowColSize][(int)rowColSize];
    }
    
    public Tile[][] getTileArr(){
        
        for(int y = 0; y < mazeWH; y+=tileWH) {  // Increments amount of tiles   
            for (int x = 0; x < mazeWH; x+=tileWH) {
                r1 = new Tile(tileBorder, x, y);
                tiles[yCount][xCount] = r1;
                xCount++;
            }
            
            xCount = 0;
            yCount++;
        }
        
        return tiles;
    }
    
    public int getMazeWH() {
        return mazeWH;
    }
    
    public int getTileWH() {
        return tileWH;
    }
}