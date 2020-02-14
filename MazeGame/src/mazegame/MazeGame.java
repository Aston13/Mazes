package mazegame;

import javax.swing.JFrame; // Import Jframe class from graphics library
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.Runnable;
import java.lang.Thread;
import javax.imageio.ImageIO;


public class MazeGame extends JFrame implements Runnable, KeyListener {
    
    private Canvas view = new Canvas();
    private int windowWidth = 1000;
    private int windowHeight = 1000;
    
    private Player p1;
    int newX = 30;
    int newY = 30;
    
    
    private int mazeWH = 870; //1160- 40
    private int tileWH = 30;
    private int tileBorder = 0;
    
    private int numOfRowCol = Math.floorDiv(mazeWH, tileWH);
    
    
    private Renderer renderer;
    BufferedImage test = loadImage("./Assets/GrassTile.png");
    
    public MazeGame() {

        //setPreferredSize(new Dimension(windowWidth, windowHeight));
        //setBounds(0, 0, windowWidth, windowHeight); // x, y, width, height
        
        add(view); // Adds graphics component to JFrame
        //setResizable(false);
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setLocationRelativeTo(null); // Spawns window in centre of screen
        setVisible(true); // Makes app window visible
        
        view.createBufferStrategy(2); // object for buffer strategy
        renderer = new Renderer(windowWidth, windowHeight);
    }
    
    public void update() {
        //renderer.generateMaze(mazeWH, tileWH, tileBorder);

        
        

            
        
        if (p1.getMoveN()) { 
            p1.setY(p1.getY()-1);
            renderer.checkCollisions(p1, -1);
        }
        
        if (p1.getMoveE()) { p1.setX(p1.getX()+1);}
        if (p1.getMoveS()) { p1.setY(p1.getY()+1);}
        if (p1.getMoveW()) { p1.setX(p1.getX()-1);}
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

        renderer.render(g); // Renders background
        renderer.renderMaze(g, numOfRowCol);
        renderer.renderPlayer(g, p1);
        
        
        g.dispose(); // clears graphics memory
        buffStrat.show(); // Buffer has been written to and is ready to be put on screen
    }
    
    public void run() {
        BufferStrategy buffStrat = view.getBufferStrategy();
        renderer.generateMaze(mazeWH, tileWH, tileBorder);
        p1 = new Player(0,0,30);
        addKeyListener(this);
        render();

        Long lastTime = System.nanoTime();
        double nanoSecondConversion = 100000000.0 / 60; // Updated 60 times per second
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

    @Override
    public void keyTyped(KeyEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP){ p1.setMoveN(true); }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT){ p1.setMoveE(true); }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN){ p1.setMoveS(true); }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT){ p1.setMoveW(true); }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        
        if (e.getKeyCode() == KeyEvent.VK_UP){ p1.setMoveN(false); }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT){ p1.setMoveE(false); }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN){ p1.setMoveS(false); }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT){ p1.setMoveW(false); }
        
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
