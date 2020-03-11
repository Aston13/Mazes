package mazegame;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Tile {

    private final int tileWH;
    private int xPos;
    private int yPos;
    private Color c1;
    private boolean visited;
    private boolean wall;
    private boolean playerExplored;
    private boolean exitPortal;
    private boolean exitCheck;
    private int rowNo;
    private int colNo;
    
    Random rand = new Random();
    float r = rand.nextFloat()/4f;
    float g = rand.nextFloat()/2f;
    float b = rand.nextFloat();
    Color randomColor = new Color(r, g, b);
    
    public Tile(int tileWH, int xPos, int yPos) {
        this.tileWH = tileWH;
        this.xPos = xPos;
        this.yPos = yPos;
        this.c1 = Color.cyan;
        exitCheck = false;
        exitPortal = false;
        visited = false;
        wall = true;
        playerExplored = false;
        
    }
    
    public void setExitPortal(boolean ex) {
        c1 = Color.GREEN;
        exitPortal = ex;
    }
    
    public boolean isExitPortal() {
        return exitPortal;
    }

    public boolean hasBeenVisited(){
        return visited;
    }
    
    public boolean getCheckedExitPath() {
        return exitCheck;
    }
    public void setCheckedExitPath(boolean checked) {
        exitCheck = checked;
        setColor(Color.ORANGE);
    }
    
    public int getRowNo () {
        return rowNo;
    }
    
    public int getColNo() {
        return colNo;
    }
    
    public void setRowNo (int no) {
        rowNo = no;
    }
    
    public void setColNo(int no) {
        colNo = no;
    }
    
    public void setVisited(boolean vis){
        visited = vis;
        wall = false;
        
        //setColor(randomColor.brighter().brighter());
        setColor(Color.black);
        
    }
    
    
    public void setPlayerExplored(boolean hasExplored){
        playerExplored = hasExplored;
        //setColor(Color.darkGray);
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
