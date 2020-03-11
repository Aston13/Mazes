package mazegame;

import java.awt.Color;

public class TilePassage implements Tile {
    
    private final int tileWH;
    private Color c1;
    private int rowNo;
    private int colNo;
    private int xPos;
    private int yPos;
    private boolean playerExplored;
    private boolean exitCheck;

    public TilePassage (int tileWH, int xPos, int yPos) {
        this.tileWH = tileWH;
        this.xPos = xPos;
        this.yPos = yPos;
        c1 = Color.BLACK;
        playerExplored = false;
        exitCheck = false;
    }
    
    public boolean getCheckedExitPath() {
        return exitCheck;
    }
    
    public void setCheckedExitPath(boolean checked) {
        exitCheck = checked;
        c1 = Color.ORANGE;
    }
    
    public void setPlayerExplored(boolean hasExplored){
        playerExplored = hasExplored;
        setColor(Color.darkGray);
    }
    
    public boolean getPlayerExplored(){
        return playerExplored;
    }
    
    public int getRowNo() {
        return rowNo;
    }

    public int getColNo() {
        return colNo;
    }

    public void setRowNo(int no) {
        rowNo = no;
    }

    public void setColNo(int no) {
        colNo = no;
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
        return "Passage";
    }
}
