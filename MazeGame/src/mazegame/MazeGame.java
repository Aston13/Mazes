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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class MazeGame extends JFrame implements Runnable {
    
    private final Canvas gameView = new Canvas();
    private final int windowWidth;
    private final int windowHeight;
    private boolean gameInProgress = false;
    private volatile boolean paused = false;
    private JPanel pauseOverlay = null;
    private Player player;
    private final UI ui;
    private final int tileWH = 100;
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
        load(false);
        setCurrentLevel(-1);
        
    }
    
    public void setCurrentLevel(int level) {
        if (level == -1){
            for (int i = 1; i < levelData.length; i++) {
                String []lineWords = levelData[i].split(",");
                if (lineWords[1].equalsIgnoreCase("incomplete")){
                    levelCount = i;
                    //System.out.println("play rowcol" + rowColAmount);
                    rowColAmount += ((i-1)*2);
                    break;
                }
            }
        } else {
            levelCount = level;
            //System.out.println("select rowcol" + rowColAmount);
            int rc = 10 + ((level-1)*2);
            if (rc % 2 == 0) {rc+=1;}
            rowColAmount = rc;
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
    
    public void load(boolean reset) {
        try {
            levelData = am.loadLevelData(reset);
            System.out.println("Game Loaded.");
        } catch (IOException ex) {
            Logger.getLogger(MazeGame.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private boolean useDirectRendering = false;
    private BufferedImage offscreenBuffer = null;

    private Graphics getGameGraphics() {
        if (!useDirectRendering) {
            try {
                BufferStrategy bs = gameView.getBufferStrategy();
                if (bs != null) {
                    Graphics g = bs.getDrawGraphics();
                    if (g != null) return g;
                }
            } catch (Exception e) {
                // BufferStrategy is broken (e.g. CheerpJ) — fall back permanently
                useDirectRendering = true;
                System.out.println("BufferStrategy failed, switching to offscreen buffer.");
            }
        }
        // Manual double-buffer: draw to offscreen image, blit in showBuffer()
        if (offscreenBuffer == null
                || offscreenBuffer.getWidth() != windowWidth
                || offscreenBuffer.getHeight() != windowHeight) {
            offscreenBuffer = new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_ARGB);
        }
        return offscreenBuffer.getGraphics();
    }

    private void showBuffer() {
        if (!useDirectRendering) {
            try {
                BufferStrategy bs = gameView.getBufferStrategy();
                if (bs != null) { bs.show(); return; }
            } catch (Exception e) {
                useDirectRendering = true;
            }
        }
        // Blit offscreen buffer to canvas in one operation (no flicker)
        if (offscreenBuffer != null) {
            Graphics g = gameView.getGraphics();
            if (g != null) {
                g.drawImage(offscreenBuffer, 0, 0, null);
                g.dispose();
            }
        }
    }

    public void update() {
        int halfP = player.getSize()/2;
        int fullP = player.getSize();
        Graphics g = getGameGraphics();
        if (g == null) return;
        
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
        Graphics g = getGameGraphics();
        if (g == null) return;
        super.paint(g); // Override

        renderer.renderBackground(g); // Renders background
        renderer.renderMaze(g, tileWH);
        renderer.renderPlayer(g, player, tileWH);
        renderer.renderHUD(g, player, levelCount);

        g.dispose(); // clears graphics memory
        showBuffer(); // Buffer has been written to and is ready to be put on screen
    }
    
    public void renderBackground(){
        Graphics g = getGameGraphics();
        if (g == null) return;
        super.paint(g); // Override
        renderer.renderBackground(g); // Renders background
        g.dispose();
        showBuffer();
    }
    
    public void updatePlayer() {
        renderer.updateFrames();
    }

    private void showPauseMenu() {
        paused = true;
        renderer.stopTimer();
        player.setMoveN(false);
        player.setMoveE(false);
        player.setMoveS(false);
        player.setMoveW(false);

        pauseOverlay = new JPanel(null);
        pauseOverlay.setBackground(new Color(0, 0, 0, 180));
        pauseOverlay.setBounds(0, 0, windowWidth, windowHeight);

        JButton resume = ui.getTopButton("Resume [Space]");
        JButton menu = ui.getMidButton("Main Menu");
        JLabel title = ui.getLogo("Paused");
        pauseOverlay.add(title);
        pauseOverlay.add(resume);
        pauseOverlay.add(menu);

        addKeyBinding(pauseOverlay, KeyEvent.VK_SPACE, "Resume", false, (evt) -> {
            resumeGame();
        });
        addKeyBinding(pauseOverlay, KeyEvent.VK_ESCAPE, "ResumeEsc", false, (evt) -> {
            resumeGame();
        });

        resume.addActionListener((e) -> resumeGame());
        menu.addActionListener((e) -> {
            removePauseOverlay();
            setGameState(false, "Menu");
        });

        getLayeredPane().add(pauseOverlay, Integer.valueOf(100));
        pauseOverlay.setVisible(true);
        pauseOverlay.requestFocusInWindow();
    }

    private void resumeGame() {
        paused = false;
        removePauseOverlay();
        renderer.beginTimer();
        pane.requestFocusInWindow();
    }

    private void removePauseOverlay() {
        if (pauseOverlay != null) {
            getLayeredPane().remove(pauseOverlay);
            getLayeredPane().repaint();
            pauseOverlay = null;
        }
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
        try {
            gameView.createBufferStrategy(2);
        } catch (Exception e) {
            System.out.println("BufferStrategy not supported, using direct rendering.");
        }

        renderer.generateMaze(tileWH, tileBorder);
        renderer.centerMaze();
        player = new Player(renderer.getStartingX(), renderer.getStartingY(), tileWH);
        renderer.beginTimer();
        
        render();

        while(getGameState()) {
            if (paused) {
                try { Thread.sleep(50); } catch (InterruptedException ie) { }
                lastTime = System.nanoTime();
                continue;
            }
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
        Graphics gv = gameView.getGraphics();
        if (gv != null) gv.dispose();
        
        Graphics gf = getGraphics();
        if (gf != null) gf.dispose();
        
        renderBackground();
        renderer.stopTimer();
        
        
        if (stateChange.equalsIgnoreCase("Level Failed")){
            runGameOverScreen();
        } else if (stateChange.equalsIgnoreCase("Next Level")){
            double timeInMs = renderer.getTimeTaken();
            runCompletionScreen(timeInMs);
        } else if (stateChange.equalsIgnoreCase("Menu")){
            removePauseOverlay();
            runMenu();
        } 
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
            
            runMenu();
            
        });
    }
    
    public void runCompletionScreen(double timeTaken) {
        
        String []lineWords = levelData[levelCount].split(",");
        double bestTime = Double.parseDouble(lineWords[2]);
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
        JPanel panelWrapper = new JPanel(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0,1));
        ArrayList<JPanel> levelPanels = ui.getLevelPanels(levelData, this);

        panel.setBackground(Color.red);
        for (int i = 1; i < levelData.length; i++) {
            JPanel p = levelPanels.get(i-1);
            panel.add(p);
        }

        panelWrapper.add(ui.getLevelHeader(this), BorderLayout.NORTH);
        panelWrapper.add(panel, BorderLayout.SOUTH);
        
        
        JScrollPane scrollPane = new JScrollPane(panelWrapper, VERTICAL_SCROLLBAR_ALWAYS, HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(this.getPreferredSize());
        scrollPane.setViewportView(panelWrapper);
        
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
        setContentPane(scrollPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        scrollPane.setVisible(true);
        
        addKeyBinding(panel, KeyEvent.VK_ESCAPE, "Menu", false, (evt) -> {runMenu();});
        
    }
    
    public void setLevel(int level) {
        levelCount += level-1;
        rowColAmount += (level-1*2);
    }
    
    public void increaseLevel() {
        if (levelCount < 30) {
            levelCount += 1;
            rowColAmount += 2;
            setGameState(true, "Increase Level");   
        }
    }
    
    public void playSelectedLevel() {
            dispose();
            MazeGame newGame =  new MazeGame(windowWidth, windowHeight, ui, rowColAmount);
            Thread newGameThread = new Thread(newGame);
            newGame.levelCount = levelCount;
            newGame.setGameState(true, "Level Select");
            newGameThread.start();
    }
    
    public void runMenu() {
        try {
            super.remove(gameView);
            pane.removeAll();
            BufferStrategy bs = gameView.getBufferStrategy();
            if (bs != null) bs.dispose();
            
        } catch (Exception e) {
            
        }
        pane = new JPanel(new GridLayout());
        setUpFrame();
        
        JButton play = ui.getTopButton("Continue [Space]");
        JButton levels = ui.getMidButton("Level Selection");
        JButton quit = ui.getBottomButton("Quit [Esc]");
        JLabel logo = ui.getLogo("Wesley's Way Out");
         
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
        
        addKeyBinding(comp, KeyEvent.VK_ESCAPE, "Exit", false, (evt) -> {
            if (!paused) {
                showPauseMenu();
            }
        });
    }
    
    public void addKeyBinding(JComponent comp, int keyCode, String id, boolean onRelease, ActionListener al) {
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
