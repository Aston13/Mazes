/*
 * Aston Turner created this.
 */
package mazegame;
import java.awt.Point;

/**
 *
 * @author Aston Turner <16052488 @ herts.ac.uk>
 */
public class Tilemap {
    
    private int width = 0;
    private int height = 0;
    
    private Point pixelRow[];
    private Point pixelColumn[];
    private Point oddRow[];
    private Point thickRow[];
    
    public Tilemap(int width, int height) {
        this.pixelRow = new Point[width];
        this.pixelColumn = new Point[height];
        this.width = 600;
        this.height = 600;
        
        this.makeRows();
        this.makeColumns();
    }
    
    public void makeRows() {
        for(int i = 0; i < pixelRow.length; i++) {
            pixelRow[i] = new Point(((width)*i),((width)*(i+1)));
        }
    }
    
    public void makeColumns() {
        for(int i = 0; i < pixelColumn.length; i++) {
            pixelColumn[i] = new Point(((height)*i), ((height)*(i+1)));
        }
    }
    
    public Point [] getOddRows() {
        
        
        int x = 0;
        oddRow = new Point[300];
        
        for (int i = 0; i < pixelRow.length; i+=2) {
            oddRow[x] = new Point(pixelRow[i].x, pixelRow[i].y);
            x++;
        }
        
        return oddRow;
    }
    
    public Point[] getThickRows() {
        int x = 0;
        thickRow = new Point[height/10];
        
        for (int i = 0; i < pixelRow.length; i+=10) {
                thickRow[x] = new Point(pixelRow[i].x, pixelRow[i].y);
                x++;
        }
        return thickRow;
    }
}
