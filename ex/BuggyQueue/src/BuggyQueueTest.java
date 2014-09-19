import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @class BuggyQueueTest
 * 
 * @brief Test program for the SimpleQueue that induces race
 *        conditions due to lack of synchronization.
 */
public class BuggyQueueTest
{ 
    /**
     * Maximum number of iterations.
     */
    private final static int mMaxIterations = 1000000;

    /**
     * @class ProducerThread
     *
     * @brief This producer runs in a separate Java Thread and passes
     *        Strings to a consumer Thread via a shared BlockingQueue.
     */
    static class ProducerThread<BQ extends BlockingQueue> extends Thread {
        /**
         * This queue is shared with the consumer.
         */
        private final BQ mBlockingQueue;
        
        /**
         * Constructor initializes the BlockingQueue data
         * member.
         */
        ProducerThread(BQ blockingQueue) {
            mBlockingQueue = blockingQueue;
        }

        /**
         * This method runs in a separate Java Thread and passes
         * Strings to a consumer Thread via a shared BlockingQueue.
         */
        public void run(){ 
            try {
                for(int i = 0; i < mMaxIterations; i++)
                    // Calls the put() method, which blocks if the
                    // queue is full.
                    mBlockingQueue.put(Integer.toString(i)); 
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
    }

    /**
     * @class ConsumerThread
     *
     * @brief This consumer runs in a separate Java Thread and
     *        receives Strings from a producer Thread via a shared
     *        BlockingQueue.
     */
    static class ConsumerThread<BQ extends BlockingQueue> extends Thread {
        /**
         * This queue is shared with the producer.
         */
        private final BQ mBlockingQueue;
        
        /**
         * Constructor initializes the BlockingQueue data member.
         */
        ConsumerThread(BQ blockingQueue) {
            mBlockingQueue = blockingQueue;
        }

        /**
         * This method runs in a separate Java Thread and receives
         * Strings from a producer Thread via a shared BlockingQueue.
         */
        public void run(){ 
            try {
                for(int i = 0; i < mMaxIterations; i++) {
                    // Calls the take() method, which blocks if the
                    // queue is empty.
                    Object s = mBlockingQueue.take();

                    if((i % (mMaxIterations / 100)) == 0)
                        System.out.println(s);
                }
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
        }
    }

    /**
     * Main entry point that tests the SimpleQueue class.
     */
    public static void main(String argv[]) {
        final SimpleQueue<String> simpleQueue =
            new SimpleQueue<String>();

        try {
            // Create a ProducerThread.
            Thread producer =
                new ProducerThread(simpleQueue);
        
            // Create a ConsumerThread.
            Thread consumer =
                new ConsumerThread(simpleQueue);

            // Run both Threads concurrently.
            producer.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}

            consumer.start();

            // Wait for both Threads to stop.
            producer.join();
            consumer.join();
        } catch (Exception e) {
            System.out.println("caught exception");
        }
    }
}
