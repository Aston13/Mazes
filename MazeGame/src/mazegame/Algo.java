package mazegame;

import java.util.Collections;
import java.util.Random;

public class Algo {
    Tile t1 = new Tile();
    Rectangle [][] updateGrid = t1.getTileArr();
    int newXPos;
    int newYPos;
    int tileSize = 30;
    int[] directions = {1, 2, 3, 4};
    
    
    
    
    //public int seedOne = (int)Math.random()*30;
    //public int seedTwo = (int)Math.random()*30;
    
    public Rectangle[][] carvePassage(int x, int y, Rectangle grid[][]){
        shuffleArray(directions);
        
        for(int i = 0; i < directions.length; i++) {
            if(isCellValid(x,y,grid,directions[i])) {

               carvePassage(newXPos,newYPos,updateGrid); 
            }
        }
        return updateGrid;
    }
    
    
    private static void shuffleArray(int[] array) {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            if (index != i) {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }

    
    public boolean isCellValid(int x, int y, Rectangle grid[][], int direction){

        switch(direction){
            // North
            case 1:
                if (y <= 1){return false;}
                else if (grid[y-2][x].hasBeenVisited() == false){
                    this.updateGrid[y-2][x].setVisited(true);
                    this.updateGrid[y-1][x].setVisited(true);
                    newYPos = y-2;
                    newXPos = x;
                    return true;
                }
            
            // East
            case 2:
                if (x >= 27){return false;}
                else if (grid[y][x+2].hasBeenVisited() == false){
                    this.updateGrid[y][x+2].setVisited(true);
                    this.updateGrid[y][x+1].setVisited(true);
                    newYPos = y;
                    newXPos = x+2;
                    return true;
                }
                
            // West
            case 3:
                if (x <= 1){return false;}
                else if (grid[y][x-2].hasBeenVisited() == false){
                    this.updateGrid[y][x-2].setVisited(true);
                    this.updateGrid[y][x-1].setVisited(true);
                    newYPos = y;
                    newXPos = x-2;
                    return true;
                }
                
            // South
            case 4:
                if (y >= 27){return false;}
                else if (grid[y+2][x].hasBeenVisited() == false){
                    this.updateGrid[y+2][x].setVisited(true);
                    this.updateGrid[y+1][x].setVisited(true);
                    newYPos = y+2;
                    newXPos = x;
                    return true;
                }
        }
        
        return false;
    }
    

 
    
}
