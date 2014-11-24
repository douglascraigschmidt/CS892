package example.imagetaskgang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;

/**
 * @class ImageTaskGang
 *
 * @brief Customizes the TaskGang framework to use the Java
 *        ExecutorCompletionService to concurrently download a List of
 *        images from web servers, apply image processing filters to
 *        each image, and store the results in files that can be
 *        displayed to users via various means defined by the context
 *        in which this class is used.
 *
 *        This class implements the "..." in the Proactor pattern.
 */
public class ImageTaskGang extends TaskGang<URL> {
    /**
     * The List of filters to apply to the downloaded images.
     */
    private List<Filter> mFilters;

    /**
     * An iterator to the input URLs from which we will download
     * images.
     */
    private Iterator<List<URL>> mUrlIterator;

    /**
     * An ExecutorCompletionService that executes image filtering
     * tasks on designated URLs.
     */
    private ExecutorCompletionService<InputEntity> mCompletionService;

    /**
     * Set the completion hook that's called when all the images are
     * downloaded and processed.
     */
    private Runnable mCompletionHook;

    /**
     * The barrier that's used to coordinate each cycle, i.e., each
     * Thread must await on mIterationBarrier for all the other
     * Threads to complete their processing before they all attempt to
     * move to the next cycle en masse.
     */
    protected CountDownLatch mIterationBarrier = null;

    /**
     * Constructor initializes the superclass and data members.
     */
    public ImageTaskGang(Filter[] filters,
                         Iterator<List<URL>> urlIterator,
                         Runnable completionHook) {
        // Create an Iterator for the array of URLs to download.
        mUrlIterator = urlIterator;

        // Store the Filters to apply.
        mFilters = Arrays.asList(filters);

        // Initialize the Executor with a cached pool of Threads,
        // which grow dynamically.
        setExecutor(Executors.newCachedThreadPool());

        // Connect the Executor with the CompletionService to process
        // SearchResults concurrently.
        mCompletionService =
            new ExecutorCompletionService<InputEntity>(getExecutor());

        // Set the completion hook that's called when all the images
        // are downloaded and processed.
        mCompletionHook = completionHook;
    }

    /**
     * Each task in the gang uses the CountDownLatch countDown()
     * method indicate that they are done with their processing.
     */
    @Override
    protected void taskDone(int index) throws IndexOutOfBoundsException {
        try {
            // Indicate that this task is done with its computations.
            mIterationBarrier.countDown();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } 
    }

    /**
     * Factory method that returns the next List of URLs to be
     * downloaded and processed concurrently by the ImageTaskGang.
     */
    @Override
    protected List<URL> getNextInput() {
        if (mUrlIterator.hasNext()) {
            // Note that we're starting a new cycle.
            incrementCycle();

            // Return a List containing the URLs to download
            // concurrently.
            return mUrlIterator.next();
        }
        else
            // Indicate that we're done.
            return null;
    }

    /**
     * Initiate the TaskGang to run each task in a pool of Threads
     */
    @Override
    protected void initiateTaskGang(int inputSize) {
        // Create a new iteration barrier with the appropriate size.
        mIterationBarrier = new CountDownLatch(inputSize);

        // Enqueue each item in the input List for execution in the
        // Executor's Thread pool.
        for (int i = 0; i < inputSize; ++i)
            getExecutor().execute(makeTask(i));

        // Process all the Futures concurrently via the
        // ExecutorCompletionService's completion queue.
        concurrentlyProcessFilteredResults();
    }

    /**
     * Block on the ExecutorCompletionService's completion queue,
     * until all the processed downloads have been received.  Store
     * the processed downloads in an organized manner
     */
    protected void concurrentlyProcessFilteredResults() {
        // Need to account for all the downloaded images and all the
        // filtering of these images.
        final int count = getInput().size() * mFilters.size();

        // Loop for the designated number of results.
        for (int i = 0; i < count; ++i) 
            try {
                // Take the next ready Future off the
                // CompletionService's queue.
                final Future<InputEntity> resultFuture =
                    mCompletionService.take();

                // The get() call will not block since the results
                // should be ready before they are added to the
                // completion queue.
                InputEntity inputEntity = resultFuture.get();
    
                PlatformStrategy.instance().errorLog
                    ("ImageTaskGang",
                     "Operation on file " 
                     + inputEntity.getSourceURL()
                     + " in iteration "
                     + currentCycle()
                     + (inputEntity.succeeded() == true 
                        ? " succeeded" 
                        : " failed"));
                     
            } catch (ExecutionException e) {
                PlatformStrategy.instance().errorLog("ImageTaskGang",
                                                     "get() ExecutionException");
            } catch (InterruptedException e) {
                PlatformStrategy.instance().errorLog("ImageTaskGang",
                                                     "get() InterruptedException");
            }
    }

    /**
     * Hook method that used as an exit barrier to wait for the gang
     * of tasks to exit.
     */
    @Override
    protected void awaitTasksDone() {
        try {
            for (;;) {
                // Wait until all the iteration cycles are done.
                mIterationBarrier.await();

                // Check to see if there's any input remaining to
                // process.
                if (setInput(getNextInput()) == null)
                    break;
                
                // Invoke hook method to initialize the gang of tasks.
                initiateTaskGang(getInput().size());
            } 

            // Only call the shutdown() and awaitTermination() methods
            // if we've actually got an ExecutorService (as opposed to
            // just an Executor).
            if (getExecutor() instanceof ExecutorService) {
                ExecutorService executorService = 
                    (ExecutorService) getExecutor();

                // Tell the ExecutorService to initiate a graceful
                // shutdown.
                executorService.shutdown();

                // Wait for all the tasks/threads in the pool to
                // complete.
                executorService.awaitTermination(Long.MAX_VALUE,
                                                 TimeUnit.NANOSECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PlatformStrategy.instance().errorLog("ImageTaskGang",
                                             "calling completion hook");

        // Run the completion hook now that all the image processing
        // is done.
        mCompletionHook.run();
    }

    /**
     * Hook method that returns true as long there's URLs for the
     * TaskGang to process.
     */
    @Override
    protected boolean advanceTaskToNextCycle() {
        return mUrlIterator.hasNext();
    }

    /**
     * Runs in a background Thread, downloads an image, and initiates
     * processing on the image via the ExecutorCompletionService.
     */
    @Override
    protected boolean processInput(URL urlToDownload) {
        // Download an image and store it in an ImageEntity object.
    	final ImageEntity originalImage =
            new ImageEntity(urlToDownload,
                            downloadContent(urlToDownload));

        // For each filter in the List of Filters, submit a task to
        // the ExecutorCompletionService that filters the image
        // downloaded from the given URL, stores the results in a
        // file, and puts the results of the filtered image in the
        // completion queue.
        for (final Filter filter : mFilters) {
        	
            // The ExecutorCompletionService will receive a callable
            // and invoke its call() method, which returns the
            // filtered InputEntity, which is an ImageEntity.
            mCompletionService.submit(new Callable<InputEntity>() {
                    @Override
                    public InputEntity call() {
                    	// Create an OutputFilterDecorator that
                        // contains the original filter and the
                        // original Image.
                        Filter decoratedFilter =
                            new OutputFilterDecorator(filter, 
                                                      originalImage);

                        // Filter the original image and store it in a
                        // file.
                        return decoratedFilter.filter(originalImage);
                    }
                });
        }

        return true;
    }

    /**
     * Download the contents found at the given URL and return them as
     * a raw byte array.
     */
    @SuppressLint("NewApi")
    private byte[] downloadContent(URL url) {
        // The size of the image downloading buffer
        final int BUFFER_SIZE = 4096;

        // @@ Nolan, please document this code.
        ByteArrayOutputStream ostream = 
            new ByteArrayOutputStream();
        byte[] readBuffer = new byte[BUFFER_SIZE];
        int bytes;

        try {
            // @@ Nolan, this wasn't compiling for me when
            // implementing via "try-with-resources", so I moved it
            // here.  I probably need to update Eclipse to use Java
            // 1.7.
            InputStream istream = (InputStream) url.openStream();

            while ((bytes = istream.read(readBuffer)) > 0) {
                ostream.write(readBuffer, 0, bytes);
            }
            return ostream.toByteArray();
        } catch (IOException e) {
            // "Try-with-resources" will handle cleaning up the istream
            e.printStackTrace();
            return null;
        }
    }
}
