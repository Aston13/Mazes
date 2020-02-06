/*
 * Aston Turner created this.
 */
package mazegame;

import java.awt.Color;
import javax.swing.JFrame; // Import Jframe class from graphics library
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.image.BufferStrategy;
import java.lang.Runnable;
import java.lang.Thread;

/**
 *
 * @author Aston Turner <16052488 @ herts.ac.uk>
 */
public class MazeGame extends JFrame implements Runnable {
    
    private Canvas view = new Canvas();
    int windowWidth = 1000;
    int windowHeight = 800;
    private Renderer renderer;
    
    public MazeGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setBounds(0, 0, windowWidth, windowHeight); // x, y, width, height
        setLocationRelativeTo(null); // Spawns window in centre of screen
        add(view); // Adds graphics component to JFrame
        
        setVisible(true); // Makes app window visible
        
        view.createBufferStrategy(3); // object for buffer strategy
        
        renderer = new Renderer(1000, getHeight());
    }
    
    public void update() {
    }

    
    public void render() {
        BufferStrategy buffStrat = view.getBufferStrategy();
        Graphics g  = buffStrat.getDrawGraphics();
        super.paint(g); // Override
        
        renderer.render(g);
            
//        /* Repaint the background each frame */
//        g.setColor(Color.BLACK);
//        g.fillRect(0, 0, getWidth(), getHeight());
//            
//            
//        g.setColor(Color.GREEN);
//        g.fillOval(x, 200, 50, 50);
        
        g.dispose(); // clears graphics memory
        buffStrat.show(); // Buffer has been written to and is ready to be put on screen
    }
    
    public void run() {
        BufferStrategy buffStrat = view.getBufferStrategy();
        
        Long lastTime = System.nanoTime();
        double nanoSecondConversion = 1000000000.0 / 60; // Updated 60 times per second
        double changeInSeconds = 0;
        
        while(true) {
            Long now = System.nanoTime();
            
            changeInSeconds += (now - lastTime) / nanoSecondConversion;            
            
            while(changeInSeconds >= 1) {
                update();
                changeInSeconds = 0;
            }
            render();
            lastTime = now;
        }
    }

    public static void main(String[] args) {
        MazeGame game = new MazeGame();
        Thread gameThread = new Thread(game); // Creates a new thread for execution
        gameThread.start(); // Calls run
        
    }
    
}
