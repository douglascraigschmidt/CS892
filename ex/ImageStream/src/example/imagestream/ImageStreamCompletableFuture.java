package example.imagestream;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @class ImageStreamCompletableFuture
 *
 * @brief Customizes ImageStream to use Java 8 CompletableFutures to
 *        download, process, and store images concurrently.
 */
public class ImageStreamCompletableFuture extends ImageStream {
    /**
     * Constructor initializes the superclass and data members.
     */
    public ImageStreamCompletableFuture(Filter[] filters,
                                        Iterator<List<URL>> urlListIterator,
                                        Runnable completionHook) {
        super(filters, urlListIterator, completionHook);
    }

    /**
     * Initiate the ImageStream processing, which uses Java 8
     * CompletableFutures to download, process, and store images
     * concurrently.
     */
    @Override
    protected void initiateStream() {
        // Create a new barrier for this iteration cycle.
        mIterationBarrier = new CountDownLatch(1);

        // Concurrently process each filter in the mFilters List.
    	mFilters.parallelStream()
            .forEach(filter -> {
                    List<URL> urls = getInput();
                    // Use Java streams and CompletableFutures to
                    // download and filter all urls concurrently.
                    List<CompletableFuture<ImageEntity>> imageFutures =
                        urls.stream()

                        // Concurrently supply an async Callable task
                        // to the Executor framework that calls
                        // processInput() to download and filter an
                        // image retrieved from a given URL, stores
                        // the results in a file.
                        .map(url -> CompletableFuture.supplyAsync
                                      (() -> processInput(url, 
                                                          filter),
                              getExecutor()))
                        // Put futures returns from supplyAsync() into
                        // in List.
                        .collect(Collectors.toList());
                         
                    // Sequentially process image Future results.
                    imageFutures.stream()
                        // Join with CompletableFutures and then
                        // indicate if they succeeded or not.
                        .map(CompletableFuture::join)
                        .forEach(image ->
                                 // Indicate success or failure.
                                 PlatformStrategy.instance().errorLog
                                 ("ImageStreamCompletableFuture",
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
