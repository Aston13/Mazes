//package mazegame;
//
//import javax.swing.JFrame; // Import JFrame class from graphics library
//import java.awt.Graphics;
//import java.awt.Canvas;
//import java.awt.Dimension;
//import java.awt.GridLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyEvent;
//import java.awt.image.BufferStrategy;
//import javax.swing.AbstractAction;
//import javax.swing.ActionMap;
//import javax.swing.InputMap;
//import javax.swing.JComponent;
//import javax.swing.JPanel;
//import javax.swing.KeyStroke;
//
//public class KeyBinds extends JFrame {
//    
//    private final Canvas view = new Canvas();
//    private final int windowWidth;
//    private final int windowHeight;
//    private boolean gameInProgress = true;
//    private Player p1;
//    private final UI ui;
//    private int mazeWH = 700;
//    private final int tileWH = 20;
//    private final int tileBorder = 1;
//    private int numOfRowCol;
//    private final Renderer renderer;
//    private JPanel pane = new JPanel(new GridLayout());
//    
//    public KeyBinds(int windowHeight, int windowWidth, UI ui) {
//        this.windowWidth = windowWidth;
//        this.windowHeight = windowHeight;
//        this.ui = ui;
//        renderer = new Renderer(windowWidth, windowHeight);
//    }
//    
//    public void update() {
//        int halfP = p1.getSize()/2;
//        BufferStrategy buffStrat = view.getBufferStrategy();
//        Graphics g  = buffStrat.getDrawGraphics();
//        
//        if (p1.getMoveN()) {
//            int nextTile[] = renderer.getTile(p1.getX(),p1.getY()-(halfP-1), p1.getSize(), mazeWH, tileWH, tileBorder);
//            if(renderer.checkCollision(nextTile)) { 
//                renderer.moveMazeY(g, numOfRowCol, 1);
//                p1.setY(p1.getY()-1);  
//            }
//        }
//        if (p1.getMoveE()) { 
//            int nextTile[] = renderer.getTile(p1.getX()+(halfP+1),p1.getY(), p1.getSize(), mazeWH, tileWH, tileBorder);
//            if(renderer.checkCollision(nextTile)) { 
//                renderer.moveMazeX(g, numOfRowCol, -1);
//                p1.setX(p1.getX()+1); 
//            }
//        }
//        if (p1.getMoveS()) {
//            int nextTile[] = renderer.getTile(p1.getX(),p1.getY()+(halfP+1), p1.getSize(), mazeWH, tileWH, tileBorder);
//            if(renderer.checkCollision(nextTile)) {
//                renderer.moveMazeY(g, numOfRowCol, -1);
//                p1.setY(p1.getY()+1);
//            } 
//        }
//        if (p1.getMoveW()) { 
//            int nextTile[] = renderer.getTile(p1.getX()-(halfP-1),p1.getY(), p1.getSize(), mazeWH, tileWH, tileBorder);
//            if(renderer.checkCollision(nextTile)) {
//                renderer.moveMazeX(g, numOfRowCol, 1);
//                p1.setX(p1.getX()-1); 
//            }
//        }
//    }
//    
//    public void render() {
//        BufferStrategy buffStrat = view.getBufferStrategy();
//        Graphics g  = buffStrat.getDrawGraphics();
//        super.paint(g); // Override
//
//        renderer.renderBackground(g); // Renders background
//        //renderer.renderMaze(g, numOfRowCol);
//        renderer.renderPlayer(g, p1);
//
//        g.dispose(); // clears graphics memory
//        buffStrat.show(); // Buffer has been written to and is ready to be put on screen
//    }
//    
//    public void run() {
//        Long lastTime = System.nanoTime();
//        double nanoSecondConversion = 100000000.0 / 60; // Updated 60 times per second
//        double changeInSeconds = 0;
//        
//        setResizable(true);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ends app build on close
//        setContentPane(pane);
//        pack();
//        setLocationRelativeTo(null);
//        setVisible(true);
//
//        pane.add(view);
//        view.createBufferStrategy(2);
//        setNESWKeys(pane); 
//
//        
//        if ((mazeWH/tileWH) % 2 == 0) {
//            this.mazeWH = mazeWH - (tileWH+1);
//            this.numOfRowCol = Math.floorDiv(mazeWH, tileWH);
//        } else {
//            this.numOfRowCol = Math.floorDiv(mazeWH, tileWH);
//        }
//        
//        renderer.generateMaze(mazeWH, tileWH, tileBorder);
//        p1 = new Player(tileWH, tileWH, tileWH/2);
//        render();
//
//        while(gameInProgress) {
//            Long now = System.nanoTime();
//            changeInSeconds += (now - lastTime) / nanoSecondConversion;            
//            
//            while(changeInSeconds >= 1) {
//                update();
//                changeInSeconds = 0;
//            }
//            render();
//            lastTime = now;
//        }
//    }
//    
//    public void setNESWKeys(JComponent comp) {
//        addKeyBinding(comp, KeyEvent.VK_UP, "Move North", false, (evt) -> {p1.setMoveN(true);});
//        addKeyBinding(comp, KeyEvent.VK_RIGHT, "Move East", false, (evt) -> {p1.setMoveE(true);});
//        addKeyBinding(comp, KeyEvent.VK_DOWN, "Move South", false, (evt) -> {p1.setMoveS(true);});
//        addKeyBinding(comp, KeyEvent.VK_LEFT, "Move West", false, (evt) -> {p1.setMoveW(true);});
//        
//        addKeyBinding(comp, KeyEvent.VK_UP, "Stop North", true, (evt) -> {p1.setMoveN(false);});
//        addKeyBinding(comp, KeyEvent.VK_RIGHT, "Stop East", true, (evt) -> {p1.setMoveE(false);});
//        addKeyBinding(comp, KeyEvent.VK_DOWN, "Stop South", true, (evt) -> {p1.setMoveS(false);});
//        addKeyBinding(comp, KeyEvent.VK_LEFT, "Stop West", true, (evt) -> {p1.setMoveW(false);});
//    }
//    
//    public void addKeyBinding(JComponent comp, int keyCode, String id, Boolean onRelease, ActionListener al) {
//        InputMap inMap = comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
//        ActionMap actMap = comp.getActionMap();
//        inMap.put(KeyStroke.getKeyStroke(keyCode, 0, onRelease), id);
//
//        actMap.put(id, new AbstractAction() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                al.actionPerformed(e);
//            }
//        });  
//    }
//    
//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(windowWidth, windowHeight);
//    }
//}
