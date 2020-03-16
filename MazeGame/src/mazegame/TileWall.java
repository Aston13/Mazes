
package mazegame;

import java.awt.Color;
import java.util.Random;

public class TileWall implements Tile {
    
    private final int tileWH;
    private Color c1;
    private int xPos;
    private int yPos;
    private String neighbours; // NESW
    private String imgString = "wall";
    private String passageId;

    public TileWall (int tileWH, int xPos, int yPos) {
        this.tileWH = tileWH;
        this.xPos = xPos;
        this.yPos = yPos;
        neighbours = "0000"; // NESW
        c1 = Color.CYAN;
        passageId = String.valueOf(new Random().nextInt(4));
    }
    
    public void setPassageNeighbours(String bits) {
        neighbours = bits;
    }
    
    public String getPassageNeighbours() {
        return neighbours;
    }

    @Override
    public void setColor(Color c) {
        c1 = c;
    }

    @Override
    public Color getColor() {
        return c1;
    }

    @Override
    public int getMinY() {
        return yPos;
    }

    @Override
    public int getMaxX() {
        return xPos + tileWH;
    }

    @Override
    public int getMinX() {
        return xPos;
    }

    @Override
    public void setMinX(int x) {
        xPos = x;
    }

    @Override
    public void setMinY(int y) {
        yPos = y;
    }

    @Override
    public int getMaxY() {
        return yPos + tileWH;
    }

    @Override
    public int getSize() {
        return tileWH;
    }

    @Override
    public String getImageString() {
        return imgString + "_" + neighbours;
        
    }

    @Override
    public String getPassageImageId() {
        return passageId;
    }
}
