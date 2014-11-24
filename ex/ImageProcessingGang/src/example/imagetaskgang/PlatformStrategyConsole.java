package example.imagetaskgang;

import java.io.FileOutputStream;
import java.io.PrintStream;

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
        // @@ Nolan, please fill this in.
        return ".";
    }

    /**
     * Create an Image.
     */
    public Image makeImage(byte[] imageData){
        return new BufferedImage(null); // @@ Nolan, need to fix this.
    }
     
    public InputEntity applyGrayscaleFilter(InputEntity inputEntity) {
        // @@ Nolan, need to fix this.
    	return inputEntity;
    }
    
    public void storeImage(Image imageAdapter,
                           FileOutputStream outputFile) {
    	// @@ Nolan, need to implement this.
    	String output = "hello world";
        try {
            // @@ outputFile.write(output.getBytes());
        } catch (Exception e) {
        }
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
}

