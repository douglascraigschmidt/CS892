import java.util.List;

/**
 * @class ThreadGang
 *
 * @brief Defines a framework for spawning and running a "gang" of
 *        Threads that concurrently process input from a generic List
 *        of elements E for one or more cycles.
 */
public abstract class ThreadGang<E, R> implements Runnable {
    /**
     * The input list that's processed, which can be initialized via
     * the @code makeInputList() factory method.
     */
    protected volatile List<E> mInputList = null;

    /**
     * Set the List to use as input.
     */
    protected void setInputList(List<E> inputList) {
        mInputList = inputList;
    }

    /**
     * Get the List to use as input.
     */
    protected List<E> getInputList() {
        return mInputList;
    }

    /**
     * Factory method that makes the next List of input to be
     * processed concurrently by the gang of Threads.
     */
    protected abstract List<E> makeNextInputList();

    /**
     * Hook method that performs work a background Thread.  Returns
     * true if all goes well, else false (which will stop the
     * background Thread from continuing to run).
     */
    public abstract boolean doWorkInBackground(E inputData);

    /**
     * Hook method that returns true when all processing in a cycle is
     * finished so the gang of Threads will exit.  By default, return
     * true, which makes this a one-shot ThreadGang unless this method
     * is overridden.
     */
    protected boolean cycleDone() {
        return true;
    }

    /**
     * Hook method that can be used as an exit barrier to wait for the
     * gang of Threads to exit.
     */
    protected abstract void awaitDone();

    /**
     * Hook method that can be used to process results.
     */
    protected abstract void processResults(R results);

    /**
     * Hook method called when a worker Thread is done.  Can be used
     * in conjunction with a one-shop or cyclic barrier to wait for
     * all the other Threads to complete their current cycle.  Is
     * passed the index of the work that's done.  Returns true if the
     * wait was successfuly or throws the IndexOutOfBoundsException if
     * the item has been removed.
     */
    protected void workerDone(int index) throws IndexOutOfBoundsException {
        // No-op.
    }
    
    /**
     * Hook method that initiates the gang of Threads.
     */
    protected abstract void initiateThreadGang(int inputSize);

    /**
     * Template method that runs all the Threads in the gang.  
     */
    @Override
    public void run() {
        // Invoke hook method to get initial List of input data to
        // process.
        setInputList (makeNextInputList());

        // Invoke hook method to initialize the gang of Threads.
        initiateThreadGang(getInputList().size());

        // Invoke hook method to wait for all the Threads to exit.
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

                do {
                    E element;
                    try {
                        element = getInputList().get(index);
                    } catch (IndexOutOfBoundsException e) {
                        // Bail out.
                        return;
                    }

                    // Process input data element.
                    if (doWorkInBackground(element) == false)
                        return;
                    else
                        try {
                            // Indicate the worker Thread is done with
                            // this cycle, which can block on an exit
                            // barrier.
                            workerDone(index);
                        } catch (IndexOutOfBoundsException e) {
                            return;
                        }

                    // Keep running until instructed to stop.
                } while (cycleDone() == false);
            }
        };
    }
}
