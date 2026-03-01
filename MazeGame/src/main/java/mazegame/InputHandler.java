package mazegame;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * Centralises all keyboard and mouse input handling for the game. Manages WASD/arrow key movement,
 * pause/resume key dispatch, and pause-menu mouse clicks.
 */
public class InputHandler {

  /** Receives high-level input events from the handler. */
  public interface Listener {
    void onPauseRequested();

    void onResumeRequested();

    void onRestartRequested();

    void onMenuRequested();

    Player getPlayer();

    boolean isGameInProgress();

    boolean isPaused();

    /** Returns pause-menu button rectangles (resume, restart, menu). May be null before pause. */
    Rectangle getResumeBtn();

    Rectangle getRestartBtn();

    Rectangle getMenuBtn();

    /** Converts canvas-space coords to logical-space coords (for scaled rendering). */
    int[] toLogicalCoords(int screenX, int screenY);

    /** Sets the hovered pause-button index (0=resume, 1=restart, 2=menu, -1=none). */
    void setHoveredPauseBtn(int index);

    /** Returns the logical game width (for touch D-pad hit testing). */
    int getGameWidth();

    /** Returns the logical game height (for touch D-pad hit testing). */
    int getGameHeight();

    /** Called when a touch/mouse direction changes on the D-pad. */
    void onTouchDirection(int dir);

    /** Returns true if on-screen touch controls should be active. */
    boolean showTouchControls();
  }

  private final Listener listener;
  private KeyEventDispatcher globalDispatcher;

  public InputHandler(Listener listener) {
    this.listener = listener;
  }

  /** Installs movement key bindings (arrows + WASD) and in-game ESC on the given component. */
  public void bindMovementKeys(JComponent comp) {
    // Arrow keys
    bindKey(comp, KeyEvent.VK_UP, "Move North", false, e -> listener.getPlayer().setMoveN(true));
    bindKey(comp, KeyEvent.VK_RIGHT, "Move East", false, e -> listener.getPlayer().setMoveE(true));
    bindKey(comp, KeyEvent.VK_DOWN, "Move South", false, e -> listener.getPlayer().setMoveS(true));
    bindKey(comp, KeyEvent.VK_LEFT, "Move West", false, e -> listener.getPlayer().setMoveW(true));

    bindKey(comp, KeyEvent.VK_UP, "Stop North", true, e -> listener.getPlayer().setMoveN(false));
    bindKey(comp, KeyEvent.VK_RIGHT, "Stop East", true, e -> listener.getPlayer().setMoveE(false));
    bindKey(comp, KeyEvent.VK_DOWN, "Stop South", true, e -> listener.getPlayer().setMoveS(false));
    bindKey(comp, KeyEvent.VK_LEFT, "Stop West", true, e -> listener.getPlayer().setMoveW(false));

    // WASD
    bindKey(
        comp, KeyEvent.VK_W, "Move North WASD", false, e -> listener.getPlayer().setMoveN(true));
    bindKey(comp, KeyEvent.VK_D, "Move East WASD", false, e -> listener.getPlayer().setMoveE(true));
    bindKey(
        comp, KeyEvent.VK_S, "Move South WASD", false, e -> listener.getPlayer().setMoveS(true));
    bindKey(comp, KeyEvent.VK_A, "Move West WASD", false, e -> listener.getPlayer().setMoveW(true));

    bindKey(
        comp, KeyEvent.VK_W, "Stop North WASD", true, e -> listener.getPlayer().setMoveN(false));
    bindKey(comp, KeyEvent.VK_D, "Stop East WASD", true, e -> listener.getPlayer().setMoveE(false));
    bindKey(
        comp, KeyEvent.VK_S, "Stop South WASD", true, e -> listener.getPlayer().setMoveS(false));
    bindKey(comp, KeyEvent.VK_A, "Stop West WASD", true, e -> listener.getPlayer().setMoveW(false));

    // ESC during gameplay
    bindKey(
        comp,
        KeyEvent.VK_ESCAPE,
        "Exit",
        false,
        e -> {
          if (!listener.isPaused()) {
            listener.onPauseRequested();
          }
        });
  }

  /**
   * Registers a global key dispatcher for ESC/Space/R during pause, plus ESC to pause during
   * gameplay. Call {@link #removeGlobalDispatcher()} when the level ends.
   */
  public void installGlobalDispatcher() {
    globalDispatcher =
        e -> {
          if (e.getID() != KeyEvent.KEY_PRESSED) return false;
          int key = e.getKeyCode();

          if (listener.isGameInProgress() && !listener.isPaused() && key == KeyEvent.VK_ESCAPE) {
            listener.onPauseRequested();
            return true;
          }
          if (listener.isPaused()) {
            if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ESCAPE) {
              listener.onResumeRequested();
              return true;
            }
            if (key == KeyEvent.VK_R) {
              listener.onRestartRequested();
              return true;
            }
          }
          return false;
        };
    KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(globalDispatcher);
  }

  /** Removes the global key dispatcher installed by {@link #installGlobalDispatcher()}. */
  public void removeGlobalDispatcher() {
    if (globalDispatcher != null) {
      KeyboardFocusManager.getCurrentKeyboardFocusManager()
          .removeKeyEventDispatcher(globalDispatcher);
      globalDispatcher = null;
    }
  }

  /**
   * Installs a mouse listener on the given component for pause-menu button clicks (resume, restart,
   * main menu).
   */
  public void bindPauseMouseClicks(java.awt.Component target) {
    target.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            if (!listener.isPaused()) return;
            int[] logical = listener.toLogicalCoords(e.getX(), e.getY());
            java.awt.Point pt = new java.awt.Point(logical[0], logical[1]);
            Rectangle resume = listener.getResumeBtn();
            Rectangle restart = listener.getRestartBtn();
            Rectangle menu = listener.getMenuBtn();
            if (resume != null && resume.contains(pt)) {
              listener.onResumeRequested();
            } else if (restart != null && restart.contains(pt)) {
              listener.onRestartRequested();
            } else if (menu != null && menu.contains(pt)) {
              listener.onMenuRequested();
            }
          }
        });
    target.addMouseMotionListener(
        new MouseAdapter() {
          @Override
          public void mouseMoved(MouseEvent e) {
            if (!listener.isPaused()) {
              target.setCursor(java.awt.Cursor.getDefaultCursor());
              listener.setHoveredPauseBtn(-1);
              return;
            }
            int[] logical = listener.toLogicalCoords(e.getX(), e.getY());
            java.awt.Point pt = new java.awt.Point(logical[0], logical[1]);
            Rectangle resume = listener.getResumeBtn();
            Rectangle restart = listener.getRestartBtn();
            Rectangle menu = listener.getMenuBtn();
            int idx = -1;
            if (resume != null && resume.contains(pt)) idx = 0;
            else if (restart != null && restart.contains(pt)) idx = 1;
            else if (menu != null && menu.contains(pt)) idx = 2;
            listener.setHoveredPauseBtn(idx);
            target.setCursor(
                idx >= 0
                    ? java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR)
                    : java.awt.Cursor.getDefaultCursor());
          }
        });
  }

  /**
   * Installs mouse press/release/drag listeners for touch D-pad and pause icon. CheerpJ maps mobile
   * touch events to Swing mouse events, so this works on both desktop and mobile browsers.
   */
  public void bindTouchControls(java.awt.Component target) {
    if (!listener.showTouchControls()) return;

    MouseAdapter touchHandler =
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            if (listener.isPaused()) return;
            int[] logical = listener.toLogicalCoords(e.getX(), e.getY());
            int lx = logical[0];
            int ly = logical[1];
            int gw = listener.getGameWidth();
            int gh = listener.getGameHeight();

            // Check pause icon first
            if (TouchDpad.hitTestPause(lx, ly, gw)) {
              if (listener.isGameInProgress() && !listener.isPaused()) {
                listener.onPauseRequested();
              }
              return;
            }

            // Check D-pad
            int dir = TouchDpad.hitTestDpad(lx, ly, gw, gh);
            if (dir != TouchDpad.NONE) {
              applyDirection(dir, true);
              listener.onTouchDirection(dir);
            }
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            // Release all directions
            clearAllDirections();
            listener.onTouchDirection(TouchDpad.NONE);
          }

          @Override
          public void mouseDragged(MouseEvent e) {
            if (listener.isPaused()) return;
            int[] logical = listener.toLogicalCoords(e.getX(), e.getY());
            int dir =
                TouchDpad.hitTestDpad(
                    logical[0], logical[1], listener.getGameWidth(), listener.getGameHeight());
            // Update direction: clear old, apply new
            clearAllDirections();
            if (dir != TouchDpad.NONE) {
              applyDirection(dir, true);
            }
            listener.onTouchDirection(dir);
          }
        };
    target.addMouseListener(touchHandler);
    target.addMouseMotionListener(touchHandler);
  }

  private void applyDirection(int dir, boolean active) {
    Player p = listener.getPlayer();
    if (p == null) return;
    switch (dir) {
      case TouchDpad.NORTH:
        p.setMoveN(active);
        break;
      case TouchDpad.EAST:
        p.setMoveE(active);
        break;
      case TouchDpad.SOUTH:
        p.setMoveS(active);
        break;
      case TouchDpad.WEST:
        p.setMoveW(active);
        break;
      default:
        break;
    }
  }

  private void clearAllDirections() {
    Player p = listener.getPlayer();
    if (p == null) return;
    p.setMoveN(false);
    p.setMoveE(false);
    p.setMoveS(false);
    p.setMoveW(false);
  }

  /** Utility: binds a key action on a Swing component. */
  public static void bindKey(
      JComponent comp, int keyCode, String id, boolean onRelease, ActionListener al) {
    InputMap inMap = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actMap = comp.getActionMap();
    inMap.put(KeyStroke.getKeyStroke(keyCode, 0, onRelease), id);
    actMap.put(
        id,
        new AbstractAction() {
          @Override
          public void actionPerformed(ActionEvent e) {
            al.actionPerformed(e);
          }
        });
  }
}
