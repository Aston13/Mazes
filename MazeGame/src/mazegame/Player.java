package mazegame;

public class Player {
    
    private int x;
    private int y;
    private int size;
    
    public Player (int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
}
