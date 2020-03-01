package mazegame;

import java.awt.Color;
import java.util.Random;

public class Tile {

    private int tileWH;
    private int xPos;
    private int yPos;
    private Color c1;
    private boolean visited;
    private boolean wall;
    private boolean playerExplored;
    
    Random rand = new Random();
    float r = rand.nextFloat()/4f;
    float g = rand.nextFloat()/2f;
    float b = rand.nextFloat();
    Color randomColor = new Color(r, g, b);
    
    public Tile(int tileWH, int xPos, int yPos){
        this.tileWH = tileWH;
        this.xPos = xPos;
        this.yPos = yPos;
        this.c1 = Color.cyan;
        visited = false;
        wall = true;
        playerExplored = false;
    }
    

    
    public boolean hasBeenVisited(){
        return visited;
    }
    
    public void setVisited(boolean vis){
        visited = vis;
        wall = false;
        
        //setColor(randomColor.brighter().brighter());
        setColor(Color.black);
    }
    
    public void setPlayerExplored(boolean hasExplored){
        playerExplored = hasExplored;
        setColor(Color.darkGray);
    }
    
    public boolean getPlayerExplored(){
        return playerExplored;
    }
    
    public boolean isWall(){
        return wall;
    }
    
    public void setColor(Color c) {
        c1 = c;
    }
    
    public Color getColor() {
        return c1;
    }
    
    public int getMinY() {
        return yPos;
    }
    
    public int getMaxX(){
        return xPos + tileWH;
    }
    
    public int getMinX() {
        return xPos;
    }
    
    public void setMinX(int x) {
        xPos = x;
    }
    
    public void setMinY(int y) {
        yPos = y;
    }
    
    public int getMaxY(){
        return yPos + tileWH;
    }
    
    public int getSize() {
        return tileWH;
    }
}
