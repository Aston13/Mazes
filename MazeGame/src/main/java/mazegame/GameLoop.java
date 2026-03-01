package mazegame;

import javax.swing.Timer;

/**
 * Manages the frame-rate–capped update/render cycle using a {@link javax.swing.Timer}, which fires
 * on the EDT. This avoids cross-thread rendering issues and is fully compatible with CheerpJ's
 * browser runtime (Swing Timer maps to JavaScript {@code setInterval}).
 */
public class GameLoop {

  private static final int TARGET_FPS = 30;
  private static final int FRAME_DELAY_MS = 1000 / TARGET_FPS;
  private static final double TICK_CONVERSION = 100_000_000.0 / TARGET_FPS;

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
  private Timer timer;
  private Runnable onComplete;
  private long lastTime;
  private double changeTick;
  private double changeAnim;

  public GameLoop(Callbacks callbacks) {
    this.callbacks = callbacks;
  }

  /**
   * Sets a callback to run on the EDT when the game loop ends (i.e. when {@code isGameInProgress}
   * returns false).
   */
  public void setOnComplete(Runnable onComplete) {
    this.onComplete = onComplete;
  }

  /** Starts the game loop using a Swing Timer (fires on the EDT). */
  public void start() {
    if (timer != null && timer.isRunning()) {
      return;
    }
    lastTime = System.nanoTime();
    changeTick = 0;
    changeAnim = 0;
    timer = new Timer(FRAME_DELAY_MS, e -> tick());
    timer.setCoalesce(true);
    timer.start();
  }

  /** Stops the timer. Safe to call from any thread. */
  public void stop() {
    if (timer != null) {
      timer.stop();
    }
  }

  /** Stops the timer. Retained for API compatibility (Timer runs on EDT, no thread to join). */
  public void join() {
    stop();
  }

  /** Returns true if the timer is running. */
  public boolean isRunning() {
    return timer != null && timer.isRunning();
  }

  /** Single tick of the game loop — called on the EDT by the Swing Timer. */
  private void tick() {
    if (!callbacks.isGameInProgress()) {
      stop();
      if (onComplete != null) {
        onComplete.run();
      }
      return;
    }

    if (callbacks.isPaused()) {
      callbacks.handlePauseFrame();
      lastTime = System.nanoTime();
      return;
    }

    long now = System.nanoTime();
    double elapsed = (now - lastTime) / TICK_CONVERSION;
    changeTick += elapsed;
    changeAnim += elapsed;

    // Run enough updates to catch up — prevents movement from slowing when rendering is heavy
    while (changeTick >= 1) {
      callbacks.onUpdate();
      changeTick -= 1;
      // Cap to prevent spiral-of-death after long pauses / lag spikes
      if (changeTick > 5) {
        changeTick = 0;
        break;
      }
    }

    while (changeAnim >= 10) {
      callbacks.onAnimationTick();
      changeAnim -= 10;
    }

    callbacks.onRender();
    lastTime = now;
  }
}
