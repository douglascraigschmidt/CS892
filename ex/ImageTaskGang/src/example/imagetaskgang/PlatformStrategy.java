package example.imagetaskgang;

import java.io.FileOutputStream;

/** 
 * @class PlatformStrategy
 *
 * @brief Provides methods that define a platform-independent
 *        mechanism for ... This class is a singleton that also plays
 *        the role of the "Strategy" in the Strategy pattern and the
 *        Product in the Factory Method pattern.  Both the
 *        PlatformStrategyConsole and PlatformStrategyAndroid
 *        subclasses extend this class.
 */
public abstract class PlatformStrategy {
    /** 
     * The singleton @a PlatformStrategy instance. 
     */
    private static PlatformStrategy sUniqueInstance = null;

    /** 
     * Method to return the one and only singleton instance. 
     */
    public static PlatformStrategy instance() {
        return sUniqueInstance;
    }

    /** 
     * Method that sets a new PlatformStrategy singleton and returns the one
     * and only singleton instance.
     */
    public static PlatformStrategy instance(PlatformStrategy platform) {
        return sUniqueInstance = platform;
    }

    /**
     * Return the path for the directory where images are stored.
     */
    public abstract String getDirectoryPath();

    /**
     * Factory method that creates an @a Image from a byte array.
     */
    public abstract Image makeImage(byte[] imageData);

    /**
     * Apply a grayscale filter to the @a inputEntity and return it.
     */
    public abstract InputEntity applyGrayscaleFilter(InputEntity inputEntity);

    /**
     * Store the @a image in the given @outputFile.
     */
    public abstract void storeImage(Image image,
                                    FileOutputStream outputFile);

    /**
     * Error log formats the message and displays it for the debugging
     * purposes.
     */
    public abstract void errorLog(String javaFile,
                                  String errorMessage);

    /**
     * Make the constructor protected to ensure singleton access.
     */
    protected PlatformStrategy() {}
}
