package mazegame;

public class Start {
    private static final int windowWidth = 650;
    private static final int windowHeight = 650;
    private static final UI ui = new UI(windowWidth, windowHeight);
    private static final MazeGame game =  new MazeGame(windowWidth,
            windowHeight, ui);
    private static final StartMenu menu = new StartMenu(windowWidth,
            windowHeight, ui);
    
    
    
    private static final StartCanvas doubleC =  new StartCanvas(windowWidth,
            windowHeight, ui);
    
    private static final KeyBinds kb =  new KeyBinds(windowWidth,
        windowHeight, ui);
    
    public static void main (String [] args){
        //menu.run();
        //.game.run();
        //doubleC.runMenu();
        //doubleC.runMaze();
        kb.run();
    }
}
