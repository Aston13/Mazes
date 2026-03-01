package mazegame;

/**
 * Manages the game thread and frame-rate–capped update/render cycle. Decoupled from the JFrame so
 * the threading model is explicit and testable.
 */
public class GameLoop {

  private static final int TARGET_FPS = 30;
  private static final long FRAME_TIME_NS = 1_000_000_000L / TARGET_FPS;
  private static final double TICK_CONVERSION = 100_000_000.0 / TARGET_FPS;
  private static final int PAUSE_SLEEP_MS = 50;

  /** Callback interface for the game to implement update/render hooks. */
  public interface Callbacks {
    void onUpdate();

    void onRender();

    void onAnimationTick();

    boolean isGameInProgress();

    boolean isPaused();

    /** Called each frame while paused. Returns true if an action was handled. */
    boolean handlePauseFrame();
  }

  private final Callbacks callbacks;
  private Thread thread;
  private volatile boolean running;

  public GameLoop(Callbacks callbacks) {
    this.callbacks = callbacks;
  }

  /** Starts the game loop on a new daemon thread. */
  public void start() {
    if (thread != null && thread.isAlive()) {
      return;
    }
    running = true;
    thread = new Thread(this::loop, "GameLoop");
    thread.setDaemon(true);
    thread.start();
  }

  /** Signals the loop to stop. Does not block. */
  public void stop() {
    running = false;
  }

  /** Blocks until the game loop thread has finished. */
  public void join() {
    if (thread != null) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  /** Returns true if the loop thread is alive. */
  public boolean isRunning() {
    return thread != null && thread.isAlive();
  }

  private void loop() {
    long lastTime = System.nanoTime();
    double changeTick = 0;
    double changeAnim = 0;

    while (running && callbacks.isGameInProgress()) {
      if (callbacks.isPaused()) {
        callbacks.handlePauseFrame();
        try {
          Thread.sleep(PAUSE_SLEEP_MS);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
        lastTime = System.nanoTime();
        continue;
      }

      long now = System.nanoTime();
      double elapsed = (now - lastTime) / TICK_CONVERSION;
      changeTick += elapsed;
      changeAnim += elapsed;

      while (changeTick >= 1) {
        callbacks.onUpdate();
        changeTick = 0;
      }

      while (changeAnim >= 10) {
        callbacks.onAnimationTick();
        changeAnim = 0;
      }

      callbacks.onRender();
      lastTime = now;

      // Cap frame rate
      long frameElapsed = System.nanoTime() - now;
      long sleepMs = (FRAME_TIME_NS - frameElapsed) / 1_000_000;
      if (sleepMs > 0) {
        try {
          Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }
}
