package mazegame;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class UI {
    private final int windowWidth;
    private final int windowHeight;

    public UI(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }
    
    public JLabel getLogo(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        int width = windowWidth;
        int height = 50;
        int x = (windowWidth-width)/2;
        int y = (windowWidth-height)/10;
        
        l.setBounds(x, y*2, width, height);
        l.setFont(new Font("Dialog", Font.PLAIN, 40));
        l.setForeground(Color.CYAN);
        l.setVisible(true);
        
        return l;
    }
    
    public JButton getTopButton(String text) {
        JButton b = new JButton(text);
        int width = 100;
        int height = 50;
        int x = (windowWidth-width)/2;
        int y = (windowWidth-height)/10;
        
        b.setBounds(x, y*4, width, height);
        b.setBackground(Color.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setVisible(true);
        
        return b;
    }
    
    public JButton getMidButton(String text) {
        JButton b = new JButton(text);
        int width = 100;
        int height = 50;
        int x = (windowWidth-width)/2;
        int y = (windowWidth-height)/10;
        
        b.setBounds(x, y*5, width, height);
        b.setBackground(Color.DARK_GRAY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setVisible(true);
        
        return b;
    }
}
