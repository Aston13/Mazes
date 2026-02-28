package mazegame;

public class Start {
    private static final int WINDOW_WIDTH = 650;
    private static final int WINDOW_HEIGHT = 650;
    
    private static final UI ui = new UI(WINDOW_WIDTH);
    private static final MazeGame game =  new MazeGame(WINDOW_WIDTH,
            WINDOW_HEIGHT, ui, 10);

    public static void main (String [] args){
        javax.swing.SwingUtilities.invokeLater(() -> game.runMenu());
    }
}
