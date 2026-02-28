package mazegame;

/**
 * Application entry point. Creates the game window and launches the main menu on the Event Dispatch
 * Thread.
 */
public class Start {

  private static final int WINDOW_SIZE = 650;
  private static final int INITIAL_GRID_SIZE = 10;

  /**
   * Launches Wesley's Way Out.
   *
   * @param args command-line arguments (unused)
   */
  public static void main(String[] args) {
    UI ui = new UI(WINDOW_SIZE);
    MazeGame game = new MazeGame(WINDOW_SIZE, WINDOW_SIZE, ui, INITIAL_GRID_SIZE);
    javax.swing.SwingUtilities.invokeLater(game::runMenu);
  }
}
