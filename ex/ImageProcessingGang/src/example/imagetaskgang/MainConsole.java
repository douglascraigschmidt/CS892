package example.imagetaskgang;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @class MainConsole
 *
 * @brief This class is the main entry point for a Java console
 *        version of the PingPong application.
 */
public class MainConsole {
    /**
     * Array of Filters to apply to the downloaded images.
     */
    private static Filter[] FILTERS = {
        new NullFilter(),
        new GrayScaleFilter(),
        new NullFilter("Null1"),
        new NullFilter("Null2"),
        new GrayScaleFilter("Gray1"),
        new GrayScaleFilter("Gray2")
    };

    /**
     * Get the input from the default array of arrays.
     * @throws IOException 
     */
    private static Iterator<List<URL>> getURLIterator() {
    	try {
            final URL[] urls1 = {        
                new URL("http://www.mariowiki.com/images/thumb/1/19/GoldMushroomNSMB2.png/200px-GoldMushroomNSMB2.png"),
                new URL("http://png-1.findicons.com/files/icons/2297/super_mario/256/mushroom_life.png")
            };
            final URL[] urls2 = {
                new URL("http://img4.wikia.nocookie.net/__cb20080812195802/nintendo/en/images/1/12/1upshroom.png"),
                new URL("http://www.mariowiki.com/images/thumb/5/57/Powerup-mini-mushroom-sm.png/200px-Powerup-mini-mushroom-sm.png"),
                new URL("http://a66c7b.medialib.glogster.com/media/92/92a90af3755a6e3de9faad540af216bc3cdd7839add09a7735c22844b725d55b/propeller-mushroom-jpg.jpg")
            };
            final List<List<URL>> variableNumberOfInputURLs = 
                new ArrayList<List<URL>>();
            variableNumberOfInputURLs.add(Arrays.asList(urls1));
            variableNumberOfInputURLs.add(Arrays.asList(urls2));
            return variableNumberOfInputURLs.iterator();
    	} catch (MalformedURLException e) {
            return null;
    	}   
    }

    /**
     * The Java virtual machine requires the instantiation of a main
     * method to run the console version of the PlayPingPong app.
     */
    public static void main(String[] args) {
        /** 
         * Initializes the Platform singleton with the appropriate
         * PlatformStrategy, which in this case will be the
         * ConsolePlatform.
         */
        PlatformStrategy.instance
            (new PlatformStrategyFactory
             (System.out).makePlatformStrategy());

        /** Initializes the Options singleton. */
        Options.instance().parseArgs(args);

        PlatformStrategy.instance().errorLog("MainConsole", 
                                             "Starting ImageTaskGangTest");

        new ImageTaskGang(FILTERS,
                          getURLIterator()).run();

        PlatformStrategy.instance().errorLog("MainConsole", 
                                             "Ending ImageTaskGangTest");
    }
}
