package mazegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;

/**
 * Factory for Swing UI components used in menus, level selection, and in-game screens. All
 * components use absolute positioning relative to the window size.
 */
public class UI {

  private static final int BUTTON_WIDTH = 175;
  private static final int BUTTON_HEIGHT = 50;
  private static final int LOGO_HEIGHT = 50;
  private static final int LOGO_FONT_SIZE = 40;
  private static final int HEADER_HEIGHT = 100;
  private static final int LEVEL_PANEL_HEIGHT = 50;
  private static final int BORDER_THICKNESS = 5;
  private static final int LEVEL_BORDER_THICKNESS = 2;

  private static final Color COMPLETED_COLOR = new Color(0, 150, 0, 255);
  private static final Color CURRENT_LEVEL_COLOR = new Color(219, 167, 26, 255);
  private static final Color LOCKED_COLOR = new Color(250, 50, 50, 125);
  private static final Color LEVEL_LABEL_BG = new Color(70, 70, 70, 125);

  private final int windowWH;

  /**
   * Creates a UI factory for the given window size.
   *
   * @param windowWidth the window width in pixels
   */
  public UI(int windowWidth) {
    this.windowWH = windowWidth;
  }

  /**
   * Creates the game logo label.
   *
   * @param text the logo text
   * @return a styled JLabel
   */
  public JLabel getLogo(String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    int x = 0;
    int ySlot = (windowWH - LOGO_HEIGHT) / 10;

    label.setBounds(x, ySlot * 2, windowWH, LOGO_HEIGHT);
    label.setFont(new Font("Dialog", Font.PLAIN, LOGO_FONT_SIZE));
    label.setForeground(Color.CYAN);
    label.setVisible(true);
    return label;
  }

  /**
   * Creates a menu button at the top vertical slot.
   *
   * @param text the button label
   * @return a styled JButton
   */
  public JButton getTopButton(String text) {
    return createMenuButton(text, 4);
  }

  /**
   * Creates a menu button at the middle vertical slot.
   *
   * @param text the button label
   * @return a styled JButton
   */
  public JButton getMidButton(String text) {
    return createMenuButton(text, 5);
  }

  /**
   * Creates a menu button at the bottom vertical slot.
   *
   * @param text the button label
   * @return a styled JButton
   */
  public JButton getBottomButton(String text) {
    return createMenuButton(text, 6);
  }

  private JButton createMenuButton(String text, int ySlotMultiplier) {
    JButton button = new JButton(text);
    int x = (windowWH - BUTTON_WIDTH) / 2;
    int ySlot = (windowWH - BUTTON_HEIGHT) / 10;

    button.setBounds(x, ySlot * ySlotMultiplier, BUTTON_WIDTH, BUTTON_HEIGHT);
    button.setBackground(Color.DARK_GRAY);
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setVisible(true);
    return button;
  }

  /**
   * Creates level selection panels from the saved level data.
   *
   * @param levelData the level data lines (index 0 is header)
   * @param game the game instance for action callbacks
   * @return a list of level panels
   */
  public ArrayList<JPanel> getLevelPanels(String[] levelData, MazeGame game) {
    ArrayList<JPanel> levelPanels = new ArrayList<>();

    int currentLevel = -1;

    for (int i = 1; i < levelData.length; i++) {
      String bestTime = "---";
      // 0 = locked, 1 = completed, 2 = current
      int category = 0;

      String[] lineWords = levelData[i].split(",");
      if (lineWords[1].equalsIgnoreCase("completed")) {
        category = 1;
      } else if (lineWords[1].equalsIgnoreCase("incomplete") && currentLevel == -1) {
        currentLevel = i;
        category = 2;
      }

      if (!lineWords[2].equals("-1")) {
        bestTime = lineWords[2] + " seconds";
      }

      String labelText = "<html>Level: " + i + "<br/>" + "Best Time: " + bestTime + "</html>";

      levelPanels.add(getLevelPanel(labelText, category, game, i));
    }

    return levelPanels;
  }

  /**
   * Creates the header panel for the level selection screen with "Main Menu" and "Reset" buttons.
   *
   * @param game the game instance for action callbacks
   * @return the header panel
   */
  public JPanel getLevelHeader(MazeGame game) {
    JPanel panel = new JPanel(new BorderLayout());

    JLabel title = new JLabel("Level Selection", SwingConstants.CENTER);
    title.setOpaque(true);
    title.setPreferredSize(new Dimension(windowWH, LOGO_HEIGHT));
    title.setVisible(true);
    panel.add(title, BorderLayout.NORTH);

    JButton reset = new JButton("Reset");
    reset.setBackground(Color.LIGHT_GRAY);
    reset.setForeground(Color.BLACK);
    reset.setPreferredSize(new Dimension(windowWH / 2, LOGO_HEIGHT));
    reset.setBorder(
        new CompoundBorder(
            BorderFactory.createMatteBorder(
                BORDER_THICKNESS,
                BORDER_THICKNESS,
                BORDER_THICKNESS,
                BORDER_THICKNESS,
                Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(
                BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS)));
    reset.setFocusPainted(false);
    reset.setVisible(true);
    reset.addActionListener(
        (e) -> {
          game.load(true);
          game.save();
          game.runLevelSelection();
        });
    panel.add(reset, BorderLayout.EAST);

    JButton back = new JButton("Main Menu [Esc]");
    back.setBackground(Color.LIGHT_GRAY);
    back.setForeground(Color.BLACK);
    back.setPreferredSize(new Dimension(windowWH / 2, LOGO_HEIGHT));
    back.setBorder(
        new CompoundBorder(
            BorderFactory.createMatteBorder(
                BORDER_THICKNESS,
                BORDER_THICKNESS,
                BORDER_THICKNESS,
                BORDER_THICKNESS,
                Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(
                BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS, BORDER_THICKNESS)));
    back.setFocusPainted(false);
    back.setVisible(true);
    back.addActionListener((e) -> game.runMenu());
    panel.add(back, BorderLayout.WEST);

    panel.setPreferredSize(new Dimension(windowWH, HEADER_HEIGHT));
    panel.setBackground(Color.RED);
    return panel;
  }

  /**
   * Creates a single level panel with label and play/replay/locked button.
   *
   * @param labelText the HTML-formatted label text
   * @param category 0 = locked, 1 = completed, 2 = current
   * @param game the game instance for action callbacks
   * @param level the level number
   * @return the level panel
   */
  public JPanel getLevelPanel(String labelText, int category, MazeGame game, int level) {
    JPanel panel = new JPanel(new GridLayout(1, 1));
    panel.setPreferredSize(new Dimension(windowWH - LOGO_HEIGHT, LEVEL_PANEL_HEIGHT));
    panel.setBackground(Color.GRAY);
    panel.add(getLevelLabel(labelText));

    if (category == 1) {
      panel.add(getLevelButton("Replay", COMPLETED_COLOR, game, level));
    } else if (category == 2) {
      panel.add(getLevelButton("Play", CURRENT_LEVEL_COLOR, game, level));
    } else {
      panel.add(getLockedLabel("Locked"));
    }

    panel.setOpaque(true);
    panel.setVisible(true);
    return panel;
  }

  /**
   * Creates a play/replay button for a level panel.
   *
   * @param text the button label ("Play" or "Replay")
   * @param color the button background colour
   * @param game the game instance for action callbacks
   * @param level the level number to launch
   * @return a styled JButton
   */
  public JButton getLevelButton(String text, Color color, MazeGame game, int level) {
    JButton button = new JButton(text);
    button.setBackground(color);
    button.setForeground(Color.BLACK);
    button.setBorder(
        new CompoundBorder(
            BorderFactory.createMatteBorder(
                LEVEL_BORDER_THICKNESS,
                0,
                LEVEL_BORDER_THICKNESS,
                LEVEL_BORDER_THICKNESS,
                Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(
                LEVEL_BORDER_THICKNESS, 0, LEVEL_BORDER_THICKNESS, LEVEL_BORDER_THICKNESS)));
    button.setFocusPainted(false);
    button.setVisible(true);

    button.addActionListener(
        (e) -> {
          game.setCurrentLevel(level);
          game.setGameState(true, "Level Select");
          game.playSelectedLevel();
        });

    return button;
  }

  private JLabel getLevelLabel(String text) {
    JLabel label = new JLabel(text);
    label.setBackground(LEVEL_LABEL_BG);
    label.setForeground(Color.BLACK);
    label.setBorder(
        new CompoundBorder(
            BorderFactory.createMatteBorder(
                LEVEL_BORDER_THICKNESS,
                LEVEL_BORDER_THICKNESS,
                LEVEL_BORDER_THICKNESS,
                0,
                Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(
                LEVEL_BORDER_THICKNESS, LEVEL_BORDER_THICKNESS, LEVEL_BORDER_THICKNESS, 0)));
    label.setOpaque(true);
    label.setVisible(true);
    return label;
  }

  private JLabel getLockedLabel(String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setBackground(LOCKED_COLOR);
    label.setForeground(Color.BLACK);
    label.setBorder(
        new CompoundBorder(
            BorderFactory.createMatteBorder(
                LEVEL_BORDER_THICKNESS,
                0,
                LEVEL_BORDER_THICKNESS,
                LEVEL_BORDER_THICKNESS,
                Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(
                LEVEL_BORDER_THICKNESS, 0, LEVEL_BORDER_THICKNESS, LEVEL_BORDER_THICKNESS)));
    label.setOpaque(true);
    label.setVisible(true);
    return label;
  }
}
