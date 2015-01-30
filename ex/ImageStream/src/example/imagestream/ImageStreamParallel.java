package example.imagestream;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @class ImageStreamParallel
 *
 * @brief Customizes ImageStream to use a Java 8 stream to download,
 *        process, and store images concurrently.
 */
public class ImageStreamParallel extends ImageStream {
    /**
     * Constructor initializes the superclass and data members.
     */
    public ImageStreamParallel(Filter[] filters,
                               Iterator<List<URL>> urlListIterator,
                               Runnable completionHook) {
        super(filters, urlListIterator, completionHook);
    }

    /**
     * Initiate the ImageStream processing, which uses a Java 8 stream
     * to download, process, and store images concurrently.
     */
    @Override
    protected void initiateStream() {
        // Create a new exit barrier.
        mIterationBarrier = new CountDownLatch(1);

        // Concurrently process each filter in the mFilters List.
    	mFilters.parallelStream()
            .forEach(filter -> {
                    List<URL> urls = getInput();
                    // Use Java 8 streams to download and filter all
                    // urls concurrently.
                    urls.parallelStream()
                        // Call processInput() to download and filter
                        // the image retrieved from the given URL,
                        // store the results in a file, and indicate
                        // success or failure.
                        .map(url -> processInput(url, filter))
                        .forEach(image ->
                                 // Indicate success or failure.
                                 PlatformStrategy.instance().errorLog
                                 ("ImageStreamParallel",
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
