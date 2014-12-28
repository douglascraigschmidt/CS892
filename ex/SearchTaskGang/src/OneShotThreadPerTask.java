import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @class OneShotThreadPerTask
 *
 * @brief Customizes the SearchTaskGangCommon framework to process a
 *        one-shot List of tasks via an Executor that creates a Thread
 *        for each task. The Executor model is a Thread per task. The
 *        unit of concurrency is each input String. The results
 *        processing model is synchronous.
 */
public class OneShotThreadPerTask
       extends SearchTaskGangCommon {
    /**
     * The List of worker Threads that were created.
     */
    private List<Thread> mWorkerThreads;

    /**
     * Constructor initializes the superclass and data members.
     */
    public OneShotThreadPerTask(String[] wordsToFind,
                                String[][] stringsToSearch) {
        // Pass input to superclass constructor.
        super(wordsToFind,
              stringsToSearch);

        // This List holds Threads so they can be joined.
        mWorkerThreads = new LinkedList<Thread>();
    }

    /**
     * Initiate the TaskGang to run each task in a separate Thread.
     */
    protected void initiateTaskGang(int inputSize) {
        // Create a fixed-size Thread pool.
        if (getExecutor() == null) 
            // Create an Executor whose execute() method runs each 
        	// worker task in a separate Thread.
            setExecutor (r -> { Thread t = new Thread(r);
            					mWorkerThreads.add(t);
            					t.start();
                              }
                         );

        // Enqueue each item in the input List for execution in a
        // separate Thread.
        for (int i = 0; i < inputSize; ++i) 
            getExecutor().execute(makeTask(i));
    }

    /**
     * Runs in a background Thread and searches the inputData for all
     * occurrences of the words to find.
     */
    @Override
    protected boolean processInput (String inputData) {
        // Iterate through each word we're searching for and try to
        // find it in the inputData.
    	Arrays.stream(mWordsToFind).forEach(word -> { 
    		SearchResults results = searchForWord(word, inputData);
    		 // Each time a match is found the SearchResult.print()
            // method is called to print the output.  We put this
            // call in a synchronized block so the output isn't
            // scrambled.
            synchronized(System.out) {
                results.print();
            }
    	});
    	
        return true;
    }

    /**
     * Hook method that uses Thread.join() as an exit barrier to wait
     * for the gang of tasks to exit.
     */
    protected void awaitTasksDone() {
    	mWorkerThreads.forEach
    		(thread -> {
    			try {
    				thread.join();
    			} catch (InterruptedException e) {
    				System.out.println("awaitTasksDone interrupted");
    			}
    		});                 
    }
}

