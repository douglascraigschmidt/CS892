import java.util.concurrent.CountDownLatch;
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
     * CountDownLatch that's used to coordinate the processing
     * threads.
     */
    protected CountDownLatch mBarrier;

    /**
     * Hook method that performs work a background Thread.  Returns
     * true if all goes well, else false (which will stop the
     * background Thread from continuing to run).
     */
    public abstract boolean doWorkInBackground(E inputData);

    /**
     * Constructor initializes the input List.
     */
    OneShotThreadGang(List<E> inputList) {
        mInputList = inputList;
    }

    /**
     * Template method that runs all the Threads in the gang.  It
     * first calls the makeInputList() hook method to get the
     * initial input List.  It then creates a CyclicBarrier whose
     * "parties" count corresponds to each element in the input List
     * and whose barrier action (if any) is initialized via the
     * makeBarrierAction() hook method).  Next it creates and starts a
     * new Thread that performs the processing designated by the
     * makeWorker() hook method for each element in the input List.
     * Finally, it calls the awaitDone() hook method to wait for all
     * the processing to complete.
     */
    @Override
    public void run() {
        final int size = mInputList.size();

        // Create a CountDownLatch whose count corresponds to each
        // element in the input List.
        mBarrier = new CountDownLatch(size);

        // Create and start a Thread for each element in the input
        // List - each Thread performs the processing designated by
        // the doWorkInBackgroundThread() hook method.
        for (int i = 0; i < size; ++i)
            new Thread(makeWorker(i)).start();

        try {
            // Wait for all the background Threads to exit.
            mBarrier.await();
        } catch (InterruptedException e) {
        }
    }

    /**
     * Factory method that creates a Runnable worker to process the
     * current contents of the input list.
     */
    protected Runnable makeWorker(final int index) {
        return new Runnable() {
            // This method runs in background Thread.
            public void run() {
                // Process the input data in a background Thread.

                doWorkInBackground(mInputList.get(index));

                // Indicate that this Thread is finished.
                mBarrier.countDown();
            }
        };
    }
}
