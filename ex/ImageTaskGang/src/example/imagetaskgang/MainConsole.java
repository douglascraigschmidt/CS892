package example.imagetaskgang;

import java.util.concurrent.CountDownLatch;

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
        new GrayScaleFilter()
    };

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

        // Create an exit barrier with a count of one to synchronize
        // with the completion of the image downloading and processing
        // in the TaskGang.
        final CountDownLatch mExitBarrier = 
            new CountDownLatch(1);

        new Thread(new ImageTaskGang(FILTERS,
        		PlatformStrategy.instance().getUrlIterator(
	        		PlatformStrategy.instance().getInputSource(
	        				Options.instance().getInputSource())),
    			new Runnable() {
    	        	@Override
    	        	public void run() {
    	        		mExitBarrier.countDown();
    	        	}
    		})).start();

        try {
            mExitBarrier.await();
        } catch (InterruptedException e) {
            PlatformStrategy.instance().errorLog("MainConsole", 
                                                 "await interrupted");
        }

        PlatformStrategy.instance().errorLog("MainConsole", 
                                             "Ending ImageTaskGangTest");
    }
}
