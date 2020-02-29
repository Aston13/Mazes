package mazegame;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

public class UI {
    private int windowWidth;
    private int windowHeight;
    private JPanel pane;

    public UI(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    public JPanel inGamePane() {
        pane = new JPanel();
        pane.setLayout(null);
        pane.setSize(windowWidth, windowWidth/15);
        pane.setFocusable(false);
        pane.setVisible(true);
        pane.add(tileSizeLabel());

        //add(pane);
        return pane;
    }

    public JLabel tileSizeLabel() {
        JLabel lbl = new JLabel();
        lbl.setText("Tile Size: ");
        lbl.setBounds(200,0,200,0);
        lbl.setSize(200,200);
        lbl.setOpaque(true);
        lbl.setBackground(Color.GRAY);
        lbl.setFocusable(false);
        lbl.setVisible(true);
        return lbl;
    }
    
    public JButton getPlayButton() {
        JButton b = new JButton("Play");
        //b.setBounds(windowWidth/2, windowHeight/2, 100, 100);

        b.setBackground(Color.magenta);
        b.setVisible(true);
        return b;
    }
}
