package mazegame;

import java.awt.Color;

public interface Tile {
    
    public abstract void setColor(Color c);
    
    public abstract Color getColor();
    
    public abstract int getMinY();
    
    public abstract int getMaxX();
    
    public abstract int getMinX();
    
    public abstract void setMinX(int x);
    
    public abstract void setMinY(int y);
    
    public abstract int getMaxY();
    
    public abstract int getSize();
    
    public abstract String getImageString();
}
