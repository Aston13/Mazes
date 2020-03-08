package mazegame;

import java.util.Stack;
import java.util.Random;

public class RecursiveB extends Tilemap {
    
    private int newXPos;
    private int newYPos;
    private final Tile [][] updateGrid;
    Stack<Tile> visitedTiles = new Stack<>();
    private Tile visited;
    private final int rowColAmount;
    private int maxSE = 0;
    private final int minNW;
    private final int[] directions = {1, 2, 3, 4}; // NESW
    private int startingX;
    private int startingY;

    
    public RecursiveB (int tileWH, int tileBorder, int rowColAmount) {
        super(tileWH, tileBorder, rowColAmount);
        this.minNW = 1;
        this.rowColAmount = rowColAmount;
        updateGrid = super.getTileArr();
        maxSE = rowColAmount - 2;  
    }

    

    public Tile[][] startGeneration() {
        
        startingX = getRandomStartingCoord();
        startingY = getRandomStartingCoord();
        updateGrid[startingY][startingX].setVisited(true);
        visited = new Tile(0, startingX, startingY);
        visitedTiles.push(visited);
        
        Tile[][] tiles = setRandomWinningTile(carvePassage(startingX, startingY));
        System.out.println(super.getPassageCount(tiles));
        return tiles;
    }
    
    public int getRandomStartingCoord() {
        
        /* Ex. Maze size 0-11 has valid cells 1-9 (and odd numbers only) */
        int endRange = rowColAmount - 2;
	int randomCoord = new Random().nextInt((endRange) + 1); // Ex. [1-9]
        if (randomCoord%2 == 0) { 
            System.out.println("even: " + randomCoord);
            randomCoord += 1;
        }
        
        System.out.println("new: " + randomCoord);
        
        return randomCoord;
    }
    
    public int getStartingX() {
        return startingX;
    }
    
    public int getStartingY() {
        return startingY;
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
    
//    public Tile[][] setEndTile(Tile[][] tileSet) {
//        
//    }
    
    public Tile[][] setRandomWinningTile(Tile[][] tileSet) {
        int colLen = tileSet[0].length-1;
        int rowLen = tileSet[1].length-1;
        
        int randCol = new Random().nextInt(colLen + 1);
        int randRow = new Random().nextInt(rowLen + 1);

        Tile tile = tileSet[randCol][randRow];
        if (!tile.isWall()) {
            tile.setExitPortal(true);
            tileSet[randCol][randRow] = tile;
        } else {
            setRandomWinningTile(tileSet);
        }
        return tileSet;
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
                    visited = new Tile(0, x, y);
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
                    visited = new Tile(0, x, y);
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
                    visited = new Tile(0, x, y);
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
                    visited = new Tile(0, x, y);
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
