package example;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import filters.Filter;
import filters.OutputFilterDecorator;

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
        getInput().parallelStream()
    		// submit the url for downloading asynchronously
    		.map(url -> CompletableFuture.supplyAsync(
						   		() -> makeImageEntity(url),
	        					getExecutor()).join())
	        // map each entity to a parallel stream of the 
	        // filtered versions of the entity
            .flatMap(imageEntity ->
            	mFilters.parallelStream()
            		// decorate each filter to write the images to files
            		.map(filter -> new OutputFilterDecorator(filter))
            		// submit the imageEntity for asynchronous filtering
            		.map(decoratedFilter -> 
            			CompletableFuture.supplyAsync(
								() -> decoratedFilter.filter(imageEntity),
								getExecutor()).join())
					.collect(Collectors.toList()).parallelStream()	
            )
            // report the success of the pipeline for each filtered entity
    		.forEach(image -> PlatformStrategy.instance().errorLog
					                ("ImageStreamCompletableFuture",
					                 "Operations"
					                 + (image.getSucceeded() == true
					                   ? " succeeded" 
					                   : " failed")
					                 + " on file " 
					                 + image.getSourceURL())
             );


        // Indicate all computations in this iteration are done.
        try {
            mIterationBarrier.countDown();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } 
    }
}
