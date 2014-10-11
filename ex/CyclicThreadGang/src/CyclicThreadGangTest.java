import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * @class CyclicThreadGangTest
 *
 * @brief This program ...
 */
public class CyclicThreadGangTest {
    /**
     * If this is set to true then lots of debugging output will be
     * generated.
     */
    public static boolean diagnosticsEnabled = true;

    /**
     * @class SearchCyclicThreadGang
     *
     * @brief Customizes the CyclicThreadGang framework to concurrently
     *        search arrays of Strings for an array of words to find.
     */
    static public class SearchCyclicThreadGang 
                  extends CyclicThreadGang<String, String> {
        /**
         * Input to search.
         */
        private String[] mInputStrings[] =
            { {"xdoodoo", "xreo", "xmiomio", "xfao", "xsoosoo", "xlao", "xtiotio", "xdoo"},
              {"xdoo", "xreoreo", "xmio", "xfaofao", "xsoo", "xlaolao", "xtio", "xdoodoo"} };

        /**
         * Number of arrays of strings to search.
         */
        private int mCount;
        
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
         * The array of words to find.
         */
        final String[] mWordsToFind;
        
        /**
         * Constructor initializes the data members;
         */
        SearchCyclicThreadGang(String[] wordsToFind) {
            mCount = mInputStrings.length;
            mWordsToFind = wordsToFind;
            mExitLatch = new CountDownLatch(1);
        }

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
        protected boolean awaitNextCycle() {
            try {
                mBarrier.await();
                return true;
            } catch (InterruptedException ex) {
                return false;
            } catch (BrokenBarrierException ex) {
                return false;
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
         * Runs in a background Thread and searches the inputData for
         * all occurrences of the words to find.
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

        /**
         * When there's no more input data to process releases the
         * exit latch and returns true, else returns false.
         */
        @Override
            protected boolean done() {
            if (mInputList == null) {
                mExitLatch.countDown();
                return true;
            }
            else 
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
     * This is the entry point into the test program.  It 
     */
    static public void main(String[] args) {
        if (diagnosticsEnabled)
            System.out.println("Starting CyclicThreadGangTest");

        // List of words to search for.
        String[] wordList = {"do",
                             "re",
                             "mi",
                             "fa",
                             "so",
                             "la",
                             "ti",
                             "do"};

        if (diagnosticsEnabled)
            System.out.println("@@@@@ Started first cycle @@@@@");

        // Start running the CyclicThreadGang to search for words
        // concurrently.
        new SearchCyclicThreadGang(wordList).run();

        if (diagnosticsEnabled)
            System.out.println("Ending CyclicThreadGangTest");
    }
}
