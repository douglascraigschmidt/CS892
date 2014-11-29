package example.imagetaskgang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @class PlatformStrategyConsole
 *
 * @brief Implements a platform-independent API for ...  It plays the
 *        role of the "Concrete Strategy" in the Strategy pattern.
 */
public class PlatformStrategyConsole extends PlatformStrategy {
    /**
     * Contains information for printing output to the console window.
     */
    private final PrintStream mOutput;

    /** 
     * Constructor initializes the data member.
     */
    public PlatformStrategyConsole(Object output) {
        mOutput = (PrintStream) output;
    }
	
    /**
     * Return the path for the directory where images are stored.
     */
    public String getDirectoryPath() {
        return new File("DownloadImages").getAbsolutePath();
    }

    /**
     * Create an Image.
     */
    public Image makeImage(byte[] imageData){
        return new BufferedImage(imageData);
    }
     
    public InputEntity applyGrayscaleFilter(InputEntity inputEntity) {
//    	Image imageAdapter = ((ImageEntity) inputEntity).getImage();
//    	java.awt.image.BufferedImage originalImage = ((BufferedImage) imageAdapter).mBufferedImage;
//        java.awt.image.BufferedImage grayScaleImg =
//        		new java.awt.image.BufferedImage(originalImage.getColorModel(),
//                                  originalImage.copyData(null),
//                                  originalImage.getColorModel().isAlphaPremultiplied(),
//                                  null);
//
//        boolean hasTransparent = grayScaleImg.getColorModel().hasAlpha();
//        int width = grayScaleImg.getWidth();
//        int height = grayScaleImg.getHeight();
//
//        // A common pixel-by-pixel grayscale conversion algorithm 
//        // using values obtained from http://en.wikipedia.org/wiki/Grayscale
//        for (int i = 0; i < height; ++i) {
//            for (int j = 0; j < width; ++j) {
//            	
//            	// Check if the pixel is transparent in the original
//                if (hasTransparent 
//                    && (grayScaleImg.getRGB(j, i) >> 24) == 0x00) {
//                    continue;
//                }
//                
//                // Convert the pixel to grayscale
//                Color c = new Color(grayScaleImg.getRGB(j, i));
//                int grayConversion = (int) (c.getRed() * 0.299)
//                    + (int) (c.getGreen() * 0.587)
//                    + (int) (c.getBlue() * 0.114);
//                Color grayScale = new Color(grayConversion, grayConversion,
//                                            grayConversion);
//                grayScaleImg.setRGB(j, i, grayScale.getRGB());
//            }
//        }
//   	
//    	  BufferedImage grayScaleImage = new BufferedImage(grayScaleImg);
//
//        return new ImageEntity(processResult.getSourceURL(),
//                               grayScaleImage);
    	return inputEntity;
    }
    
    public void storeImage(Image imageAdapter,
                           FileOutputStream outputFile) {
    	// Write the image to the appropriate directory
//    	ImageIO.write(((BufferedImage) imageAdapter).mBufferedImage,
//                	  "png",
//                	  outputFile);
    }

    /**
     * Error log formats the message and displays it for the debugging
     * purposes.
     */
    public void errorLog(String javaFile, String errorMessage) {
        mOutput.println(javaFile 
                        + " " 
                        + errorMessage);
    }

    /**
     * Overrides the getURLIterator method to return the
     * Console-specific input sources.
     */
	public Iterator<List<URL>> getUrlIterator(InputSource source) {
		List<List<URL>> variableNumberOfInputURLs = 
                new ArrayList<List<URL>>();
    	
    	try {
    		switch (source) {
    		case DEFAULT:
	            variableNumberOfInputURLs = super.getDefaultList();
	            break;
	           
    		case FILE:
    			// @@ Nolan change to try with resources after upgrade to 1.7
    			BufferedReader urlReader = null;
    			try {
    				urlReader = new BufferedReader(new FileReader(
    						Options.instance().getURLFilePathname()));
			    	List<URL> currentUrls = new ArrayList<URL>();
			    	String url;
			    	while ( (url = urlReader.readLine()) != null) {
			    		if (url.equalsIgnoreCase(Options.instance().getSeparator())) {
			    			variableNumberOfInputURLs.add(currentUrls);
			    			currentUrls = new ArrayList<URL>();
			    		}
			    		else {
			    			currentUrls.add(new URL(url));
			    		}
			    	}
			    	variableNumberOfInputURLs.add(currentUrls);
			    	
		    	} catch (FileNotFoundException e) {
		    		mOutput.println("URL file not found");
				} catch (IOException e) {
					mOutput.println("Error reading file");
				} finally {
		    		if (urlReader != null) {
						try {
							urlReader.close();
						} catch (IOException e) {
							mOutput.println("Error closing reader");
						}
		    		}
		    	}
    			break;
    			
		    default:
		    	mOutput.println("Invalid Source");
		    	return null;
	    	}
    	} catch (MalformedURLException e) {
    		mOutput.println("Invalid URL");
    		return null;
    	}
    	
		return variableNumberOfInputURLs.iterator();
	}
	
}

