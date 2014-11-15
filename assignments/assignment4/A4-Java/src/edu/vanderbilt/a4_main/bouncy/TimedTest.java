package edu.vanderbilt.a4_main.bouncy;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import edu.vanderbilt.a4_android.bouncy.BarrierManager;
import edu.vanderbilt.a4_android.bouncy.BarrierManagerStrategy;
import edu.vanderbilt.a4_android.bouncy.BarrierManagerStrategy.Strategy;
import edu.vanderbilt.a4_android.bouncy.BouncyBalloon;
import edu.vanderbilt.a4_android.bouncy.BouncyBarrier;
import edu.vanderbilt.a4_android.bouncy.Point;

/**
 * @class TimedTest
 *
 * @brief This class runs a simulation of bouncy balloons using
 *        different BarrierManager strategies and outputs the time
 *        required to run each simulation.  
 */
public class TimedTest {
    public static void main(String args[]) {
        try {
            System.out.print("Running Synchronized Test: ");
            System.out.println
                (doTest(new BarrierManagerStrategy(Strategy.Synchronized)) + "ms");

            System.out.print("Running Reentrant Lock Test: ");
            System.out.println
                (doTest(new BarrierManagerStrategy(Strategy.ReentrantLock)) + "ms");

            System.out.print("Running RWLock Test: ");
            System.out.println
                (doTest(new BarrierManagerStrategy(Strategy.RWLock)) + "ms");
            
            System.out.print("Running CopyOnWrite Test: ");
            System.out.println
            (doTest(new BarrierManagerStrategy(Strategy.CopyOnWrite)) + "ms");
        

            System.out.print("Running StampedLock Test: ");
            System.out.println
                (doTest(new StampedBarrierManager()) + "ms");
        } catch (Exception e) {
            System.out.println("An exception occurred. Quitting.");
        }
    }

    /**
     * Performs a test on a barrier manager. Returns the number of
     * milliseconds taken to complete the test.
     */
    private static long doTest(BarrierManager manager)
        throws InterruptedException, BrokenBarrierException {

        // A cyclic barrier so that everyone starts at once.
        CyclicBarrier barrier = 
            new CyclicBarrier(TestOptions.NUM_THREADS + 1);
        // A countdown latch so we know when everyone is done.
        CountDownLatch latch =
            new CountDownLatch(TestOptions.NUM_THREADS);

        // Add an invincible barrier on the left side
        manager.addBarrier(new BouncyBarrier(new Point(0, 0),
                                             true));

        // Create the bouncy balloons and the threads
        for (int i = 1; i <= TestOptions.NUM_THREADS; ++i) {
            new Thread
                (new BouncyRunnable
                 (new BouncyBalloon
                  (new Point(i * 5, i * 5), 5, 0,
                   i + 1), manager, barrier,
                  latch)).start();

            // Add a barrier for each balloon to remove on the right
            // side
            manager.addBarrier
                (new BouncyBarrier
                 (new Point
                  (TestOptions.NUM_THREADS 
                   * 5 
                   * TestOptions.SCALE_FACTOR + 5 
                   * i,
                   0),
                  false));
        }

        long startTime = System.currentTimeMillis();
        // Let them start
        barrier.await();
        // Wait for them to finish
        latch.await();
        long endTime = System.currentTimeMillis();

        return endTime - startTime;
    }

    private static class BouncyRunnable implements Runnable {
        /**
         * A barrier that lets all the bouncy balloon threads start at the same time.
         */
        CyclicBarrier mBarrier;

        /**
         * The barrier manager we're testing.
         */
        BarrierManager mManager;

        /**
         * A countdown latch that we use to notify the main thread that we're done.
         */
        CountDownLatch mLatch;

        /**
         * The ballon we're managing.
         */
        BouncyBalloon mBalloon;

        /**
         * Constructor initializes the data members.
         */
        public BouncyRunnable(BouncyBalloon balloon, BarrierManager manager,
                              CyclicBarrier barrier, CountDownLatch latch) {
            mBalloon = balloon;
            mManager = manager;
            mLatch = latch;
            mBarrier = barrier;
        }

        /**
         * Hook method that runs the tests.
         */
        public void run() {

            // Wait for all the threads to arrive.
            try {
                mBarrier.await();
            } catch (Exception e) {
                System.out.println
                    ("Exception thrown in a thread. Quitting thread.");
                mLatch.countDown();
                return;
            }

            while (true) {
                // Bounce the ball if necessary. The manager takes
                // care of removing barriers.
                if (mBalloon.getBouncesLeft() > 0)
                    mManager.bounceAndRemoveIfNecessary(mBalloon);

                // If we've exploded
                if (mBalloon.getBouncesLeft() <= 0) 
                    break;

                // Otherwise, move the balloon.
                else 
                    mBalloon.move();
            }

            // Tell the main thread that we're done.
            mLatch.countDown();
        }
    }
}
