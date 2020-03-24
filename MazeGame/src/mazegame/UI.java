package mazegame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
    
    public JPanel getLevelPanel(String s) {
        JPanel p = new JPanel(new GridLayout(1,1));
        int width = windowWH;
        int height = 100;

        
 
        p.setPreferredSize(new Dimension(width-50,height));
        p.setBackground(Color.DARK_GRAY);
        p.setForeground(Color.WHITE);
        
        p.add(getLevelLabel(s));
        p.add(getLevelButton("Play"));
        
        p.setVisible(true);

        
        return p;
    }
    
    public JButton getLevelButton(String text) {
        JButton b = new JButton(text);


        

        b.setBackground(Color.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setVisible(true);
        
        return b;
    }
    
    public JLabel getLevelLabel(String text) {
        JLabel l = new JLabel(text);


        l.setBackground(Color.DARK_GRAY);
        l.setForeground(Color.WHITE);
        l.setVisible(true);
        
        return l;
    }
}
