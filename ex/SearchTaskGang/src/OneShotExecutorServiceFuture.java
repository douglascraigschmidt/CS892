import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @class OneShotExecutorServiceFuture
 * 
 * @brief Customizes the SearchTaskGangCommon framework to process a
 *        one-shot List of tasks via a variable-sized pool of Threads
 *        created by the ExecutorService. The unit of concurrency is a
 *        "task per search word". The results processing model uses
 *        the Synchronous Future model, which defers the results
 *        processing until all words to search for have been
 *        submitted.
 */
public class OneShotExecutorServiceFuture
       extends SearchTaskGangCommon {
    /**
     * A List of Futures that contain SearchResults.
     */
    protected List<CompletableFuture<SearchResults>> mResultFutures;

    /**
     * Constructor initializes the superclass and data members.
     */
    protected OneShotExecutorServiceFuture(String[] wordsToFind,
                                           String[][] stringsToSearch) {
        // Pass input to superclass constructor.
        super(wordsToFind, 
              stringsToSearch);

        // Initialize the Executor with a cached pool of Threads,
        // which grow dynamically.
        setExecutor (Executors.newCachedThreadPool());
    }

    /**
     * Process all the Futures containing search results.
     */
    protected void processFutureResults(List<CompletableFuture<SearchResults>> resultFutures) {
        // Iterate through the List of Futures and print the search
        // results.
    	resultFutures.stream().map(CompletableFuture::join).forEach(SearchResults::print);   
    }

    /**
     * Hook method that performs work a background task.  Returns true
     * if all goes well, else false (which will stop the background
     * task from continuing to run).
     */
    protected boolean processInput(final String inputData) {
        ExecutorService executorService = 
            (ExecutorService) getExecutor();

        mResultFutures = 
        	Arrays.stream(mWordsToFind)
        	      .map(word -> CompletableFuture.supplyAsync
        	                         (() -> searchForWord(word, 
        											      inputData), 
              						  executorService)).
                       collect(Collectors.toList());
        return true;
    }

    /**
     * Initiate the TaskGang to process each word as a separate task
     * in the ExecutorService's Thread pool.
     */
    protected void initiateTaskGang(int inputSize) {
        // Preallocate the List of Futures to hold all the
        // SearchResults.
        mResultFutures = 
            new ArrayList<CompletableFuture<SearchResults>> 
            (inputSize * mWordsToFind.length);

        // Process each String of inputData via the processInput()
        // method.  Note that input Strings aren't run concurrently,
        // just eacd word that's being searched for.
        getInput().forEach(inputData -> processInput(inputData));

        // Process all the Futures.
        processFutureResults(mResultFutures);
    }
}

