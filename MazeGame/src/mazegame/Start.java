package mazegame;

public class Start {
    private static final int windowWidth = 650;
    private static final int windowHeight = 650;
    private static final UI ui = new UI(windowWidth, windowHeight);
    private static final MazeGame game =  new MazeGame(windowWidth,
            windowHeight, ui, 10);

    public static void main (String [] args){
        //game.run();
        game.runMenu();

    }
}
