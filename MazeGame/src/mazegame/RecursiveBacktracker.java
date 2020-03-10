package mazegame;

import java.awt.Color;
import java.util.Stack;
import java.util.Random;

public class RecursiveBacktracker extends Tilemap {
    
    private int newXPos;
    private int newYPos;
    private final Tile [][] updateGrid;
    private Tile [][] exitTileSet;
    Stack<Tile> visitedTiles = new Stack<>();
    private Tile visited;
    private final int rowColAmount;
    private int maxSE = 0;
    private final int minNW;
    private final int[] directions = {1, 2, 3, 4}; // NESW
    private int startingX;
    private int startingY;
    private int exitPathLength;
    private boolean loop = true;
    private int biggestStack = 1;
    private Tile furthestReached = new Tile(0,1,1);

    
    public RecursiveBacktracker (int tileWH, int tileBorder, int rowColAmount) {
        super(tileWH, tileBorder, rowColAmount);
        this.minNW = 1;
        this.rowColAmount = rowColAmount;
        exitPathLength = 1;
        updateGrid = super.getTileArr();
        maxSE = rowColAmount - 2;  
    }

    public Tile[][] startGeneration() {
        startingX = getRandomStartingCoord();
        startingY = getRandomStartingCoord();
        updateGrid[startingY][startingX].setVisited(true);
        visited = new Tile(0, startingX, startingY);
        visitedTiles.push(visited);
        Tile[][] tiles = setWinningTile(carvePassage(startingX, startingY));
        return tiles;
    }
    
    public int getRandomStartingCoord() {
        
        /* Ex. Maze size 0-11 has valid cells 1-9 (and odd numbers only) */
        int endRange = rowColAmount - 2;
	int randomCoord = new Random().nextInt((endRange) + 1); // Ex. [1-9]
        if (randomCoord%2 == 0) { 
            randomCoord += 1;
        }
        
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
            // Check junctions here?
        }
        if (visitedTiles.size() == 1) {
            carvePassage(visitedTiles.pop().getMinX(), visitedTiles.pop().getMinY());
        }
        return updateGrid;
    }
    
    
    
    public Tile[][] setWinningTile(Tile[][] tileSet) {
        
        visitedTiles.clear();
         
        Tile tile = tileSet[startingX][startingY];
        tile.setRowNo(startingX);
        tile.setColNo(startingY);
        
        
        visitedTiles.push(tile);
        exitTileSet = tileSet;
        // X+2 == South
        // X-2 == North
        // Y-2 == West
        // Y+2 == East

        exitTileSet[startingX][startingY].setCheckedExitPath(true);
        setExitPath(startingX, startingY);
        return exitTileSet;
    }
    
    public boolean setExitPath(int cX, int cY) {
        
        
        if (visitedTiles.size() > biggestStack){
            biggestStack = visitedTiles.size();
            furthestReached = visitedTiles.peek();
        }
        
        int pathCount = super.getPassageCount(exitTileSet);
        if (exitPathLength > pathCount/2) {
            
            visitedTiles.push(furthestReached);
            furthestReached.setExitPortal(true);
            visitedTiles.clear();
            return true;
        }

        shuffleDirection(directions);
        int direction = directions[1];
        
        
        for (int i = 0; i < directions.length; i++) {
            direction = directions[i];
       

        
            // Checks if the cell in direction[i] is a path cell.
            if(checkPath(direction, cX, cY)) {
                if (direction == 1){
                     // North
                        cX = cX-1;
                        setExitPath(cX, cY);
                        break;

                } else if (direction == 2) {
                    // East
                        cY = cY+1;
                        setExitPath(cX, cY);
                        break;

                } else if (direction == 3) {
                    // South
                        cX = cX+1;
                        setExitPath(cX, cY);
                        break;
                } else if (direction == 4) {
                    // West
                    cY = cY-1;
                    setExitPath(cX, cY);
                    break;
                }
            }
            
            if(i == directions.length-1){
                Tile t;

                if (visitedTiles.size() == 1) {
                    t = visitedTiles.peek();
                } else {
                    t = visitedTiles.pop();
                }
                setExitPath(t.getRowNo(), t.getColNo());
            }
        }
    
        
//        else if (exitPathLength < 10) {
//            // Reached a dead-end. Retain cells visited count.
//            // Pop to previous visited cell in stack and check for alternate routes.
//            Tile t2;
//            if (visitedTiles.size() == 1) {
//                t2 = visitedTiles.peek();
//            } else {
//                t2 = visitedTiles.pop();
//                t2.setColor(Color.yellow);
//            }
//            
//            cX = t2.getRowNo();
//            cY = t2.getColNo();
//            setExitPath(cX, cY);
//        }

        return false;
    }
    public boolean checkPath(int dir, int cX, int cY) {
        
        Tile t;
        if (dir == 1) {
                 //North -- Check tile is in a valid range and hasn't been visited already.
                if ((cX-1 >= minNW)) {
                    if (!exitTileSet[cX-1][cY].getCheckedExitPath()){
                        if (!exitTileSet[cX-1][cY].isWall()){
                            exitTileSet[cX-1][cY].setCheckedExitPath(true);
                            exitPathLength++;
                            t = exitTileSet[cX-1][cY];
                            t.setColNo(cY);
                            t.setRowNo(cX-1);
                            visitedTiles.push(t);
                            return true;
                        }
                    }
                }
        } else if (dir == 2) {
                // East
                if (cY+1 <= maxSE) {
                    if (!exitTileSet[cX][cY+1].getCheckedExitPath()) {
                        if (!exitTileSet[cX][cY+1].isWall()){
                            exitTileSet[cX][cY+1].setCheckedExitPath(true);
                            exitPathLength++;
                            t = exitTileSet[cX][cY+1];
                            t.setColNo(cY+1);
                            t.setRowNo(cX);
                            visitedTiles.push(t);
                            return true;
                        }
                    }
                }   
        } else if (dir == 3) {
                // South
                if (cX+1 <= maxSE) {
                    if (!exitTileSet[cX+1][cY].getCheckedExitPath()) {
                        if (!exitTileSet[cX+1][cY].isWall()){
                            exitTileSet[cX+1][cY].setCheckedExitPath(true);
                            exitPathLength++;
                            t = exitTileSet[cX+1][cY];
                            t.setColNo(cY);
                            t.setRowNo(cX+1);
                            visitedTiles.push(t);
                            return true;
                        }
                    }
                }
        } else if (dir == 4) {
                // West
                if (cY-1 >= minNW) {
                    if (!exitTileSet[cX][cY-1].getCheckedExitPath()) {
                        if (!exitTileSet[cX][cY-1].isWall()){
                            exitTileSet[cX][cY-1].setCheckedExitPath(true);
                            exitPathLength++;
                            t = exitTileSet[cX][cY-1];
                            t.setColNo(cY-1);
                            t.setRowNo(cX);
                            visitedTiles.push(t);
                            return true;
                        }
                    }
                } 
        } 
        return false;   
    }
    
    private void shuffleDirection(int[] array) {
        int index;
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
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
                if (!((y <= minNW)||(updateGrid[y-2][x].hasBeenVisited() == true))) {
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
                if (!((x >= maxSE) || (updateGrid[y][x+2].hasBeenVisited() == true))) {
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
                if (!((y >= maxSE)||(updateGrid[y+2][x].hasBeenVisited() == true))) {
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
                if (!((x <= minNW)||(updateGrid[y][x-2].hasBeenVisited() == true))) {
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
