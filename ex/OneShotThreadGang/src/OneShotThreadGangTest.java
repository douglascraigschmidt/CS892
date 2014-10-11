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
 * @brief This program ...
 */
public class OneShotThreadGangTest {
    /**
     * If this is set to true then lots of debugging output will be
     * generated.
     */
    public static boolean diagnosticsEnabled = true;

    static public abstract class SearchOneShotThreadGangCommon
                  extends OneShotThreadGang<String> {
        /**
         * The array of words to find.
         */
        final String[] mWordsToFind;
        
        /**
         * Constructor initializes the data members;
         */
        public SearchOneShotThreadGangCommon(String[] wordsToFind, 
                                             List<String> inputList) {
            // Pass input to search to superclass constructor.
            super(inputList);
            mWordsToFind = wordsToFind;
        }

        /**
         * Runs in a background Thread and searches the inputData for
         * all occurrences of the words to find.
         */
        public boolean doWorkInBackground (String inputData) {
            for (String word : mWordsToFind) 
                for (int i = inputData.indexOf(word, 0);
                     i != -1;
                     i = inputData.indexOf(word, i + word.length()))
                    if (diagnosticsEnabled)
                        System.out.println("in thread " 
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
     * @class SearchThreadGang
     *
     * @brief Customizes the OneShotThreadGang framework to
     *        concurrently search an array of Strings for an array of
     *        words to find.
     */
    static public class SearchOneShotThreadGangJoin 
                  extends SearchOneShotThreadGangCommon {
        /**
         * The List of worker Threads that were created.
         */
        private List<Thread> mWorkerThreads;
        
        /**
         * Hook method that uses the CountDownLatch as an exit barrier to
         * wait for the gang of Threads to exit.
         */
        protected void awaitDone() {
            try {
                for (Thread thread : mWorkerThreads)
                    thread.join();
            } catch (InterruptedException e) {
            }
        }

        /**
         * Hook method called when a worker Thread is done - it decrements
         * the CountDownLatch.
         */
        protected void workerDone() {
            // no-op.
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
                mWorkerThreads.add(t);
                t.start();
            }
        }

        /**
         * Constructor initializes the data members;
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
     * @class SearchThreadGang
     *
     * @brief Customizes the OneShotThreadGang framework to
     *        concurrently search an array of Strings for an array of
     *        words to find.
     */
    static public class SearchOneShotThreadGangCountDownLatch 
                  extends SearchOneShotThreadGangCommon {
        /**
         * CountDownLatch that's used to coordinate the processing
         * threads.
         */
        protected CountDownLatch mBarrier;
        
        /**
         * Hook method that uses the CountDownLatch as an exit barrier to
         * wait for the gang of Threads to exit.
         */
        protected void awaitDone() {
            try {
                mBarrier.await();
            } catch (InterruptedException e) {
            }
        }

        /**
         * Hook method called when a worker Thread is done - it decrements
         * the CountDownLatch.
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
         * Constructor initializes the data members;
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
     * @class SearchThreadGang
     *
     * @brief Customizes the OneShotThreadGang framework to
     *        concurrently search an array of Strings for an array of
     *        words to find.
     */
    static public class SearchOneShotThreadGangExecutor
                  extends SearchOneShotThreadGangCommon {
        /**
         * The List of worker Threads that were created.
         */
        private ExecutorService mExecutorService;
        
        private final int MAX_THREADS = 4;
        
        /**
         * Hook method that uses the CountDownLatch as an exit barrier to
         * wait for the gang of Threads to exit.
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
         * Hook method called when a worker Thread is done - it decrements
         * the CountDownLatch.
         */
        protected void workerDone() {
        }
        
        /**
         * Hook method that initiates the gang of Threads.
         */
        protected void initiateThreadGang(int size) {
            // Create a fixed-size Thread pool.
            mExecutorService = Executors.newFixedThreadPool(MAX_THREADS);

            // Enqueue each item in the input List for execution in
            // the Executor's Thread pool.
            for (int i = 0; i < size; ++i) {
                mExecutorService.execute(makeWorker(i));
            }

        }

        /**
         * Constructor initializes the data members;
         */
        SearchOneShotThreadGangExecutor(String[] wordsToFind, 
                                        List<String> inputList) {
            // Pass input to search to superclass constructor.
            super(wordsToFind, inputList);
        }
    }
  
    private static OneShotThreadGang<String> makeOneShotThreadGang(String[] wordList,
                                                                   List<String> inputList,
                                                                   boolean useJoin) {
        return new SearchOneShotThreadGangExecutor(wordList, inputList);
        //        return useJoin 
        // ? new SearchOneShotThreadGangJoin(wordList, 
        // inputList)
        // : new SearchOneShotThreadGangCountDownLatch(wordList, 
        //                                                inputList);
    }

    /**
     * This is the entry point into the test program.  It 
     */
    static public void main(String[] args) {
        if (diagnosticsEnabled)
            System.out.println("Starting ThreadGangTest");

        // List of words to search for.
        String[] wordList = {"do",
                             "re",
                             "mi",
                             "fa",
                             "so",
                             "la",
                             "ti",
                             "do"};
        
        List<String> inputList = Arrays.asList("xdoodoo", "xreo", "xmiomio", "xfao", "xsoosoo", 
                                               "xlao", "xtiotio", "xdoo", "xdoo", "xreoreo", "xmio", 
                                               "xfaofao", "xsoo", "xlaolao", "xtio", "xdoodoo");

        OneShotThreadGang<String> searchThreadGang =
            makeOneShotThreadGang(wordList, inputList, args.length > 0);

        // Start running the ThreadGang to search for words
        // concurrently.
        searchThreadGang.run();

        if (diagnosticsEnabled)
            System.out.println("Ending ThreadGangTest");
    }
}
