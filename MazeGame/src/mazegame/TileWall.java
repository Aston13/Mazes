
package mazegame;

import java.awt.Color;

public class TileWall implements Tile {
    
    private final int tileWH;
    private Color c1;
    private int xPos;
    private int yPos;

    public TileWall (int tileWH, int xPos, int yPos) {
        this.tileWH = tileWH;
        this.xPos = xPos;
        this.yPos = yPos;
        c1 = Color.CYAN;
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
        return "Wall";
    }
}
