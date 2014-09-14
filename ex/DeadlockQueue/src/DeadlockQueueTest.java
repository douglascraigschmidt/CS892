/**
 * @class DeadlockQueueTest
 *
 * @brief 
 */
class DeadlockQueueTest {
    /**
     * @class TransferRunnable
     * 
     * @brief Helper class that's passed a parameter to a Thread.
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
     * Entry point into the program that creates two instances of
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
            args.length > 0 ? Integer.parseInt(args[0]) : 1000000;

        // Create two DeadlockQueue's.
        final DeadlockQueue aQueue = new DeadlockQueue();
        final DeadlockQueue bQueue = new DeadlockQueue();

        // Create/start a Thread that transfers the contents of aQueue
        // to bQueue.
        Thread t1 = new Thread(new TransferRunnable(aQueue,
                                                    bQueue,
                                                    iterations));
        System.out.println("starting first thread");
        t1.start();
        
        // Create/start a Thread that transfers the contents of bQueue to
        // aQueue.
        Thread t2 = new Thread(new TransferRunnable(bQueue,
                                                    aQueue,
                                                    iterations));
        System.out.println("starting second thread");
        t2.start();

        try {
            t1.join();
            System.out.println("joined first thread");
            t2.join();
            System.out.println("joined second thread");
        } catch (Exception e) {
            System.out.println("caught exception");
        }
    }
}
