package mazegame;

import java.awt.Color;

public class Tile {
    
    private int tileSize = 28;
    private Rectangle r1;
    private int yCount, xCount = 0;
    
    
    private Rectangle tiles[][] = new Rectangle[30][30];
    
    
    public Rectangle[][] getTileArr(){
        for(int y = 0; y < 900; y+=30) {
            
            for (int x = 0; x < 900; x+=30) {

                r1 = new Rectangle(tileSize, tileSize, x, y);
                tiles[yCount][xCount] = r1;
                xCount++;
            }
            xCount = 0;
            yCount++;
        }
        return tiles;
    }
   
    
}
