package mazegame;

import java.awt.Color;
import java.util.Random;

/**
 * A passable tile in the maze. May optionally hold a key item that the player must collect to
 * unlock the exit.
 */
public class TilePassage implements Tile {

  private final int tileWH;
  private Color color;
  private int rowNo;
  private int colNo;
  private int xPos;
  private int yPos;
  private boolean playerExplored;
  private boolean exitCheck;
  private boolean item;
  private String imageString;
  private final String passageId;

  public TilePassage(int tileWH, int xPos, int yPos) {
    this.tileWH = tileWH;
    this.xPos = xPos;
    this.yPos = yPos;
    this.color = Color.WHITE;
    this.passageId = String.valueOf(new Random().nextInt(4));
    setItem(false);
  }

  /**
   * Sets whether this passage contains a key item.
   *
   * @param item true if a key should be placed here
   */
  public void setItem(boolean item) {
    this.item = item;
    if (item) {
      imageString = "Key";
      setColor(Color.DARK_GRAY);
    } else {
      imageString = "Passage";
      setColor(Color.BLACK);
    }
  }

  public boolean hasItem() {
    return item;
  }

  public boolean getCheckedExitPath() {
    return exitCheck;
  }

  public void setCheckedExitPath(boolean checked) {
    exitCheck = checked;
    color = Color.ORANGE;
  }

  public void setPlayerExplored(boolean hasExplored) {
    playerExplored = hasExplored;
    setColor(Color.DARK_GRAY);
  }

  public boolean getPlayerExplored() {
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
