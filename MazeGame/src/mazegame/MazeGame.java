package mazegame;

import javax.swing.JFrame; // Import JFrame class from graphics library
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;


public class MazeGame extends JFrame implements KeyListener {
    
    private Canvas view = new Canvas();
    private final int windowWidth;
    private final int windowHeight;
    private boolean gameInProgress = true;
    private Player p1;
    private final UI ui;
    private int mazeWH = 700;
    private final int tileWH = 20;
    private final int tileBorder = 1;
    private int numOfRowCol;
    private Renderer renderer;
    
    public MazeGame(int windowHeight, int windowWidth, UI ui) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.ui = ui;
        
        add(ui.inGamePane());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close

        
        renderer = new Renderer(windowWidth, windowHeight);
    }
    
    public void update() {
        int halfP = p1.getSize()/2;
        
        if (p1.getMoveN()) {
            int nextTile[] = renderer.getTile(p1.getX(),p1.getY()-(halfP-1), p1.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) { 
                BufferStrategy buffStrat = view.getBufferStrategy();
                Graphics g  = buffStrat.getDrawGraphics();
                renderer.moveMazeY(g, numOfRowCol, 1);
                p1.setY(p1.getY()-1);  
            }
        }
        if (p1.getMoveE()) { 
            int nextTile[] = renderer.getTile(p1.getX()+(halfP+1),p1.getY(), p1.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) { 
                BufferStrategy buffStrat = view.getBufferStrategy();
                Graphics g  = buffStrat.getDrawGraphics();
                renderer.moveMazeX(g, numOfRowCol, -1);
                p1.setX(p1.getX()+1); 
            }
        }
        if (p1.getMoveS()) {
            int nextTile[] = renderer.getTile(p1.getX(),p1.getY()+(halfP+1), p1.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) {
                BufferStrategy buffStrat = view.getBufferStrategy();
                Graphics g  = buffStrat.getDrawGraphics();
                renderer.moveMazeY(g, numOfRowCol, -1);
                p1.setY(p1.getY()+1);
            } 
        }
        if (p1.getMoveW()) { 
            int nextTile[] = renderer.getTile(p1.getX()-(halfP-1),p1.getY(), p1.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) {
                BufferStrategy buffStrat = view.getBufferStrategy();
                Graphics g  = buffStrat.getDrawGraphics();
                renderer.moveMazeX(g, numOfRowCol, 1);
                p1.setX(p1.getX()-1); 
            }
        }
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
        Long lastTime = System.nanoTime();
        double nanoSecondConversion = 100000000.0 / 60; // Updated 60 times per second
        double changeInSeconds = 0;
        
        add(view); // Adds graphics component to JFrame
        pack();
        view.createBufferStrategy(2); // object for buffer strategy
        setLocationRelativeTo(null); // Spawns window in centre of screen
        setVisible(true); // Makes app window visible
        addKeyListener(this);
        
        if ((mazeWH/tileWH) % 2 == 0) {
            this.mazeWH = mazeWH - (tileWH+1);
            this.numOfRowCol = Math.floorDiv(mazeWH, tileWH);
        } else {
            this.numOfRowCol = Math.floorDiv(mazeWH, tileWH);
        }
        
        renderer.generateMaze(mazeWH, tileWH, tileBorder);
        p1 = new Player(tileWH, tileWH, tileWH/2);
        render();

        while(gameInProgress) {
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
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(windowWidth, windowHeight);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_UP)||(e.getKeyCode() == KeyEvent.VK_W)){ p1.setMoveN(true);}
        else if ((e.getKeyCode() == KeyEvent.VK_RIGHT)||(e.getKeyCode() == KeyEvent.VK_D)){ p1.setMoveE(true); }
        else if ((e.getKeyCode() == KeyEvent.VK_DOWN)||(e.getKeyCode() == KeyEvent.VK_S)){ p1.setMoveS(true); }
        else if ((e.getKeyCode() == KeyEvent.VK_LEFT)||(e.getKeyCode() == KeyEvent.VK_A)){ p1.setMoveW(true); }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_UP)||(e.getKeyCode() == KeyEvent.VK_W)){ p1.setMoveN(false); }
        else if ((e.getKeyCode() == KeyEvent.VK_RIGHT)||(e.getKeyCode() == KeyEvent.VK_D)){ p1.setMoveE(false); }
        else if ((e.getKeyCode() == KeyEvent.VK_DOWN)||(e.getKeyCode() == KeyEvent.VK_S)){ p1.setMoveS(false); }
        else if ((e.getKeyCode() == KeyEvent.VK_LEFT)||(e.getKeyCode() == KeyEvent.VK_A)){ p1.setMoveW(false); }
    }
}
