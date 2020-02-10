package mazegame;

import java.awt.Color;

public class Rectangle {
    
    
    private int width;
    private int height;
    private int xPos;
    private int yPos;
    private Color c1;
    private boolean visited;
    
    public Rectangle(int width, int height, int xPos, int yPos){
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.c1 = Color.darkGray;
        visited = false;
        
    }
    
    public int getX() {
        return xPos;
    }
    
    public boolean hasBeenVisited(){
        return visited;
    }
    
    public void setVisited(boolean vis){
        visited = vis;
        c1 = Color.CYAN;
    }
    
    public Color getColor() {
        return c1;
    }
    
    public int getY() {
        return yPos;
    }
    
    public int getSize() {
        return width;
    }
}
