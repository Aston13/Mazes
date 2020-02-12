///*
// * Aston Turner created this.
// */
//package mazegame;
//
//import java.util.Random;
//
///**
// *
// * @author Aston Turner <16052488 @ herts.ac.uk>
// */
//public class BackupAlgo {
//package mazegame;
//
//import java.util.Random;
//
//public class RecursiveB extends Tilemap {
//    
//    private int mazeWH;
//    private int tileWH;
//    private int tileBorder;
//    private int newXPos;
//    private int newYPos;
//    private Tile [][] updateGrid;
//
//    
//    public RecursiveB (int mazeWH, int tileWH, int tileBorder) {
//        super(mazeWH, tileWH, tileBorder);
//        updateGrid = super.getTileArr();
//    }
//
//    int[] directions = {11, 22, 33, 44}; // NESW
//
//    public Tile[][] startGeneration() {
//        int startingXPos = new Random().nextInt(13+0); // [0...13]
//        int startingYPos = new Random().nextInt(13+0); // [0...13]
//        
//        startingXPos = (startingXPos*2)+1; // [1...27]
//        startingYPos = (startingYPos*2)+1; // [1...27]
//        
//        updateGrid[1][5].setVisited(true);  // y-x
//        return carvePassage(5, 1);          // x-y
//    }
//    
//    public Tile[][] carvePassage(int x, int y){
//        randomDir(directions);
//        
//        for(int i = 0; i < directions.length; i++) {
//            
//            if(isCellValid(x,y,directions[i]) == true) { // Currently at x,y - can I go direction i?
//                System.out.println("cell valid Direction: " + directions[i]);
//               carvePassage(newXPos,newYPos);
//               return updateGrid;
//            }
//        }  
//        return updateGrid;
//    }
//   
//    
//    private void randomDir(int[] array) {
//        int index;
//        Random random = new Random();
//        for (int i = 0; i < array.length - 1; i++) {
//            index = random.nextInt(i + 1);
//            if (index != i) {
//                array[index] ^= array[i];
//                array[i] ^= array[index];
//                array[index] ^= array[i];
//            }
//        }
//    }
//
//    public boolean isCellValid(int x, int y, int direction){
//
//        switch(direction){
//            // North
//            case 11:
//                if (y <= 1){return false;}
//                else if (updateGrid[y-2][x].hasBeenVisited() == false){
//                    updateGrid[y-2][x].setVisited(true);
//                    updateGrid[y-1][x].setVisited(true);
//                    newYPos = y-2;
//                    newXPos = x;
//                    System.out.println("North");
//                    return true;
//   
//                }
//
//            // East
//            case 22:
//                if (x >= 27){return false;}
//                else if (updateGrid[y][x+2].hasBeenVisited() == false){
//                    updateGrid[y][x+2].setVisited(true);
//                    updateGrid[y][x+1].setVisited(true);
//                    newYPos = y;
//                    newXPos = x+2;
//                    System.out.println("East");
//                    return true;
//                }
//                
//            // South
//            case 33:
//                if (y >= 27){return false;}
//                else if (updateGrid[y+2][x].hasBeenVisited() == false){
//                    updateGrid[y+2][x].setVisited(true);
//                    updateGrid[y+1][x].setVisited(true);
//                    newYPos = y+2;
//                    newXPos = x;
//                    System.out.println("South");
//                    return true;
//                    
//                }
//                
//            // West
//            case 44:
//                if (x <= 1){return false;}
//                else if (updateGrid[y][x-2].hasBeenVisited() == false){
//                    updateGrid[y][x-2].setVisited(true);
//                    updateGrid[y][x-1].setVisited(true);
//                    newYPos = y;
//                    newXPos = x-2;
//                    System.out.println("West");
//                    return true;
//                    
//                }
//        }
//        return false;
//    } 
//}
//}
