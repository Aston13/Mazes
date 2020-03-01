package mazegame;

import java.awt.BorderLayout;
import javax.swing.JFrame; // Import JFrame class from graphics library
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;

public class StartMenu extends JFrame implements ActionListener {

    private final int windowWidth;
    private final int windowHeight;
    private final UI ui;
    private JPanel pane = new JPanel();
    private JButton btn;
    
    public StartMenu(int windowHeight, int windowWidth, UI ui) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.ui = ui;

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close  
    }
    
    public void run() {
        btn = ui.getPlayButton();
        btn.addActionListener(this);
        btn.setActionCommand("Open");
        

        
        
        
        pane.add(btn, BorderLayout.CENTER);
        pack();
        add(pane);
        setLocationRelativeTo(null); // Spawns window in centre of screen
        setVisible(true); // Makes app window visible
        
    }
    
    public void runGame() {
        UI ui2 = new UI(windowWidth, windowHeight);
        MazeGame game =  new MazeGame(windowWidth,
            windowHeight, ui2);
        game.run();
        
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(windowWidth, windowHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if(cmd.equals("Open")){
            dispose();
            UI ui2 = new UI(windowWidth, windowHeight);
            MazeGame game =  new MazeGame(windowWidth,
            windowHeight, ui2);
            game.run();
        }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
