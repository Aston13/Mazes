package mazegame;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class AssetManager {

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
    
    public BufferedImage getPreloadedImage(String key) {
        try {
            return preloadedImages.get(key);
        } catch (NullPointerException e) {
            return null;
        }
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

        keyImage = ImageIO.read(getClass().getResourceAsStream("Assets\\KeyOnly.png")); 
        lockedExitImage = ImageIO.read(getClass().getResourceAsStream("Assets\\ExitLocked.png"));
        unlockedExitImage = ImageIO.read(getClass().getResourceAsStream("Assets\\ExitUnlocked.png"));
        
        preloadedImages.put("Key", keyImage);
        preloadedImages.put("Locked Exit", lockedExitImage);
        preloadedImages.put("Open Exit", unlockedExitImage);
        
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
