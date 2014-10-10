import java.util.Arrays;
import java.util.List;

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

    /**
     * @class SearchThreadGang
     *
     * @brief Customizes the OneShotThreadGang framework to
     *        concurrently search an array of Strings for an array of
     *        words to find.
     */
    static public class SearchOneShotThreadGang extends OneShotThreadGang<String> {
        /**
         * The array of words to find.
         */
        final String[] mWordsToFind;
        
        /**
         * Constructor initializes the data members;
         */
        SearchOneShotThreadGang(String[] wordsToFind) {
            // Pass input to search to superclass constructor.
            super(Arrays.asList("xdoodoo", "xreo", "xmiomio", "xfao", "xsoosoo", "xlao", "xtiotio", "xdoo",
                                "xdoo", "xreoreo", "xmio", "xfaofao", "xsoo", "xlaolao", "xtio", "xdoodoo"));
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

        // Start running the ThreadGang to search for words
        // concurrently.
        new SearchOneShotThreadGang(wordList).run();

        if (diagnosticsEnabled)
            System.out.println("Ending ThreadGangTest");
    }
}
