package TestJavaFX2;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;

import com.jhlabs.image.ContrastFilter;
import com.jhlabs.image.DissolveFilter;
import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.GammaFilter;
import com.jhlabs.image.HSBAdjustFilter;
import com.jhlabs.image.InvertFilter;
import com.jhlabs.image.LevelsFilter;
import com.jhlabs.image.PointillizeFilter;
import com.jhlabs.image.StampFilter;
import com.jhlabs.image.ThresholdFilter;

import net.sourceforge.javaocr.ocrPlugins.mseOCR.CharacterRange;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImageLoader;
import net.sourceforge.javaocr.scanner.PixelImage;
//import net.sourceforge.javaocr.scanner.*;

@SuppressWarnings("deprecation")
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
        URL uri = this.getClass().getResource("");
        String trainingImageDir1 = uri.getFile();
        String trainingImageDir = trainingImageDir1.substring(1);
        
        try
        {
            scanner.clearTrainingImages();
            TrainingImageLoader loader = new TrainingImageLoader();
            HashMap<Character, ArrayList<TrainingImage>> trainingImageMap = new HashMap<Character, ArrayList<TrainingImage>>();
/*            loader.load(
                    trainingImageDir + "ascii.png",
                    new CharacterRange('!', '~'),
                    trainingImageMap);*/
            loader.load(
                    trainingImageDir + "digitsbold.jpg",
                    new CharacterRange('0', '9'),
                    trainingImageMap);
/*            loader.load(
                    trainingImageDir + "cam2.jpg",
                    new CharacterRange('0', '9'),
                    trainingImageMap);*/
            
            
            scanner.addTrainingImages(trainingImageMap);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(2);
        }
    }

    @SuppressWarnings("deprecation")
	public String process(BufferedImage image)
    {

        PixelImage pixelImage = new PixelImage(image);
        pixelImage.toGrayScale(true);
        //pixelImage.filter();
        
        ContrastFilter contrast = new ContrastFilter();
        BufferedImage dest1=contrast.createCompatibleDestImage(image,null);
        contrast.filter(image, dest1);
        
        GammaFilter   gamma = new GammaFilter();
        BufferedImage dest2 = gamma.createCompatibleDestImage(dest1,null);
        gamma.setGamma(1.5f);
        gamma.filter(dest1, dest2);
        ThresholdFilter threshold = new ThresholdFilter();
        BufferedImage dest3 = threshold.createCompatibleDestImage(dest2,null);
        threshold.filter(dest2, dest3);
        
        String text = scanner.scan(dest2, 0, 0, 0, 0, null);
        return text;
    }
}