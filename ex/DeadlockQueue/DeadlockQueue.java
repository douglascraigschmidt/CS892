import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayList;

/*
 * @class DeadlockQueue
 *
 * @brief Illustrates how deadlock can occur due to "circular
 * waiting".  It's particularly appropriate since the deadlock occurs
 * sporatically, which makes it even harder to identify and diagnose
 * the problem!
 */
class DeadlockQueue {
    /**
     * The queue consists of a List of Strings.
     */
    List<String> mQ = new ArrayList<String>();

    /**
     * True if the queue is empty.
     */
    Boolean isEmpty() {
        return mQ.size() == 0;
    }

    /**
     * Add a new String to the end of the queue.
     */
    void put(String msg){
        mQ.add(msg);
    } 

    /**
     * Remove the String at the front of the queue.
     */
    String take(){
        return mQ.remove(0);
    } 

    /**
     * Transfer the contents of src to dest.
     */
    static void transfer(DeadlockQueue src,
                         DeadlockQueue dest){
        // Acquire the locks for src and dest.
        synchronized(src) {
            synchronized(dest) {
                // Remove each element from src and put it into dest.
                while(!src.isEmpty()) {
                    dest.put(src.take());
                }
            }
        }
    }

    /**
     * Helper class that's passed a parameter to a Thread.
     */
    static class TransferRunnable implements Runnable {
        private DeadlockQueue mAQueue;
        private DeadlockQueue mBQueue;
        private int mIterations;

        /**
         * Constructor stores the parameters into data members.
         */
        public TransferRunnable(DeadlockQueue a,
                                DeadlockQueue b,
                                int iterations) {
            mAQueue = a;
            mBQueue = b;
            mIterations = iterations;
        }

        /**
         * This hook method is called in a new Thread and it transfers
         * the contents of mAQueue to mBQueue.
         */
        public void run() {
            for (int i = 0; i < mIterations; ++i)
                DeadlockQueue.transfer(mAQueue, mBQueue);
        }
    }

    /**
     * Entry point into the main Thread that creates two instances of
     * DeadlockQueue (aQueue and bQueue) and two Threads that attempt
     * to transfer the contents of aQueue and bQueue in opposite
     * orders.  Although this will work sometimes, it also often
     * deadlocks since the Deadlock.transfer() method running in one
     * Thread will acquire aQueue's monitor lock, while the
     * Deadlock.transfer() method running in another Thread will
     * acquire bQueue's monitor lock.  At this point, both Threads are
     * waiting to acquire the other DeadlockQueue's monitor lock,
     * which causes a circular wait that doesn't terminate!
     */
    static public void main(String[] args) {
        // Designated the number of iterations to run in each thread.
        int iterations =
            args.length > 0 ? Integer.parseInt(args[1]) : 1000000;

        // Create two DeadlockQueue's.
        final DeadlockQueue aQueue = new DeadlockQueue();
        final DeadlockQueue bQueue = new DeadlockQueue();

        // Create a Thread that transfers the contents of aQueue to
        // bQueue.
        new Thread(new TransferRunnable(aQueue,
                                        bQueue,
                                        iterations)).start();

        // Create a Thread that transfers the contents of bQueue to
        // aQueue.
        new Thread(new TransferRunnable(bQueue,
                                        aQueue,
                                        iterations)).start();
    }
}






