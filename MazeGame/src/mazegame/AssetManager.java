package mazegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class AssetManager {

    private String levelDataFile = "./src/mazegame/assets/LevelData.txt";
    
    private BufferedImage grassPassage0  = null;
    private BufferedImage grassPassage1  = null;
    private BufferedImage grassPassage2  = null;
    private BufferedImage grassPassage3  = null;
    
    private BufferedImage keyImage = null;
    private BufferedImage lockedExitImage = null;
    private BufferedImage unlockedExitImage = null;
    private HashMap<String, BufferedImage> preloadedImages = new HashMap();
    
    private BufferedImage wall_0000  = null;
    private BufferedImage wall_0001  = null;
    private BufferedImage wall_0010  = null;
    private BufferedImage wall_0011 = null;
    private BufferedImage wall_0100 = null;
    private BufferedImage wall_0101 = null;
    private BufferedImage wall_0110 = null;
    private BufferedImage wall_0111 = null;
    private BufferedImage wall_1000 = null;
    private BufferedImage wall_1001 = null;
    private BufferedImage wall_1010 = null;
    private BufferedImage wall_1011 = null;
    private BufferedImage wall_1100 = null;
    private BufferedImage wall_1101 = null;
    private BufferedImage wall_1110 = null;
    private BufferedImage wall_1111 = null;
    
    private BufferedImage dogEast0 = null;
    private BufferedImage dogEast1 = null;
    private BufferedImage dogEast2 = null;
    private BufferedImage dogEast3 = null;
    private BufferedImage dogEast4 = null;
    private BufferedImage dogEast5 = null;
    private BufferedImage dogEast6 = null;
    
    private BufferedImage dogWest0 = null;
    private BufferedImage dogWest1 = null;
    private BufferedImage dogWest2 = null;
    private BufferedImage dogWest3 = null;
    private BufferedImage dogWest4 = null;
    private BufferedImage dogWest5 = null;
    private BufferedImage dogWest6 = null;
    
    private BufferedImage dogNorth0 = null;
    private BufferedImage dogNorth1 = null;
    private BufferedImage dogNorth2 = null;
    private BufferedImage dogNorth3 = null;
    private BufferedImage dogNorth4 = null;
    private BufferedImage dogNorth5 = null;
    
    private BufferedImage dogSouth0 = null;
    private BufferedImage dogSouth1 = null;
    private BufferedImage dogSouth2 = null;
    private BufferedImage dogSouth3 = null;
    private BufferedImage dogSouth4 = null;
    private BufferedImage dogSouth5 = null;
    
    private BufferedImage key0 = null;
    private BufferedImage key1 = null;
    private BufferedImage key2 = null;
    private BufferedImage key3 = null;
    private BufferedImage key4 = null;
    private BufferedImage key5 = null;
    private BufferedImage key6 = null;
    private BufferedImage key7 = null;
    private BufferedImage key8 = null;
    private BufferedImage key9 = null;
    private BufferedImage key10 = null;
    private BufferedImage key11 = null;
    private BufferedImage key12 = null;
    private BufferedImage key13 = null;
    private BufferedImage key14 = null;
    private BufferedImage key15 = null;
    private BufferedImage key16 = null;
    private BufferedImage key17 = null;
    private BufferedImage key18= null;
    private BufferedImage key19 = null;
    
    private Timer t;
    private int keyTimerCount;
    
    public AssetManager() {
        t = new Timer(100, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                keyTimerCount++;
                if (keyTimerCount == 19) {
                    keyTimerCount = 0;
                    t.restart();
                }
            }
            
        });
        t.start();
    }
    
    public void saveLevelData(String []lines) throws IOException {
//        File file = new File("./src/mazegame/assets/");
//        for(String fileNames : file.list()) System.out.println(fileNames);

        BufferedWriter writer = new BufferedWriter(new FileWriter(levelDataFile));
        
        //Level Number | Completed | Best Time
        for(String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
    
    public String[] loadLevelData() throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(levelDataFile));
        int lines = 0;
        
        
        //Level Number | Completed | Best Time
        while(reader.readLine() != null) {lines++;}
        reader.close();
        
        
        String [] loadedData = new String[lines];
        
        BufferedReader reader2 = new BufferedReader(new FileReader(levelDataFile));
        loadedData[0] = reader2.readLine();
        
        for (int i = 1; i < lines; i++) {
            
            loadedData[i] = reader2.readLine();
        }
        reader2.close();
        
        return loadedData;
    }
    
    
    public BufferedImage getPreloadedImage(String key) {
        try {
            return preloadedImages.get(key);
        } catch (Exception e) {
            return null;
        }
    }
    
    public BufferedImage getKeyFrame() {
        if(keyTimerCount == 19) {keyTimerCount = 0;}
        try {
            return preloadedImages.get("Key_" + String.valueOf(keyTimerCount));
        } catch (Exception e) {
            return null;
        }
    }
    
    public BufferedImage getBlinkingKeyFrame() {
        if(keyTimerCount == 19) {keyTimerCount = 0;}
        try {
            if(keyTimerCount%2 == 0){
                
                return preloadedImages.get("Key_" + String.valueOf(keyTimerCount));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
    
    public void preloadImages() throws IOException {
        ImageIO.setUseCache(false);
        
        grassPassage0 = ImageIO.read(getClass().getResourceAsStream("Assets\\GrassPassage_0.png"));
        grassPassage1  = ImageIO.read(getClass().getResourceAsStream("Assets\\GrassPassage_1.png"));
        grassPassage2 = ImageIO.read(getClass().getResourceAsStream("Assets\\GrassPassage_2.png"));
        grassPassage3  = ImageIO.read(getClass().getResourceAsStream("Assets\\GrassPassage_3.png"));
        
        preloadedImages.put("GrassPassage_0", grassPassage0);
        preloadedImages.put("GrassPassage_1", grassPassage1);
        preloadedImages.put("GrassPassage_2", grassPassage2);
        preloadedImages.put("GrassPassage_3", grassPassage3);

        
        lockedExitImage = ImageIO.read(getClass().getResourceAsStream("Assets\\ExitLocked.png"));
        unlockedExitImage = ImageIO.read(getClass().getResourceAsStream("Assets\\ExitUnlocked.png"));
        preloadedImages.put("Locked Exit", lockedExitImage);
        preloadedImages.put("Open Exit", unlockedExitImage);
        
        key0 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_0.png"));
        key1 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_1.png"));
        key2 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_2.png"));
        key3 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_3.png"));
        key4 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_4.png"));
        key5 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_5.png"));
        key6 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_6.png"));
        key7 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_7.png"));
        key8 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_8.png"));
        key9 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_9.png"));
        key10 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_10.png"));
        key11 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_11.png"));
        key12 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_12.png"));
        key13 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_13.png"));
        key14 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_14.png"));
        key15 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_15.png"));
        key16 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_16.png"));
        key17 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_17.png"));
        key18 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_18.png"));
        key19 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Key\\Key_19.png"));
        
        preloadedImages.put("Key_0", key0);
        preloadedImages.put("Key_1", key1);
        preloadedImages.put("Key_2", key2);
        preloadedImages.put("Key_3", key3);
        preloadedImages.put("Key_4", key4);
        preloadedImages.put("Key_5", key5);
        preloadedImages.put("Key_6", key6);
        preloadedImages.put("Key_7", key7);
        preloadedImages.put("Key_8", key8);
        preloadedImages.put("Key_9", key9);
        preloadedImages.put("Key_10", key10);
        preloadedImages.put("Key_11", key11);
        preloadedImages.put("Key_12", key12);
        preloadedImages.put("Key_13", key13);
        preloadedImages.put("Key_14", key14);
        preloadedImages.put("Key_15", key15);
        preloadedImages.put("Key_16", key16);
        preloadedImages.put("Key_17", key17);
        preloadedImages.put("Key_18", key18);
        preloadedImages.put("Key_19", key19);
        
        
        dogNorth0 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\north_0.png"));
        dogNorth1 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\north_1.png"));
        dogNorth2 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\north_2.png"));
        dogNorth3 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\north_3.png"));
        dogNorth4 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\north_4.png"));
        dogNorth5 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\north_5.png"));
        
        preloadedImages.put("dogNorth0", dogNorth0);
        preloadedImages.put("dogNorth1", dogNorth1);
        preloadedImages.put("dogNorth2", dogNorth2);
        preloadedImages.put("dogNorth3", dogNorth3);
        preloadedImages.put("dogNorth4", dogNorth4);
        preloadedImages.put("dogNorth5", dogNorth5);
        
        dogEast0 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\right_0.png"));
        dogEast1 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\right_1.png"));
        dogEast2 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\right_2.png"));
        dogEast3 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\right_3.png"));
        dogEast4 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\right_4.png"));
        dogEast5 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\right_5.png"));
        dogEast6 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\right_6.png"));
        
        preloadedImages.put("dogEast0", dogEast0);
        preloadedImages.put("dogEast1", dogEast1);
        preloadedImages.put("dogEast2", dogEast2);
        preloadedImages.put("dogEast3", dogEast3);
        preloadedImages.put("dogEast4", dogEast4);
        preloadedImages.put("dogEast5", dogEast5);
        preloadedImages.put("dogEast6", dogEast6);

        dogSouth0 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\south_0.png"));
        dogSouth1 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\south_1.png"));
        dogSouth2 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\south_2.png"));
        dogSouth3 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\south_3.png"));
        dogSouth4 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\south_4.png"));
        dogSouth5 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\south_5.png"));
        
        preloadedImages.put("dogSouth0", dogSouth0);
        preloadedImages.put("dogSouth1", dogSouth1);
        preloadedImages.put("dogSouth2", dogSouth2);
        preloadedImages.put("dogSouth3", dogSouth3);
        preloadedImages.put("dogSouth4", dogSouth4);
        preloadedImages.put("dogSouth5", dogSouth5);
        
        dogWest0 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\left_0.png"));
        dogWest1 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\left_1.png"));
        dogWest2 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\left_2.png"));
        dogWest3 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\left_3.png"));
        dogWest4 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\left_4.png"));
        dogWest5 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\left_5.png"));
        dogWest6 = ImageIO.read(getClass().getResourceAsStream("Assets\\AnimationFrames\\Dog\\left_6.png"));
        
        preloadedImages.put("dogWest0", dogWest0);
        preloadedImages.put("dogWest1", dogWest1);
        preloadedImages.put("dogWest2", dogWest2);
        preloadedImages.put("dogWest3", dogWest3);
        preloadedImages.put("dogWest4", dogWest4);
        preloadedImages.put("dogWest5", dogWest5);
        preloadedImages.put("dogWest6", dogWest6);
        
        //Wall with passage in direction NESW | 0000
        wall_0000 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0000.png"));
        wall_0001 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0001.png"));
        wall_0010 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0010.png"));
        wall_0011 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0011.png"));
        wall_0100 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0100.png"));
        wall_0101 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0101.png"));
        wall_0110 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0110.png"));
        wall_0111 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_0111.png"));
        wall_1000 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1000.png"));
        wall_1001 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1001.png"));
        wall_1010 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1010.png"));
        wall_1011 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1011.png"));
        wall_1100 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1100.png"));
        wall_1101 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1101.png"));
        wall_1110 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1110.png"));
        wall_1111 = ImageIO.read(getClass().getResourceAsStream("Assets\\wall_1111.png"));
        
        preloadedImages.put("wall_0000", wall_0000);
        preloadedImages.put("wall_0001", wall_0001);
        preloadedImages.put("wall_0010", wall_0010);
        preloadedImages.put("wall_0011", wall_0011);
        preloadedImages.put("wall_0100", wall_0100);
        preloadedImages.put("wall_0101", wall_0101);
        preloadedImages.put("wall_0110", wall_0110);
        preloadedImages.put("wall_0111", wall_0111);
        preloadedImages.put("wall_1000", wall_1000);
        preloadedImages.put("wall_1001", wall_1001);
        preloadedImages.put("wall_1010", wall_1010);
        preloadedImages.put("wall_1011", wall_1011);
        preloadedImages.put("wall_1100", wall_1100);
        preloadedImages.put("wall_1101", wall_1101);
        preloadedImages.put("wall_1110", wall_1110);
        preloadedImages.put("wall_1111", wall_1111);
                  
    }
}
