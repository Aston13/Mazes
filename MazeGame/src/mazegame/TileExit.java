package mazegame;

import java.awt.Color;
import java.util.Random;

/**
 * An exit tile that can be locked or unlocked. The player must collect
 * all required keys before the exit becomes accessible.
 */
public class TileExit implements Tile {

    private final int tileWH;
    private Color color;
    private int xPos;
    private int yPos;
    private int rowNo;
    private int colNo;
    private boolean accessible;
    private String imageString;
    private final String passageId;

    public TileExit(int tileWH, int xPos, int yPos) {
        this.tileWH = tileWH;
        this.xPos = xPos;
        this.yPos = yPos;
        this.passageId = String.valueOf(new Random().nextInt(4));
        setAccessible(false);
    }

    /**
     * Sets whether this exit is accessible (unlocked).
     *
     * @param access true to unlock, false to lock
     */
    public void setAccessible(boolean access) {
        accessible = access;
        if (access) {
            imageString = "Open Exit";
            setColor(Color.GREEN);
        } else {
            imageString = "Locked Exit";
            setColor(Color.RED);
        }
    }

    public boolean getAccessible() {
        return accessible;
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
        color = c;
    }

    @Override
    public Color getColor() {
        return color;
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
        return imageString;
    }

    @Override
    public String getPassageImageId() {
        return passageId;
    }
}
