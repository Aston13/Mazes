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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final int tileWH = 150;
    private final int tileBorder = 0;
    private Renderer renderer;
    private  JPanel pane = new JPanel(new GridLayout());
    private int levelCount = 1;
    private Thread thread;
    private int rowColAmount;
    private int movementSpeed = 5;
    private int fps = 30;
    private AssetManager am;
    private String stateChange;
    private String[] levelData;
    
    
    public MazeGame (int windowHeight, int windowWidth, UI ui, int rowColAmount) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.ui = ui;
        if (rowColAmount % 2 == 0) {rowColAmount+=1;}
        this.rowColAmount = rowColAmount;
        am = new AssetManager();
        
        try {
            levelData = am.loadLevelData(false);
            System.out.println("Game Loaded.");
        } catch (IOException ex) {
            Logger.getLogger(MazeGame.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void save() {
        try {
            am.saveLevelData(levelData);
            System.out.println("Game Saved.");
        } catch (IOException ex) {
            System.out.println("File not found.");
        }
    }
    
    public void setUpFrame() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setContentPane(pane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public void setGameState(boolean inProgress, String reason) {
        gameInProgress = inProgress;
        stateChange = reason;
    }
    
    public boolean getGameState() {
        return gameInProgress;
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
        renderer.renderPlayer(g, player, tileWH);
        renderer.renderHUD(g, player, levelCount);

        g.dispose(); // clears graphics memory
        buffStrat.show(); // Buffer has been written to and is ready to be put on screen
    }
    
    public void renderBackground(){
        BufferStrategy buffStrat = gameView.getBufferStrategy();
        Graphics g  = buffStrat.getDrawGraphics();
        super.paint(g); // Override
        renderer.renderBackground(g); // Renders background
    }
    
    public void updatePlayer() {
        renderer.updateFrames();
    }
    
    
    @Override
    public void run() {
        Long lastTime = System.nanoTime();
        double nanoSecondConversion = 100000000.0 / fps; // Updated <fps> times per second
        double changeInSeconds = 0;
        double changeInSeconds2 = 0;
        renderer = new Renderer(windowWidth, windowHeight, rowColAmount, tileWH, am, this);
      
        setNESWKeys(pane);

        setUpFrame();
        pane.add(gameView);
        gameView.createBufferStrategy(2);

        renderer.generateMaze(tileWH, tileBorder);
        renderer.centerMaze();
        player = new Player(renderer.getStartingX(), renderer.getStartingY(), tileWH);
        renderer.beginTimer();
        
        render();

        while(getGameState()) {
            Long now = System.nanoTime();
            changeInSeconds += (now - lastTime) / nanoSecondConversion;
            changeInSeconds2 += (now - lastTime) / nanoSecondConversion; 
            
            while(changeInSeconds >= 1) {
                update();
                
                changeInSeconds = 0;
            }
            
            while(changeInSeconds2 >= 10) {
                updatePlayer();
                changeInSeconds2 = 0;
            }
            
            render();
            lastTime = now;
        }
        gameView.getGraphics().finalize();
        
        getGraphics().finalize();
        
        renderBackground();
        renderer.stopTimer();
        
        
        if (stateChange.equalsIgnoreCase("Level Failed")){
            runGameOverScreen();
        } else if (stateChange.equalsIgnoreCase("Next Level")){
            double timeInMs = renderer.getTimeTaken();
            runCompletionScreen(timeInMs);
        }
        
        System.out.println("State change reason not recognised.");
        
    }
    
    public void runGameOverScreen() {
        JPanel panel = new JPanel(new GridLayout());
        thread = new Thread(this);
        
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setContentPane(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        JLabel gameOver = ui.getLogo("You failed level " + levelCount + "!");
        JButton retry = ui.getTopButton("Retry Level? [Space]");
        JButton menu = ui.getMidButton("Main Menu [Esc]");
        panel.add(gameOver);
        panel.add(retry);
        panel.add(menu);
        panel.setLayout(null);
        panel.setBackground(Color.BLACK);
        panel.setVisible(true);
        
        addKeyBinding(panel, KeyEvent.VK_SPACE, "Retry Level", false, (evt) -> {
            setGameState(true, "");
            thread.start();
        });
        
        retry.addActionListener((ActionEvent e) -> {
            setGameState(true, "");
            thread.start();
        });
        
        addKeyBinding(panel, KeyEvent.VK_ESCAPE, "Menu", false, (evt) -> {runMenu();});
        menu.addActionListener((e) -> {
//            dispose();
//            MazeGame newGame =  new MazeGame(windowWidth, windowHeight, ui, rowColAmount);
//            Thread newGameThread = new Thread(newGame);
//            newGame.setGameState(false,"");
//            newGame.runMenu();
//            newGameThread.start();
            //setGameState(false,"");
            
            //thread.start();
            
            runMenu();
            
        });
    }
    
    public void runCompletionScreen(double timeTaken) {
        
        String []lineWords = levelData[levelCount].split(",");
        double bestTime = Double.valueOf(lineWords[2]);
        System.out.println("best time: " + bestTime);
        System.out.println("Time taken: " + timeTaken);
        
        if ((timeTaken < bestTime) || (bestTime == -1)) {
            
            // New best time
            bestTime = timeTaken;
        }
        
        String completedString = String.valueOf(levelCount) + 
                ",completed," + String.valueOf(bestTime);
        
        levelData[levelCount] = completedString;
        save();
        
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
        JButton menu = ui.getMidButton("Main Menu [Esc]");
        panel.add(complete);
        panel.add(next);
        panel.add(menu);
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
        
        addKeyBinding(panel, KeyEvent.VK_ESCAPE, "Menu", false, (evt) -> {runMenu();});
        menu.addActionListener((e) -> {runMenu();});
    }
    
    public void runLevelSelection() {
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
        JButton menu = ui.getMidButton("Main Menu [Esc]");
        panel.add(complete);
        panel.add(next);
        panel.add(menu);
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
        
        addKeyBinding(panel, KeyEvent.VK_ESCAPE, "Menu", false, (evt) -> {runMenu();});
        menu.addActionListener((e) -> {runMenu();});
    }
    
    public void increaseLevel() {
        
        levelCount += 1;
        rowColAmount += 2;
        setGameState(true, "");
    }
    
    public void runMenu() {
        try {
            super.remove(gameView);
            pane.removeAll();
            gameView.getBufferStrategy().dispose();
            
        } catch (Exception e) {
            
        }
        pane = new JPanel(new GridLayout());
        setUpFrame();
        
        JButton play = ui.getTopButton("Play [Space]");
        JButton levels = ui.getMidButton("Levels");
        JButton quit = ui.getBottomButton("Quit [Esc]");
        JLabel logo = ui.getLogo("Maze");
         
        pane.add(logo);
        pane.add(play);
        pane.add(levels);
        pane.add(quit);
        pane.setLayout(null);
        pane.setBackground(Color.BLACK);

        addKeyBinding(pane, KeyEvent.VK_SPACE, "Next Level", false, (evt) -> {
            dispose();
            MazeGame newGame =  new MazeGame(windowWidth, windowHeight, ui, rowColAmount);
            Thread newGameThread = new Thread(newGame);
            newGame.setGameState(true, "");
            newGameThread.start();
        });
        
        addKeyBinding(pane, KeyEvent.VK_ESCAPE, "Exit", false, (evt) -> {
            dispose();
        });
        
        levels.addActionListener((e) -> {
            runLevelSelection();
        });
        
        play.addActionListener((e) -> {
            dispose();
            MazeGame newGame =  new MazeGame(windowWidth, windowHeight, ui, rowColAmount);
            Thread newGameThread = new Thread(newGame);
            newGame.setGameState(true, "Next Level");
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
