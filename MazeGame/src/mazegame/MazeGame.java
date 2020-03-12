package mazegame;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class MazeGame extends JFrame implements Runnable {
    
    private final Canvas gameView = new Canvas();
    private final int windowWidth;
    private final int windowHeight;
    private boolean gameInProgress = false;
    private Player player;
    private final UI ui;
    private final int tileWH = 100;
    private final int tileBorder = 0;
    private Renderer renderer;
    private final JPanel pane = new JPanel(new GridLayout());
    private int levelCount = 1;
    private Thread thread;
    private int rowColAmount;
    private int movementSpeed = 5;
    private int fps = 30;
    
    public MazeGame (int windowHeight, int windowWidth, UI ui, int rowColAmount) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.ui = ui;
        if (rowColAmount % 2 == 0) {rowColAmount+=1;}
        this.rowColAmount = rowColAmount;
    }
    
    public void setUpFrame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setContentPane(pane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void setGameState(boolean inProgress) {
        gameInProgress = inProgress;
    }
    
    public void update() {
        int halfP = player.getSize()/2;
        int fullP = player.getSize();
        BufferStrategy buffStrat = gameView.getBufferStrategy();
        Graphics g = buffStrat.getDrawGraphics();
        
        if (player.getMoveN()) {
            int nextTile[] = renderer.getTile(player.getX(), player.getY()-(halfP+1), player.getSize(), tileWH, tileBorder);
            if(renderer.checkCollision(nextTile, this)) { 
                renderer.moveMazeY(g, rowColAmount, movementSpeed);
                player.setY(player.getY()-movementSpeed);  
            }
        }
        if (player.getMoveE()) { 
            int nextTile[] = renderer.getTile(player.getX()+(halfP+1), player.getY(), player.getSize(), tileWH, tileBorder);
            if(renderer.checkCollision(nextTile, this)) { 
                renderer.moveMazeX(g, rowColAmount, -movementSpeed);
                player.setX(player.getX()+movementSpeed); 
            }
        }
        if (player.getMoveS()) {
            int nextTile[] = renderer.getTile(player.getX(), player.getY()+(halfP+1), player.getSize(), tileWH, tileBorder);
            if(renderer.checkCollision(nextTile, this)) {
                renderer.moveMazeY(g, rowColAmount, -movementSpeed);
                player.setY(player.getY()+movementSpeed);
            } 
        }
        if (player.getMoveW()) { 
            int nextTile[] = renderer.getTile(player.getX()-(halfP+1), player.getY(), player.getSize(), tileWH, tileBorder);
            if(renderer.checkCollision(nextTile, this)) {
                renderer.moveMazeX(g, rowColAmount, movementSpeed);
                player.setX(player.getX()-movementSpeed); 
            }
        }
        g.dispose();
    }
    
    public void render() {
        BufferStrategy buffStrat = gameView.getBufferStrategy();
        Graphics g  = buffStrat.getDrawGraphics();
        super.paint(g); // Override

        renderer.renderBackground(g); // Renders background
        renderer.renderMaze(g, tileWH);
        renderer.renderPlayer(g, player);
        renderer.renderHUD(g, player, levelCount);

        g.dispose(); // clears graphics memory
        buffStrat.show(); // Buffer has been written to and is ready to be put on screen
    }
    
    @Override
    public void run() {
        Long lastTime = System.nanoTime();
        double nanoSecondConversion = 100000000.0 / fps; // Updated <fps> times per second
        double changeInSeconds = 0;
        renderer = new Renderer(windowWidth, windowHeight, rowColAmount, tileWH);
      
        setNESWKeys(pane);

        setUpFrame();
        pane.add(gameView);
        gameView.createBufferStrategy(3);

        renderer.generateMaze(tileWH, tileBorder);
        renderer.centerMaze();
        player = new Player(renderer.getStartingX(), renderer.getStartingY(), tileWH);
        
        
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

        runTransitionScreen();
    }
    
    public void runTransitionScreen() {
        JPanel panel = new JPanel(new GridLayout());
        thread = new Thread(this);
        
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        JLabel complete = ui.getLogo("Completed Level " + levelCount);
        JButton next = ui.getTopButton("Next Level [Space]");
        JButton quit = ui.getMidButton("Quit [Esc]");
        panel.add(complete);
        panel.add(next);
        panel.add(quit);
        panel.setLayout(null);
        panel.setBackground(Color.BLACK);
        panel.setVisible(true);
        
        addKeyBinding(panel, KeyEvent.VK_SPACE, "Next Level", false, (evt) -> {
            increaseLevel();
            thread.start();
        });
        
        next.addActionListener((ActionEvent e) -> {
            increaseLevel();
            thread.start();
        });
        
        addKeyBinding(panel, KeyEvent.VK_ESCAPE, "Exit", false, (evt) -> {dispose();});
        quit.addActionListener((e) -> {dispose();});
    }
    
    public void increaseLevel() {
        levelCount += 1;
        rowColAmount += 2;
        setGameState(true);
    }
    
    public void runMenu() {
        setUpFrame();
        JButton play = ui.getTopButton("Play [Space]");
        JButton quit = ui.getMidButton("Quit [Esc]");
        JLabel logo = ui.getLogo("Maze");
        
        pane.add(logo);
        pane.add(play);
        pane.add(quit);
        pane.setLayout(null);
        pane.setBackground(Color.BLACK);

        addKeyBinding(pane, KeyEvent.VK_SPACE, "Next Level", false, (evt) -> {
            dispose();
            MazeGame newGame =  new MazeGame(windowWidth, windowHeight, ui, rowColAmount);
            Thread newGameThread = new Thread(newGame);
            newGame.setGameState(true);
            newGameThread.start();
        });
        
        addKeyBinding(pane, KeyEvent.VK_ESCAPE, "Exit", false, (evt) -> {
            dispose();
        });
        
        play.addActionListener((e) -> {
            dispose();
            MazeGame newGame =  new MazeGame(windowWidth, windowHeight, ui, rowColAmount);
            Thread newGameThread = new Thread(newGame);
            newGame.setGameState(true);
            newGameThread.start();
        });
        
        quit.addActionListener((e) -> {dispose();});
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
        
        addKeyBinding(comp, KeyEvent.VK_ESCAPE, "Exit", false, (evt) -> {System.exit(0);});
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
