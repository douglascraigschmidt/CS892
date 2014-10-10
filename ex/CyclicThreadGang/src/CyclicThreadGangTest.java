import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.CountDownLatch;
import java.util.List;
import java.util.ListIterator;

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
    static public class SearchCyclicThreadGang extends CyclicThreadGang<String> {
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
