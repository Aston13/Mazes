package mazegame;

import java.util.Stack;
import java.util.Random;

public class RecursiveB extends Tilemap {
    
    private int newXPos;
    private int newYPos;
    private Tile [][] updateGrid;
    Stack<Tile> visitedTiles = new Stack<Tile>();
    private Tile visited;
    private int tileWH;
    
    private int maxSE = 0;
    private int minNW = 1;

    
    public RecursiveB (int mazeWH, int tileWH, int tileBorder) {
        super(mazeWH, tileWH, tileBorder);
        updateGrid = super.getTileArr();
        this.tileWH = tileWH;
        if ((mazeWH/tileWH) % 2 == 0) {
            this.maxSE = (mazeWH/tileWH)-3;
        } else {
            this.maxSE = (mazeWH/tileWH)-2;
        }
        
        
        
        
        
    }

    int[] directions = {1, 2, 3, 4}; // NESW

    public Tile[][] startGeneration() {
//        int startingXPos = new Random().nextInt(13+0); // [0...13]
//        int startingYPos = new Random().nextInt(13+0); // [0...13]
//        startingXPos = (startingXPos*2)+1; // [1...27]
//        startingYPos = (startingYPos*2)+1; // [1...27]
//        updateGrid[startingYPos][startingXPos].setVisited(true);
        
        int startingXPos = 1; // [0...13]
        int startingYPos = 1; // [0...13]
        updateGrid[startingYPos][startingXPos].setVisited(true);
        visited = new Tile(0, startingXPos, startingYPos);
        visitedTiles.push(visited);
        
        return carvePassage(startingXPos, startingYPos);
    }
    
    public Tile[][] carvePassage(int x, int y) {
        shuffleDirection(directions);
        for(int i = 0; i < directions.length; i++) {
            isCellValid(x,y,directions[i]);
        }
        
        if (visitedTiles.size() <= 1) {
            carvePassage(visitedTiles.pop().getMinX(), visitedTiles.pop().getMinY());
        } 
            return updateGrid;
    }
   
    
    private void shuffleDirection(int[] array) {
        int index;
        Random random = new Random();
        for (int i = 0; i < array.length - 1; i++) {
            index = random.nextInt(i + 1);
            if (index != i) {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }

    public void isCellValid(int x, int y, int direction){

        switch(direction){
            // North
            case 1:
                if ((y <= minNW)||(updateGrid[y-2][x].hasBeenVisited() == true)) {
                } else { 
                    visited = new Tile(30, x, y);
                    visitedTiles.push(visited);
                    
                    updateGrid[y-2][x].setVisited(true);
                    updateGrid[y-1][x].setVisited(true);
                    newYPos = y-2;
                    newXPos = x;
                    carvePassage(newXPos,newYPos);
                }

            // East
            case 2:
                if ((x >= maxSE) || (updateGrid[y][x+2].hasBeenVisited() == true)) {
                } else {
                    visited = new Tile(30, x, y);
                    visitedTiles.push(visited);
                    
                    updateGrid[y][x+2].setVisited(true);
                    updateGrid[y][x+1].setVisited(true);
                    newYPos = y;
                    newXPos = x+2;
                    carvePassage(newXPos,newYPos);
                }
                
            // South
            case 3:
                if ((y >= maxSE)||(updateGrid[y+2][x].hasBeenVisited() == true)) {
                } else {
                    visited = new Tile(30, x, y);
                    visitedTiles.push(visited);

                    updateGrid[y+2][x].setVisited(true);
                    updateGrid[y+1][x].setVisited(true);
                    newYPos = y+2;
                    newXPos = x;
                    carvePassage(newXPos,newYPos);
                }
                
            // West
            case 4:
                if ((x <= minNW)||(updateGrid[y][x-2].hasBeenVisited() == true)){
                } else {
                    visited = new Tile(30, x, y);
                    visitedTiles.push(visited);
                    updateGrid[y][x-2].setVisited(true);
                    updateGrid[y][x-1].setVisited(true);
                    newYPos = y;
                    newXPos = x-2;
                    carvePassage(newXPos,newYPos);
                }
        }
    } 
}
