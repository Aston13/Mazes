package mazegame;

/**
 * A square grid of {@link Tile} objects. Initialises all cells as
 * {@link TileWall} — the maze generator then carves passages.
 */
public class Tilemap {

    private final int tileWH;
    private final int tileBorderSize;
    private final int rowColAmount;
    private final Tile[][] tiles;

    /**
     * Creates a new tilemap grid.
     *
     * @param tileWH       pixel width/height of each tile
     * @param tileBorder   border inset (subtracted from tileWH for rendering)
     * @param rowColAmount number of rows and columns (must be odd for maze gen)
     */
    public Tilemap(int tileWH, int tileBorder, int rowColAmount) {
        this.tileWH = tileWH;
        this.tileBorderSize = tileWH - tileBorder;
        this.rowColAmount = rowColAmount;
        this.tiles = new Tile[rowColAmount][rowColAmount];
    }

    /**
     * Populates the grid with {@link TileWall} tiles and returns it.
     *
     * @return the initialised 2D tile array
     */
    public Tile[][] getTileArr() {
        int yCoord = 0;
        for (int row = 0; row < rowColAmount; row++) {
            int xCoord = 0;
            for (int col = 0; col < rowColAmount; col++) {
                tiles[row][col] = new TileWall(tileBorderSize, xCoord, yCoord);
                xCoord += tileWH;
            }
            yCoord += tileWH;
        }
        return tiles;
    }

    /**
     * Counts the number of {@link TilePassage} tiles in the given grid.
     *
     * @param grid the tile grid to scan
     * @return the passage count
     */
    public int getPassageCount(Tile[][] grid) {
        int passages = 0;
        for (Tile[] row : grid) {
            for (Tile tile : row) {
                if (tile instanceof TilePassage) {
                    passages++;
                }
            }
        }
        return passages;
    }

    /**
     * Determines which tile the given player coordinates fall within.
     *
     * @param playerX the player's x-coordinate
     * @param playerY the player's y-coordinate
     * @return a two-element array {@code [row, col]}, or {@code null} if out of bounds
     */
    public int[] getCurrentTile(int playerX, int playerY) {
        int tileRow = 0;
        for (int y = 0; y < rowColAmount * tileWH; y += tileWH) {
            int tileCol = 0;
            for (int x = 0; x < rowColAmount * tileWH; x += tileWH) {
                if (playerY >= y && playerY <= y + tileWH
                        && playerX >= x && playerX <= x + tileWH) {
                    return new int[]{tileRow, tileCol};
                }
                tileCol++;
            }
            tileRow++;
        }
        return null;
    }

    /** Returns the pixel width/height of each tile. */
    public int getTileWH() {
        return tileWH;
    }
}
