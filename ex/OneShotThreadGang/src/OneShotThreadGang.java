import java.util.LinkedList;
import java.util.List;

/**
 * @class OneShotThreadGang
 *
 * @brief Defines a framework for spawning and running a "gang" of
 *        Threads that concurrently process input from a generic List
 *        of elements E for a single cycle.
 */
public abstract class OneShotThreadGang<E> implements Runnable {
    /**
     * The input list that's processed, which can be initialized via
     * the @code makeInputList() factory method.
     */
    protected final List<E> mInputList;

   /**
     * Constructor initializes the input List.
     */
    OneShotThreadGang(List<E> inputList) {
        mInputList = inputList;
    }

    /**
     * Hook method that performs work a background Thread.  Returns
     * true if all goes well, else false (which will stop the
     * background Thread from continuing to run).
     */
    public abstract boolean doWorkInBackground(E inputData);

    /**
     * Hook method that can be used as an exit barrier to wait for the
     * gang of Threads to exit.
     */
    protected abstract void awaitDone();

    /**
     * Hook method called when a worker Thread is done.
     */
    protected abstract void workerDone();
    
    /**
     * Hook method that initiates the gang of Threads.
     */
    protected abstract void initiateThreadGang(int inputSize);

    /**
     * Template method that runs all the Threads in the gang.  
     */
    @Override
    public void run() {
        // Invoke this hook method to initialize the gang of Threads.
        initiateThreadGang(mInputList.size());

        // Invoke this hook method to wait for all the Threads to
        // exit.
        awaitDone();
    }

    /**
     * Factory method that creates a Runnable worker that will process
     * one node of the input List (at location @code index) in a
     * background Thread.
     */
    protected Runnable makeWorker(final int index) {
        return new Runnable() {

            // This method runs in background Thread.
            public void run() {
                // Process the input data in a background Thread.
                doWorkInBackground(mInputList.get(index));


                // Indicate this worker Thread is finished.
                workerDone();
            }
        };
    }
}
