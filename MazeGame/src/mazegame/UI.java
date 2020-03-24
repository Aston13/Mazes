package mazegame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class UI {
    private final int windowWH;
    

    /**
     *
     * @param windowWidth
     */
    public UI(int windowWidth) {
        this.windowWH = windowWidth;
    }
    
    public JLabel getLogo(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        int width = windowWH;
        int height = 50;
        int x = (windowWH-width)/2;
        int y = (windowWH-height)/10;
        
        l.setBounds(x, y*2, width, height);
        l.setFont(new Font("Dialog", Font.PLAIN, 40));
        l.setForeground(Color.CYAN);
        l.setVisible(true);
        
        return l;
    }
    
    public JButton getTopButton(String text) {
        JButton b = new JButton(text);
        int width = 175;
        int height = 50;
        int x = (windowWH-width)/2;
        int y = (windowWH-height)/10;
        
        b.setBounds(x, y*4, width, height);
        b.setBackground(Color.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setVisible(true);
        
        return b;
    }
    
    public JButton getMidButton(String text) {
        JButton b = new JButton(text);
        int width = 175;
        int height = 50;
        int x = (windowWH-width)/2;
        int y = (windowWH-height)/10;
        
        b.setBounds(x, y*5, width, height);
        b.setBackground(Color.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setVisible(true);
        
        return b;
    }
    
    public JButton getBottomButton(String text) {
        JButton b = new JButton(text);
        int width = 175;
        int height = 50;
        int x = (windowWH-width)/2;
        int y = (windowWH-height)/10;
        
        b.setBounds(x, y*6, width, height);
        b.setBackground(Color.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setVisible(true);
        
        return b;
    }
    
    public ArrayList<JPanel> getLevelPanels(String[] levelData, MazeGame mz) {
        ArrayList <JPanel> levelPanels = new ArrayList<>();
        
        int currentLevel = -1;
        String bestTime;
        
        // 0 = locked, 1 = completed, 2 = current
        int category;
        String labelStr;
        
        for (int i = 1; i < levelData.length; i++) {
            category = 0;
            bestTime = "---";
            labelStr = "";
            
            String []lineWords = levelData[i].split(",");
            if (lineWords[1].equalsIgnoreCase("completed")){
                category = 1;
            } else if (lineWords[1].equalsIgnoreCase("incomplete") && (currentLevel == -1)) {
                currentLevel = i;
                category = 2;
            }
            
            if (!lineWords[2].equals("-1")){bestTime = lineWords[2] + " seconds";}
            
            
            labelStr = "<html>Level: " + String.valueOf(i) + "<br/>"
                    + "Best Time: " + bestTime + "</html>";
            
            
            
            levelPanels.add(getLevelPanel(labelStr, category, mz, i));
        }
        
        
        return levelPanels;
    }
    
    public JPanel getLevelHeader(MazeGame mz) {
        JPanel p = new JPanel(new BorderLayout());
        int width = windowWH;
        int height = 100;
        
        JLabel title = new JLabel("Level Selection", SwingConstants.CENTER);
        
        title.setOpaque(true);
        title.setPreferredSize(new Dimension(windowWH, 50));
        title.setVisible(true);
        p.add(title, BorderLayout.NORTH);
        
        JButton reset = new JButton("Reset");
        reset.setBackground(Color.LIGHT_GRAY);
        reset.setForeground(Color.BLACK);
        reset.setPreferredSize(new Dimension(windowWH/2, 50));
        reset.setBorder(new CompoundBorder( // sets two borders
            BorderFactory.createMatteBorder(5, 5, 5, 5, Color.DARK_GRAY), // outer border
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        reset.setFocusPainted(false);
        reset.setVisible(true);
        reset.addActionListener((e) -> {
            mz.load(true);
            mz.save();
            mz.runLevelSelection();
        });
        p.add(reset, BorderLayout.EAST);
        
        JButton back = new JButton("Main Menu [Esc]");
        back.setBackground(Color.LIGHT_GRAY);
        back.setForeground(Color.BLACK);
        back.setPreferredSize(new Dimension(windowWH/2, 50));
        back.setBorder(new CompoundBorder( // sets two borders
        BorderFactory.createMatteBorder(5, 5, 5, 5, Color.DARK_GRAY), // outer border
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        back.setFocusPainted(false);
        back.setVisible(true);
        back.addActionListener((e) -> {
            mz.runMenu();
        });
        p.add(back, BorderLayout.WEST);
        
        
        
        
        
        p.setPreferredSize(new Dimension(width,height));
        p.setBackground(Color.red);
        
        
        return p;
        
    }
    
    public JPanel getLevelPanel(String s, int cat, MazeGame mz, int lvl){
        JPanel p = new JPanel(new GridLayout(1,1));
        int width = windowWH;
        int height = 50;
        
        // 0 = locked, 1 = completed, 2 = current
        int category = cat;
        
        p.setPreferredSize(new Dimension(width-50,height));
        p.setBackground(Color.GRAY);
        p.add(getLevelLabel(s));
        
        
        if (category  == 1) {
            p.add(getLevelButton("Replay", new Color(0, 150, 0, 255), mz, lvl));
        } else if (category == 2) {
            p.add(getLevelButton("Play", new Color(219, 167, 26, 255), mz, lvl));
        } else {
            p.add(getLockedLabel("Locked"));
        }
        
        p.setBackground(Color.gray);
        p.setOpaque(true);
        p.setVisible(true);
        return p;
    }
    
    public JButton getLevelButton(String text, Color col, MazeGame mz, int lvl) {
        
        JButton b = new JButton(text);
        b.setBackground(col);
        b.setForeground(Color.BLACK);
        b.setBorder(new CompoundBorder( // sets two borders
            BorderFactory.createMatteBorder(2, 0, 2, 2, Color.DARK_GRAY), // outer border
            BorderFactory.createEmptyBorder(2, 0, 2, 2)));
        b.setFocusPainted(false);
        b.setVisible(true);
        
        b.addActionListener((e) -> {
            mz.setCurrentLevel(lvl);
            mz.setGameState(true, "Level Select");
            mz.playSelectedLevel();
        });
        
        return b;
    }
    
    public JLabel getLevelLabel(String text) {
        JLabel l = new JLabel(text);
        l.setBackground(new Color(70,70,70, 125));
        l.setForeground(Color.BLACK);
        l.setBorder(new CompoundBorder( // sets two borders
            BorderFactory.createMatteBorder(2, 2, 2, 0, Color.DARK_GRAY), // outer border
            BorderFactory.createEmptyBorder(2, 2, 2, 0)));;
        l.setOpaque(true);
        l.setVisible(true);
        
        return l;
    }
    
    public JLabel getLockedLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setBackground(new Color(250, 50, 50, 125));
        l.setForeground(Color.BLACK);
        l.setBorder(new CompoundBorder( // sets two borders
            BorderFactory.createMatteBorder(2, 0, 2, 2, Color.DARK_GRAY), // outer border
            BorderFactory.createEmptyBorder(2, 0, 2, 2)));;
        l.setOpaque(true);
        l.setVisible(true);
        
        return l;
    }
}
