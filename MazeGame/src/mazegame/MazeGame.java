package mazegame;

import java.awt.BorderLayout;
import javax.swing.JFrame; // Import JFrame class from graphics library
import java.awt.Graphics;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class MazeGame extends JFrame implements Runnable {
    
    private final Canvas gameView = new Canvas();
    private final int windowWidth;
    private final int windowHeight;
    private boolean gameInProgress = true;
    private Player player;
    private final UI ui;
    private int mazeWH = 700;
    private final int tileWH = 20;
    private final int tileBorder = 0;
    private int numOfRowCol;
    private final Renderer renderer;
    private final JPanel pane = new JPanel(new GridLayout());
    
    public MazeGame (int windowHeight, int windowWidth, UI ui) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.ui = ui;
        renderer = new Renderer(windowWidth, windowHeight);
        
    }
    
    public void setUpFrame() {
        
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setContentPane(pane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void update() {
        int halfP = player.getSize()/2;
        BufferStrategy buffStrat = gameView.getBufferStrategy();
        Graphics g  = buffStrat.getDrawGraphics();
        
        if (player.getMoveN()) {
            int nextTile[] = renderer.getTile(player.getX(),player.getY()-(halfP-1), player.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) { 
                renderer.moveMazeY(g, numOfRowCol, 1);
                player.setY(player.getY()-1);  
            }
        }
        if (player.getMoveE()) { 
            int nextTile[] = renderer.getTile(player.getX()+(halfP+1),player.getY(), player.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) { 
                renderer.moveMazeX(g, numOfRowCol, -1);
                player.setX(player.getX()+1); 
            }
        }
        if (player.getMoveS()) {
            int nextTile[] = renderer.getTile(player.getX(),player.getY()+(halfP+1), player.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) {
                renderer.moveMazeY(g, numOfRowCol, -1);
                player.setY(player.getY()+1);
            } 
        }
        if (player.getMoveW()) { 
            int nextTile[] = renderer.getTile(player.getX()-(halfP-1),player.getY(), player.getSize(), mazeWH, tileWH, tileBorder);
            if(renderer.checkCollision(nextTile)) {
                renderer.moveMazeX(g, numOfRowCol, 1);
                player.setX(player.getX()-1); 
            }
        }
    }
    
    public void render() {
        BufferStrategy buffStrat = gameView.getBufferStrategy();
        Graphics g  = buffStrat.getDrawGraphics();
        super.paint(g); // Override

        renderer.render(g); // Renders background
        renderer.renderMaze(g, numOfRowCol);
        renderer.renderPlayer(g, player);
        renderer.renderHUD(g, player);

        g.dispose(); // clears graphics memory
        buffStrat.show(); // Buffer has been written to and is ready to be put on screen
    }
    
    public void run() {
        Long lastTime = System.nanoTime();
        double nanoSecondConversion = 100000000.0 / 60; // Updated 60 times per second
        double changeInSeconds = 0;
        setNESWKeys(pane);

        setUpFrame();
        pane.add(gameView);
        gameView.createBufferStrategy(2);

        if ((mazeWH/tileWH) % 2 == 0) {
            this.mazeWH = mazeWH - (tileWH+1);
            this.numOfRowCol = Math.floorDiv(mazeWH, tileWH);
        } else {
            this.numOfRowCol = Math.floorDiv(mazeWH, tileWH);
        }
        
        renderer.generateMaze(mazeWH, tileWH, tileBorder);
        player = new Player(tileWH, tileWH, tileWH/2);
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
    
    public void runStart() {
        setUpFrame();
        JButton play = ui.getPlayButton();
        JButton quit = ui.getQuitButton();
        
        pane.add(play);
        pane.add(quit);
        pane.setLayout(null);

        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MazeGame newGame =  new MazeGame(windowWidth,
                windowHeight, ui);
                
                Thread newGameThread = new Thread(newGame);
                newGameThread.start();
            }
        });
        
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    public void setNESWKeys(JComponent comp) {
        addKeyBinding(comp, KeyEvent.VK_UP, "Move North", false, (evt) -> {player.setMoveN(true);});
        addKeyBinding(comp, KeyEvent.VK_RIGHT, "Move East", false, (evt) -> {player.setMoveE(true);});
        addKeyBinding(comp, KeyEvent.VK_DOWN, "Move South", false, (evt) -> {player.setMoveS(true);});
        addKeyBinding(comp, KeyEvent.VK_LEFT, "Move West", false, (evt) -> {player.setMoveW(true);});
        
        addKeyBinding(comp, KeyEvent.VK_UP, "Stop North", true, (evt) -> {player.setMoveN(false);});
        addKeyBinding(comp, KeyEvent.VK_RIGHT, "Stop East", true, (evt) -> {player.setMoveE(false);});
        addKeyBinding(comp, KeyEvent.VK_DOWN, "Stop South", true, (evt) -> {player.setMoveS(false);});
        addKeyBinding(comp, KeyEvent.VK_LEFT, "Stop West", true, (evt) -> {player.setMoveW(false);});
    }
    
    public void addKeyBinding(JComponent comp, int keyCode, String id, Boolean onRelease, ActionListener al) {
        InputMap inMap = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actMap = comp.getActionMap();
        inMap.put(KeyStroke.getKeyStroke(keyCode, 0, onRelease), id);

        actMap.put(id, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                al.actionPerformed(e);
            }
        });  
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(windowWidth, windowHeight);
    }
}
