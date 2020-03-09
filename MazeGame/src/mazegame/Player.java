package mazegame;

public class Player {
    
    private int x;
    private int y;
    private final int size;
    private boolean moveN = false;
    private boolean moveE = false;
    private boolean moveS = false;
    private boolean moveW = false;
    
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

    public boolean getMoveN() {
        return moveN;
    }

    public void setMoveN(boolean moveN) {
        this.moveN = moveN;
    }

    public boolean getMoveE() {
        return moveE;
    }

    public void setMoveE(boolean moveE) {
        this.moveE = moveE;
    }

    public boolean getMoveS() {
        return moveS;
    }

    public void setMoveS(boolean moveS) {
        this.moveS = moveS;
    }

    public boolean getMoveW() {
        return moveW;
    }

    public void setMoveW(boolean moveW) {
        this.moveW = moveW;
    }
    
    
    
    public void setXY(int x, int y) {
        this.x = x;
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
