/*
 * Aston Turner created this.
 */
package mazegame;

import java.awt.Color;
import javax.swing.JFrame; // Import Jframe class from graphics library
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.Runnable;
import java.lang.Thread;
import javax.imageio.ImageIO;

/**
 *
 * @author Aston Turner <16052488 @ herts.ac.uk>
 */
public class MazeGame extends JFrame implements Runnable {
    
    private Canvas view = new Canvas();
    int windowWidth = 900;
    int windowHeight = 900;
    private Renderer renderer;
    BufferedImage test = loadImage("./Assets/GrassTile.png");
    
    public MazeGame() {
        
        
        
        //setPreferredSize(new Dimension(windowWidth, windowHeight));
        //setBounds(0, 0, windowWidth, windowHeight); // x, y, width, height
        
        

        
        
        
        
        add(view); // Adds graphics component to JFrame
        setResizable(false);
        pack();
        
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setLocationRelativeTo(null); // Spawns window in centre of screen
        setVisible(true); // Makes app window visible
        
        view.createBufferStrategy(2); // object for buffer strategy
        
        renderer = new Renderer(windowWidth, windowHeight);
        
    }
    
    public void update() {
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(windowWidth, windowHeight);
    }
    
    private BufferedImage loadImage(String fileName) {
        try {
          BufferedImage image = ImageIO.read(MazeGame.class.getResource(fileName));
          BufferedImage formattedImage =  new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
          formattedImage.getGraphics().drawImage(image, 0, 0, null);
          return formattedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public void render() {
        BufferStrategy buffStrat = view.getBufferStrategy();
        Graphics g  = buffStrat.getDrawGraphics();
        super.paint(g); // Override
        
        
        
//        
//        renderer.renderImage(test, 0, 64, 2, 2);
//        renderer.renderImage(test, 32, 32, 2, 2);
//        renderer.renderImage(test, 64, 64, 2, 2);
//        renderer.renderImage(test, 96, 96, 2, 2);
        //renderer.renderImage(test, 32, 96, 2, 2);
        
        renderer.render(g);
        //renderer.renderTiles(g);
        //renderer.renderImage(test, 96, 96, 2, 2);
        renderer.renderMaze(g);
        
        
            
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
        renderer.generateMaze();
        
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
