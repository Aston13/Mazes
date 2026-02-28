package mazegame;

import java.awt.Color;
import java.util.Random;

/**
 * An impassable wall tile. Stores which neighbouring tiles are passages (NESW bitmask) for
 * selecting the correct wall sprite.
 */
public class TileWall implements Tile {

  private final int tileWH;
  private Color color;
  private int xPos;
  private int yPos;
  private String neighbours;
  private final String passageId;

  public TileWall(int tileWH, int xPos, int yPos) {
    this.tileWH = tileWH;
    this.xPos = xPos;
    this.yPos = yPos;
    this.neighbours = "0000";
    this.color = Color.CYAN;
    this.passageId = String.valueOf(new Random().nextInt(4));
  }

  public void setPassageNeighbours(String bits) {
    neighbours = bits;
  }

  public String getPassageNeighbours() {
    return neighbours;
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
    return "wall_" + neighbours;
  }

  @Override
  public String getPassageImageId() {
    return passageId;
  }
}
