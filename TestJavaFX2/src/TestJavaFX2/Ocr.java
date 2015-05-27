package TestJavaFX2;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;

public class Ocr
{
    private OCRScanner scanner;

    public Ocr()
    {
        scanner = new OCRScanner();
    }
    
    public void loadTrainingImages()
    {
/*        if (!trainingImageDir.endsWith(File.separator))
        {
            trainingImageDir += File.separator;
        }*/
    	
        try
        {
            scanner.clearTrainingImages();
            TrainingImageLoader loader = new TrainingImageLoader();
            HashMap<Character, ArrayList<TrainingImage>> trainingImageMap = new HashMap<Character, ArrayList<TrainingImage>>();
            loader.load(
            		this.getClass().getClassLoader().getResource("font2.jpg").getFile(),
            		new CharacterRange('0', '9'),
            		trainingImageMap);
            loader.load(
            		this.getClass().getClassLoader().getResource("font3.jpg").getFile(),
            		new CharacterRange('0', '9'),
            		trainingImageMap);
            loader.load(
            		this.getClass().getClassLoader().getResource("font.jpg").getFile(),
            		new CharacterRange('0', '9'),
            		trainingImageMap);
            loader.load(
            		this.getClass().getClassLoader().getResource("digitsbold.jpg").getFile(),
                    new CharacterRange('0', '9'),
                    trainingImageMap);
            
            
            scanner.addTrainingImages(trainingImageMap);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(2);
        }
    }

 	public String process(BufferedImage image)
    {
        String text = scanner.scan(image, 0, 0, 0, 0, null);
        return text;
    }
}