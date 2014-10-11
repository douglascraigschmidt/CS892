import java.util.concurrent.CyclicBarrier;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

/**
 * @class CyclicThreadGang
 *
 * @brief Defines a framework for spawning and running a "gang" of
 *        Threads that concurrently process input from a generic List
 *        of elements E for one or more cycles, where each cycle is
 *        controlled via a CyclicBarrier.
 */
public abstract class CyclicThreadGang<E, R> implements Runnable {
    /**
     * The input list that's processed, which can be changed for each
     * cycle via the @code makeNextInputList() factory method.
     */
    protected List<E> mInputList;
    
    /**
     * Factory method that makes the next List of input to be
     * processed concurrently by the gang of Threads.
     */
    protected abstract List<E> makeNextInputList();

    /**
     * Factory method that creates the barrier action for the
     * CyclicBarrier, which is typically used to get the next List of
     * input data to process concurrently.  Can return null if there's
     * no barrier action.
     */
    protected abstract Runnable makeBarrierAction();

    /**
     * Hook method that returns true when all processing is finished
     * so the gang of Threads will exit.
     */
    protected abstract boolean done();

    /**
     * Hook method that performs work a background Thread.  Returns
     * true if all goes well, else false (which will stop this
     * particular background Thread from continuing to run).
     */
    public abstract boolean doWorkInBackground(E inputData);

    /**
     * Hook method that can be used as an exit barrier to wait for the
     * gang of Threads to exit.
     */
    protected abstract void awaitDone();

    /**
     * Each Thread in the gang can use this method to wait for all the
     * other Threads to complete their current cycle.  Returns true if
     * the wait was successfuly or false if an exception occurs.
     */
    protected abstract boolean awaitNextCycle();
        
    /**
     * Hook method that initiates the gang of Threads.
     */
    protected abstract void initiateThreadGang(int inputSize);

    /**
     * Hook method that can be used to process the results.
     */
    protected abstract void processResults(R results);

    /**
     * Template method that runs all the Threads in the gang.  It
     * first calls the makeNextInputList() hook method to get the
     * initial input List.  It then creates a CyclicBarrier whose (1)
     * "parties" count corresponds to each element in the input List
     * and (2) barrier action (if any) is initialized via the
     * makeBarrierAction() hook method).  Next a new Thread is created
     * and started for each element in the input List - each Thread
     * performs the processing designated by the
     * doWorkInBackgroundThread() hook method.  Finally, the
     * awaitDone() hook method is called to wait for the processing in
     * all worker Threads to complete before returning to the caller.
     */
    @Override
    public void run() {
        // Get initial List of input data to process.
        mInputList = makeNextInputList();

        // Invoke this hook method to initialize the gang of Threads.
        initiateThreadGang(mInputList.size());

        // Wait for the processing in all worker Threads to complete
        // before returning to the caller.
        awaitDone();
    }

    /**
     * Factory method that creates a Runnable worker to process the
     * current contents of the input list.
     */
    protected Runnable makeWorker(final int index) {
        return new Runnable() {

            // This method runs in background Thread.
            public void run() {

                // Keep running until instructed to stop.
                while (!done()) {
                    // Process the input data in a background Thread.
                    if (doWorkInBackground(mInputList.get(index)) == false)
                        return;
                    // Wait until the next cycle begins.
                    else if (awaitNextCycle() == false)
                        return;
                }
            }
        };
    }
}
