package mazegame;

import java.awt.Color;

/**
 * Represents a single tile in the maze grid. Implementations include
 * {@link TileWall}, {@link TilePassage}, and {@link TileExit}.
 */
public interface Tile {

    void setColor(Color c);

    Color getColor();

    int getMinX();

    int getMinY();

    int getMaxX();

    int getMaxY();

    void setMinX(int x);

    void setMinY(int y);

    int getSize();

    /** Returns the image cache key used to look up this tile's sprite. */
    String getImageString();

    /** Returns the grass passage variant index for background rendering. */
    String getPassageImageId();
}
