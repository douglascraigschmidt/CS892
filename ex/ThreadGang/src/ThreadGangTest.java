import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @class ThreadGangTest
 *
 * @brief This program tests various subclassse of the ThreadGang
 *        framework, which provide different implementations of an
 *        "embarraassingly parallel" application that searches for
 *        words in a List of Strings.
 *
 * @@ NS: Need to improve the documentation.
 */
public class ThreadGangTest {
    // @@ NS: Need to use enums instead of ints.
    private final static int EXECUTOR = 0;
    private final static int JOIN = 1;
    private final static int COUNTDOWNLATCH = 2;
    private final static int CYCLIC = 3;

    /**
     * If this is set to true then lots of debugging output will be
     * generated.
     */
    public static boolean diagnosticsEnabled = true;

    // @@ NS: Need to get this data from files rather than from hard-coded strings!
    /**
     * This input List will be searched concurrently by the cyclic
     * tests.
     */
    private final static String[] mInputStrings[] =
            { {"xdoodoo", "xreo"},
              // { {"xdoodoo", "xreo", "xmiomio", "xfao", "xsoosoo", "xlao", "xtiotio", "xdoo"},
              // {"xdoo", "xreoreo", "xmio", "xfaofao", "xsoo", "xlaolao", "xtio", "xdoodoo"} };
              {"xdoo", "xreoreo"} };

    /**
     * This input List will be searched concurrently by the one-shot
     * tests.
     */

    /**
     * @class SearchThreadGangCommon
     * 
     * @brief This helper class factors out the common code used by
     *        all the implementations of ThreadGang below.  It
     *        customizes the ThreadGang framework to concurrently
     *        search an array of Strings for an array of words to
     *        find.
     */
    static public abstract class SearchThreadGangCommon
                  extends ThreadGang<String, String> {
        /**
         * The array of words to find.
         */
        final String[] mWordsToFind;
        
        /**
         * Number of arrays of strings to search.
         */
        protected int mCount;
        
        /**
         * Factory method that returns the next List of Strings to be
         * searched concurrently by the gang of Threads.
         */
        @Override
        protected List<String> makeNextInputList() {
            if (mCount-- > 0) 
                return Arrays.asList(mInputStrings[mCount]);
            else 
                return null;
        }

        /**
         * Constructor initializes the data member.
         */
        public SearchThreadGangCommon(String[] wordsToFind) {
            mWordsToFind = wordsToFind;
            mCount = mInputStrings.length;
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
     * @brief Customizes the SearchThreadGangCommon framework to spawn
     *        a Thread for each element in the List of input Strings
     *        and uses Thread.join() to wait for all the Threads to
     *        finish.
     */
    static public class SearchOneShotThreadGangJoin 
                  extends SearchThreadGangCommon {
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
         * Hook method that initiates the gang of Threads.
         */
        protected void initiateThreadGang(int size) {
            // This List holds the Threads.
            mWorkerThreads = new LinkedList<Thread>();

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
        SearchOneShotThreadGangJoin(String[] wordsToFind) {
            // Pass input to search to superclass constructor.
            super(wordsToFind);
        }
    }
  
    /**
     * @class SearchOnThreadGangCountDownLatch
     *
     * @brief Customizes the SearchThreadGangCommon framework to spawn
     *        a Thread for each element in the List of input Strings
     *        and uses CountDownLatch to wait for all the Threads to
     *        finish.
     */
    static public class SearchOneShotThreadGangCountDownLatch 
                  extends SearchThreadGangCommon {
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
            // Create a CountDownLatch whose count corresponds to each
            // element in the input List.
            mBarrier = new CountDownLatch(size);

            // Create and start a Thread for each element in the input
            // List - each Thread performs the processing designated
            // by the doWorkInBackgroundThread() hook method.
            for (int i = 0; i < size; ++i) {
                Thread t = new Thread(makeWorker(i));
                t.start();
            }
        }

        /**
         * Constructor initializes the data members and super class.
         */
        SearchOneShotThreadGangCountDownLatch(String[] wordsToFind) {
            // Pass input to search to superclass constructor.
            super(wordsToFind);
        }
    }
  
    /**
     * @class SearchOneShotThreadGangExecutor
     *
     * @brief Customizes the SearchThreadGangCommon framework to spawn
     *        a pool of Threads via the ExecutorService, which is also
     *        used to wait for all the Threads to shutdown.
     */
    static public class SearchOneShotThreadGangExecutor
                  extends SearchThreadGangCommon {
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
        SearchOneShotThreadGangExecutor(String[] wordsToFind) {
            // Pass input to search to superclass constructor.
            super(wordsToFind);
        }
    }
  
    /**
     * Factory method that creates the desired type of
     * SearchThreadGangCommon subclass implementation.
     */
    private static SearchThreadGangCommon 
                   makeThreadGang(String[] wordList,
                                  int choice) {
    	SearchThreadGangCommon s = null;
        // @@ NS: need to replace with enums.
        switch(choice) {
        case EXECUTOR:
            s = new SearchOneShotThreadGangExecutor(wordList);
            break;
        case JOIN:
            s = new SearchOneShotThreadGangJoin(wordList);
            break;
        case COUNTDOWNLATCH:
            s = new SearchOneShotThreadGangCountDownLatch(wordList);
            break;
        case CYCLIC:
            s = new SearchCyclicThreadGang(wordList);
            break;
        }
        return s;
    }
    
    /**
     * @class SearchCyclicThreadGang
     *
     * @brief Customizes the SearchThreadGangCommon framework to
     *        concurrently search arrays of Strings for an array of
     *        words to find.
     */
    static public class SearchCyclicThreadGang 
                  extends SearchThreadGangCommon {
        /**
         * The barrier that's used to coordinate each cycle, i.e.,
         * each Thread must await on mBarrier for all the other
         * Threads to complete their processing before they all
         * attempt to move to the next cycle en masse.
         */
        protected CyclicBarrier mBarrier;

        /**
         * Controls when the framework exits.
         */
        final CountDownLatch mExitLatch;
        
        /**
         * Constructor initializes the data members;
         */
        SearchCyclicThreadGang(String[] wordsToFind) {
            super(wordsToFind);
            mExitLatch = new CountDownLatch(1);
        }

        /**
         * Factory method that creates the barrier action for the
         * CyclicBarrier, which checks to see if there's any more
         * input to process.
         */
        @Override
        protected Runnable makeBarrierAction() {
            return new Runnable() {
                public void run() {
                    mInputList = makeNextInputList();
                    if (mInputList != null && diagnosticsEnabled)
                        System.out.println("@@@@@ Started next cycle @@@@@");
                }
            };
        }

        /**
         * Each Thread in the gang uses a call to CyclicBarrier
         * await() to wait for all the other Threads to complete their
         * current cycle.
         */
        protected void workerDone() {
            try {
                mBarrier.await();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } 
        }

        /**
         * Hook method that initiates the gang of Threads.
         */
        protected void initiateThreadGang(int size) {
            // Create a CyclicBarrier whose (1) "parties" count
            // corresponds to each element in the input List and (2)
            // barrier action (if any) is initialized via the
            // makeBarrierAction() hook method.
            mBarrier = new CyclicBarrier(size,
                                         makeBarrierAction());

            // Create and start a Thread for each element in the input
            // List - each Thread performs the processing designated
            // by the doWorkInBackgroundThread() hook method.
            for (int i = 0; i < size; ++i)
                new Thread(makeWorker(i)).start();
        }

        /**
         * Hook method that processes the results.
         */
        protected void processResults(String results) {
            printDebugging(results);
        }

        /**
         * When there's no more input data to process releases the
         * exit latch and returns true, else returns false.
         */
        @Override
        protected boolean cycleDone() {
            if (mInputList == null) {
                mExitLatch.countDown();
                return true;
            } else
            	return false;
        }

        /**
         * Waits on an exit latch for the gang of Threads to exit.
         */
        @Override
        protected void awaitDone() {
            try {
                mExitLatch.await();
            } catch (InterruptedException e) {
            }
        }
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

        // @@ NS: Need to improve so that it iterates through all the
        // enums rather than being hard-coded.  Also, need to add
        // timing statistics.

        // Create/run appropriate type of OneShotSearchThreadGang to
        // search for words concurrently
        
        printDebugging("Starting COUNTDOWNLATCH");
        makeThreadGang(wordList, COUNTDOWNLATCH).run();
        printDebugging("Ending COUNTDOWNLATCH");

        printDebugging("Starting EXECUTOR");
        makeThreadGang(wordList, EXECUTOR).run();
        printDebugging("Ending EXECUTOR");
        
        printDebugging("Starting JOIN");
        makeThreadGang(wordList, JOIN).run();
        printDebugging("Ending JOIN");

        printDebugging("Starting CYCLIC");
        makeThreadGang(wordList, CYCLIC).run();
        printDebugging("Ending CYCLIC");
        
        printDebugging("Ending ThreadGangTest");
    }
}
