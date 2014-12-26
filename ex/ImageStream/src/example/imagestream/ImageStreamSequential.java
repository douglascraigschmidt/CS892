package example.imagestream;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @class ImageStreamSequential
 *
 * @brief Customizes ImageStream to use a Java 8 stream to download,
 *        process, and store images sequentially.
 */
public class ImageStreamSequential extends ImageStream {
    /**
     * Constructor initializes the superclass and data members.
     */
    public ImageStreamSequential(Filter[] filters,
                                 Iterator<List<URL>> urlListIterator,
                                 Runnable completionHook) {
        super(filters, urlListIterator, completionHook);
    }

    /**
     * Initiate the ImageStream processing, which uses a Java 8 stream
     * to download, process, and store images sequentially.
     */
    @Override
    protected void initiateStream() {
        // Create a new barrier for this iteration cycle.
        mIterationBarrier = new CountDownLatch(1);

        // Sequentially process each filter in the mFilters List.
    	mFilters.forEach
            (filter -> {
                List<URL> urls = getInput();
                // Use Java 8 streams to download and filter all urls
                // sequentially.
                urls.stream()
                    // Call processInput() to download and filter the
                    // image retrieved from the given URL, store the
                    // results in a file, and indicate success or
                    // failure.
                    .map(url -> processInput(url, filter))
                    .forEach(image ->
                             // Indicate success or failure.
                             PlatformStrategy.instance().errorLog
                             ("ImageStreamSequential",
                              "Operations"
                              + (image.getSucceeded() == true 
                                 ? " succeeded" 
                                 : " failed")
                              + " on file " 
                              + image.getSourceURL())
                             );
            });

        // Indicate all computations in this iteration are done.
        try {
            mIterationBarrier.countDown();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } 
    }
}
