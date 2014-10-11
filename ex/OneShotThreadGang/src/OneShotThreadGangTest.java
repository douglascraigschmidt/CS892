import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @class OneShotThreadGangTest
 *
 * @brief This program tests various subclassse of the
 *        OneShotThreadGang framework, which provide different
 *        implementations of an "embarraassingly parallel" application
 *        that searches for words in a List of Strings.
 *
 * @@ NS: Need to improve the documentation.
 */
public class OneShotThreadGangTest {
    // @@ NS: Need to use enums instead of ints.
    private final static int EXECUTOR = 0;
    private final static int JOIN = 1;
    private final static int COUNTDOWNLATCH = 2;

    /**
     * If this is set to true then lots of debugging output will be
     * generated.
     */
    public static boolean diagnosticsEnabled = true;

    /**
     * @class SearchOneShotThreadGangCommon
     * 
     * @brief This helper class factors out the common code used by
     *        all the implementations of OneShotThreadGang below.  It
     *        customizes the OneShotThreadGang framework to
     *        concurrently search an array of Strings for an array of
     *        words to find.
     */
    static public abstract class SearchOneShotThreadGangCommon
                  extends OneShotThreadGang<String, String> {
        /**
         * The array of words to find.
         */
        final String[] mWordsToFind;
        
        /**
         * Constructor initializes the data members and super class.
         */
        public SearchOneShotThreadGangCommon(String[] wordsToFind, 
                                             List<String> inputList) {
            // Pass input to search to superclass constructor.
            super(inputList);
            mWordsToFind = wordsToFind;
        }

        /**
         * Hook method that processes the results.
         */
        protected void processResults(String results) {
            // @@ NS: Need to do something more interesting here.
            printDebugging(results);
        }

        /**
         * Runs in a background Thread and searches the inputData for
         * all occurrences of the words to find.  Each time a match is
         * found the processResults() hook method is called to handle
         * the results.
         */
        public boolean doWorkInBackground (String inputData) {
            for (String word : mWordsToFind) 
                for (int i = inputData.indexOf(word, 0);
                     i != -1;
                     i = inputData.indexOf(word, i + word.length()))
                    processResults("in thread " 
                                   + Thread.currentThread().getId()
                                   + " "
                                   + word
                                   + " was found at offset "
                                   + i
                                   + " in string "
                                   + inputData);
            return true;
        }
    }

    /**
     * @class SearchOneShotThreadGangJoin
     *
     * @brief Customizes the OneShotThreadGangCommon framework to
     *        spawn a Thread for each element in the List of input
     *        Strings and uses Thread.join() to wait for all the
     *        Threads to finish.
     */
    static public class SearchOneShotThreadGangJoin 
                  extends SearchOneShotThreadGangCommon {
        /**
         * The List of worker Threads that were created.
         */
        private List<Thread> mWorkerThreads;
        
        /**
         * Hook method that uses the CountDownLatch as an exit barrier
         * to wait for the gang of Threads to exit.
         */
        protected void awaitDone() {
            try {
                for (Thread thread : mWorkerThreads)
                    thread.join();
            } catch (InterruptedException e) {
            }
        }

        /**
         * Hook method that's a no-op in this implementation.
         */
        protected void workerDone() {
            // no-op.
        }
        
        /**
         * Hook method that initiates the gang of Threads.
         */
        protected void initiateThreadGang(int size) {
            // Create and start a Thread for each element in the input
            // List - each Thread performs the processing designated
            // by the doWorkInBackgroundThread() hook method.
            for (int i = 0; i < size; ++i) {
                Thread t = new Thread(makeWorker(i));
                mWorkerThreads.add(t);
                t.start();
            }
        }

        /**
         * Constructor initializes the data members and super class.
         */
        SearchOneShotThreadGangJoin(String[] wordsToFind, 
                                    List<String> inputList) {
            // Pass input to search to superclass constructor.
            super(wordsToFind, inputList);
            
            // This List holds the Threads.
            mWorkerThreads = new LinkedList<Thread>();
        }
    }
  
    /**
     * @class SearchOnThreadGangCountDownLatch
     *
     * @brief Customizes the OneShotThreadGangCommon framework to
     *        spawn a Thread for each element in the List of input
     *        Strings and uses CountDownLatch to wait for all the
     *        Threads to finish.
     */
    static public class SearchOneShotThreadGangCountDownLatch 
                  extends SearchOneShotThreadGangCommon {
        /**
         * CountDownLatch that's used to coordinate the processing
         * threads.
         */
        protected CountDownLatch mBarrier;
        
        /**
         * Hook method that uses the CountDownLatch as an exit barrier
         * to wait for the gang of Threads to exit.
         */
        protected void awaitDone() {
            try {
                mBarrier.await();
            } catch (InterruptedException e) {
            }
        }

        /**
         * Hook method called when a worker Thread is done - it
         * decrements the CountDownLatch.
         */
        protected void workerDone() {
            mBarrier.countDown();
        }
        
        /**
         * Hook method that initiates the gang of Threads.
         */
        protected void initiateThreadGang(int size) {
            // Create and start a Thread for each element in the input
            // List - each Thread performs the processing designated by
            // the doWorkInBackgroundThread() hook method.
            for (int i = 0; i < size; ++i) {
                Thread t = new Thread(makeWorker(i));
                t.start();
            }
        }

        /**
         * Constructor initializes the data members and super class.
         */
        SearchOneShotThreadGangCountDownLatch(String[] wordsToFind, 
                                              List<String> inputList) {
            // Pass input to search to superclass constructor.
            super(wordsToFind, inputList);
            
            // Create a CountDownLatch whose count corresponds to each
            // element in the input List.
            mBarrier = new CountDownLatch(mInputList.size());
        }
    }
  
    /**
     * @class SearchOneShotThreadGangExecutor
     *
     * @brief Customizes the OneShotThreadGangCommon framework to
     *        spawn a pool of Threads via the ExecutorService, which
     *        is also used to wait for all the Threads to shutdown.
     */
    static public class SearchOneShotThreadGangExecutor
                  extends SearchOneShotThreadGangCommon {
        /**
         * The List of worker Threads that were created.
         */
        private ExecutorService mExecutorService;
        
        /**
         * Number of Threads in the pool.
         */ 
        private final int MAX_THREADS = 4;
        
        /**
         * Hook method that shutsdown the ExecutorService's Thread
         * pool and waits for all the Threads to exit before
         * returning.
         */
        protected void awaitDone() {
            mExecutorService.shutdown();
            try {
                mExecutorService.awaitTermination(Long.MAX_VALUE,
                                                  TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
            }
        }

        /**
         * Hook method that's a no-op in this implementation.
         */
        protected void workerDone() {
            // no-op.
        }
        
        /**
         * Hook method that initiates the gang of Threads by using a
         * Thread pool.
         */
        protected void initiateThreadGang(int size) {
            // Create a fixed-size Thread pool.
            mExecutorService =
                Executors.newFixedThreadPool(MAX_THREADS);

            // Enqueue each item in the input List for execution in
            // the Executor's Thread pool.
            for (int i = 0; i < size; ++i) 
                mExecutorService.execute(makeWorker(i));
        }

        /**
         * Constructor initializes the superclass.
         */
        SearchOneShotThreadGangExecutor(String[] wordsToFind, 
                                        List<String> inputList) {
            // Pass input to search to superclass constructor.
            super(wordsToFind, inputList);
        }
    }
  
    /**
     * Factory method that creates the desired type of
     * SearchOneShotThreadGangCommon subclass implementation.
     */
    private static SearchOneShotThreadGangCommon 
                   makeOneShotThreadGang(String[] wordList,
                                         List<String> inputList,
                                         int choice) {
    	SearchOneShotThreadGangCommon s = null;
        // @@ NS: need to replace with enums.
        switch(choice) {
        case EXECUTOR:
            s = new SearchOneShotThreadGangExecutor(wordList, inputList);
            break;
        case JOIN:
            s = new SearchOneShotThreadGangJoin(wordList, inputList);
            break;
        case COUNTDOWNLATCH:
            s = new SearchOneShotThreadGangCountDownLatch(wordList, 
                                                             inputList);
            break;
        }
        return s;
    }
    
    /**
     * Print debugging output if @code diagnosticsEnabled is true.
     */
    static void printDebugging(String output) {
        if (diagnosticsEnabled)
            System.out.println(output);
    }

    /**
     * This is the entry point into the test program.  
     */
    static public void main(String[] args) {
    	printDebugging("Starting ThreadGangTest");
     
        // List of words to search for.
        String[] wordList = {"do",
                             "re",
                             "mi",
                             "fa",
                             "so",
                             "la",
                             "ti",
                             "do"};
        
        // @@ NS: Need to improve so that the input comes from file,
        // not from hard-coded strings!

        // This input List to search concurrently.
        List<String> inputList = Arrays.asList("xdoodoo", "xreo", "xmiomio", "xfao", "xsoosoo", 
                                               "xlao", "xtiotio", "xdoo", "xdoo", "xreoreo", "xmio", 
                                               "xfaofao", "xsoo", "xlaolao", "xtio", "xdoodoo");

        // @@ NS: Need to improve so that it iterates through all the
        // enums rather than being hard-coded.  Also, need to add
        // timing statistics.

        // Create/run appropriate type of OneShotSearchThreadGang to
        // search for words concurrently
        printDebugging("Starting EXECUTOR");
        makeOneShotThreadGang(wordList, inputList, EXECUTOR).run();
        printDebugging("Ending EXECUTOR");
        
        printDebugging("Starting JOIN");
        makeOneShotThreadGang(wordList, inputList, JOIN).run();
        printDebugging("Ending JOIN");

        printDebugging("Starting COUNTDOWNLATCH");
        makeOneShotThreadGang(wordList, inputList, COUNTDOWNLATCH).run();
        printDebugging("Ending COUNTDOWNLATCH");

        printDebugging("Ending ThreadGangTest");
    }
}
