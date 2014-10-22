package edu.vuum.mocca;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @class PlayPingPong
 *
 * @brief This class implements a Java program that creates two threads, Ping
 *        and Pong, to alternately print "Ping" and "Pong", respectively, on the
 *        display. It uses the Template Method, Strategy, and Factory Method
 *        patterns to factor out common code and simplify the program design.
 */
public class PlayPingPong implements Runnable {
    /**
     * Number of iterations to ping/pong.
     */
    private static volatile int mMaxIterations;

    /** Maximum number of iterations per "turn" (defaults to 1). */
    private static volatile int mMaxTurns = 1;

    /**
     * Keeps track of the platform that we're running on, e.g.,
     * Android vs.  Console.
     */
    private static volatile PlatformStrategy mPlatformStrategy;

    /**
     * Which synchronization to use, e.g., "SEMA", "COND", "MONOBJ",
     * "QUEUE", and "PADDLE".  Defaults to "PADDLE".
     */
    private static String mSyncMechanism = "PADDLE";

    /**
     * Constants used to distinguish between ping and pong threads.
     */
    private final static int PING_THREAD = 0;
    private final static int PONG_THREAD = 1;

    /**
     * @Brief PingPongThread
     *
     * @class This class implements the core ping/pong algorithm, but
     *        defers the scheduling aspect to subclasses. It plays the
     *        role of the "Abstract Class" in the Template Method
     *        pattern.
     */
    static abstract class PingPongThread extends Thread {
        /**
         * Constructor initializes the various fields.
         */
        PingPongThread(String stringToPrint) {
            mStringToPrint = stringToPrint;
        }

        /**
         * Abstract hook methods that determine the ping/pong scheduling
         * protocol in the run() template method.
         */
        abstract void acquire();

        abstract void release();

        /**
         * Sets the id of the other thread.
         */
        void setOtherThreadId(long id) {
        }

        /**
         * This method runs in a separate thread of control and implements the
         * core ping/pong algorithm. It plays the role of the "template method"
         * in the Template Method pattern.
         */
        public void run() {
            for (int loopsDone = 1; loopsDone <= mMaxIterations; ++loopsDone) {
                // Perform the template method protocol for printing a
                // "ping" or a "pong" on the display. Note that the
                // acquire() and release() hook methods that control
                // the scheduling of the threads are deferred to
                // subclasses.

                acquire();

                mPlatformStrategy.print(mStringToPrint + "(" + loopsDone + ")");

                release();
            }

            // Indicate that this thread is done playing ping/pong.
            mPlatformStrategy.done();

            // Exit the thread when the loop is done.
        }

        // Data member that indicates the string to print (typically a
        // "ping" or a "pong").
        protected String mStringToPrint;
    }

    /**
     * @class PingPongThreadSema
     *
     * @brief This class uses semaphores to implement the acquire() and
     *        release() hook methods that schedule the ping/pong algorithm. It
     *        plays the role of the "Concrete Class" in the Template Method
     *        pattern.
     */
    static class PingPongThreadSema extends PingPongThread {
        /**
         * Semaphores that schedule the ping/pong algorithm
         */
        private Semaphore mSemas[] = new Semaphore[2];

        /**
         * Consts to distinguish between ping and pong Semaphores.
         */
        private final static int FIRST_SEMA = 0;
        private final static int SECOND_SEMA = 1;

        PingPongThreadSema(String stringToPrint, Semaphore firstSema,
                           Semaphore secondSema) {
            super(stringToPrint);
            mSemas[FIRST_SEMA] = firstSema;
            mSemas[SECOND_SEMA] = secondSema;
        }

        /**
         * Hook method for ping/pong acquire.
         */
        @Override
            void acquire() {
            mSemas[FIRST_SEMA].acquireUninterruptibly();
        }

        /**
         * Hook method for ping/pong release.
         */
        @Override
            void release() {
            mSemas[SECOND_SEMA].release();
        }

    }

    /**
     * @class PingPongThreadCond
     * 
     * @brief This class uses Conditions to implement the acquire() and
     *        release() hook methods that schedule the ping/pong algorithm. It
     *        plays the role of the "Concrete Class" in the Template Method
     *        pattern.
     */
    static class PingPongThreadCond extends PingPongThread {
        /**
         * Semaphores that schedule the ping/pong algorithm.
         */
        private Condition mConds[] = new Condition[2];

        /**
         * Monitor lock.
         */
        private ReentrantLock mLock = null;

        /**
         * Number of times we've iterated thus far in our "turn".
         */
        private int mTurnCountDown = 0;

        /**
         * Id for the other thread.
         */
        public long mOtherThreadId = 0;

        /**
         * Thread whose turn it currently is.
         */
        private static long mTurnOwner;

        public void setOtherThreadId(long otherThreadId) {
            this.mOtherThreadId = otherThreadId;
        }

        /**
         * Consts to distinguish between ping and pong conditions.
         */
        private final static int FIRST_COND = 0;
        private final static int SECOND_COND = 1;

        PingPongThreadCond(String stringToPrint,
                           ReentrantLock lock,
                           Condition firstCond,
                           Condition secondCond,
                           boolean isOwner) {
            super(stringToPrint);
            mTurnCountDown = mMaxTurns;
            mLock = lock;
            mConds[FIRST_COND] = firstCond;
            mConds[SECOND_COND] = secondCond;
            if (isOwner)
                mTurnOwner = this.getId();
        }

        /**
         * Hook method for ping/pong acquire.
         */
        @Override
        void acquire() {
            mLock.lock();

            while (mTurnOwner != this.getId()) {
                mConds[FIRST_COND].awaitUninterruptibly();
            }

            mLock.unlock();
        }

        /**
         * Hook method for ping/pong release.
         */
        @Override
            void release() {
            mLock.lock();

            --mTurnCountDown;

            if (mTurnCountDown == 0) {
                mTurnOwner = mOtherThreadId;
                mTurnCountDown = mMaxTurns;
                mConds[SECOND_COND].signal();
            }
            mLock.unlock();
        }
    }

    /**
     * @class PingPongThreadMonObj
     * 
     * @brief This class uses monitor objects to implement the acquire() and
     *        release() hook methods that schedule the ping/pong algorithm. It
     *        plays the role of the "Concrete Class" in the Template Method
     *        pattern.
     */
    static class PingPongThreadMonObj extends PingPongThread {
        /**
         * Semaphores that schedule the ping/pong algorithm.
         */
        private BinarySemaphore mSemas[] = new BinarySemaphore[2];

        /**
         * Consts to distinguish between ping and pong BinarySemaphores.
         */
        private final static int FIRST_SEMA = 0;
        private final static int SECOND_SEMA = 1;

        PingPongThreadMonObj(String stringToPrint,
                             BinarySemaphore firstSema,
                             BinarySemaphore secondSema,
                             boolean isOwner) {
            super(stringToPrint);
            mSemas[FIRST_SEMA] = firstSema;
            mSemas[SECOND_SEMA] = secondSema;
        }

        /**
         * Hook method for ping/pong acquire.
         */
        @Override
            void acquire() {
            mSemas[FIRST_SEMA].acquire();
        }

        /**
         * Hook method for ping/pong release.
         */
        @Override
            void release() {
            mSemas[SECOND_SEMA].release();
        }
    }

    /**
     * @class PingPongThreadQueue
     * 
     * @brief This class uses blocking queues to implement the
     *        acquire() and release() hook methods that schedule the
     *        ping/pong algorithm. It plays the role of the "Concrete
     *        Class" in the Template Method pattern.
     */
    public class PingPongThreadQueue extends PingPongThread {
        /**
         * These queues handle synchronization between our thread and
         * the other Thread.  We exploit the blocking features of the
         * queues, calling take() on the other thread's empty queue to
         * simulate conditional waiting and calling put() on our
         * thread to simulate notifying a waiter.
         */
        // TODO - You fill in here.

        /**
         * This "ball" is used to pass control between two Threads,
         * which avoids having to allocate memory dynamically each
         * time control is passed.
         */
        // TODO - You fill in here.

        /**
         * Constructor initializes the various fields.
         */
        PingPongThreadQueue(String stringToPrint,
                            LinkedBlockingQueue<Object> mine,
                            LinkedBlockingQueue<Object> other,
                            Object pingPongBall) {
            // TODO - You fill in here.
        }

        /**
         * Hook method for ping/pong acquire.
         */
        @Override
        void acquire() {
            // TODO - You fill in here.

        }

        @Override
        void release() {
            // TODO - You fill in here.
        }

    }

    /**
     * @class PingPongThreadBall
     * 
     * @brief This class uses the PingPongPaddle class to implement
     *        the acquire() and release() hook methods that schedule
     *        the ping/pong algorithm.  It plays the role of the
     *        "Concrete Class" in the Template Method pattern.
     */
    public class PingPongThreadBall extends PingPongThread {
        /**
         * These PingPongPaddle objects handle synchronization between
         * our thread and the other Thread.
         */
        // TODO - You fill in here.

        /**
         * Constructor initializes the various fields.
         */
        PingPongThreadBall(String stringToPrint,
                           PingPongPaddle mine,
                           PingPongPaddle other) {
            super(stringToPrint);
            // TODO - You fill in here.
        }

        /**
         * Hook method for ping/pong acquire.
         */
        @Override
        void acquire() {
            // Block until we receive the pingpong ball.
            // TODO - You fill in here.
        }

        @Override
        void release() {
             // Hit the pingball back.
            // TODO - You fill in here.
        }

    }

    /**
     * Constructor stores the PlatformStrategy and the number of iterations to
     * play ping/pong.
     */
    public PlayPingPong(PlatformStrategy platformStrategy,
                        int maxIterations,
			int maxTurns,
                        String syncMechanism) {
        // The PlatformStrategy being used.
        mPlatformStrategy = platformStrategy;

        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Number of iterations to perform pings and pongs per "turn".
        mMaxTurns = maxTurns;

        // Which synchronization to use (e.g., "SEMA", "COND",
        // "MONOBJ", "QUEUE", or "PADDLE").
        mSyncMechanism = syncMechanism;
    }

    private void makePingPongThreads(String schedMechanism,
                                     PingPongThread[] pingPongThreads) {
        // Use a pair of Java Semaphores.
        if (schedMechanism.equals("SEMA")) {
            // Create the semaphores that schedule threads
            // printing "ping" and "pong" in the correct
            // alternating order.
            Semaphore pingSema = new Semaphore(1); // Starts out unlocked.
            Semaphore pongSema = new Semaphore(0);

            pingPongThreads[PING_THREAD] = new PingPongThreadSema("ping",
                                                                  pingSema,
                                                                  pongSema);
            pingPongThreads[PONG_THREAD] = new PingPongThreadSema("pong",
                                                                  pongSema,
                                                                  pingSema);
        }
        // Use a pair of Java ConditionObjects.
        else if (schedMechanism.equals("COND")) {
            ReentrantLock lock = new ReentrantLock();
            Condition pingCond = lock.newCondition();
            Condition pongCond = lock.newCondition();
            int numberOfTurnsEach = 2;

            pingPongThreads[PING_THREAD] = new PingPongThreadCond("ping",
                                                                  lock,
                                                                  pingCond,
                                                                  pongCond,
                                                                  true);
            pingPongThreads[PONG_THREAD] = new PingPongThreadCond("pong",
                                                                  lock,
                                                                  pongCond,
                                                                  pingCond,
                                                                  false);
            pingPongThreads[PING_THREAD]
                .setOtherThreadId(pingPongThreads[PONG_THREAD].getId());
            pingPongThreads[PONG_THREAD]
                .setOtherThreadId(pingPongThreads[PING_THREAD].getId());
        }
        // Use a pair of Java BinarySemaphores implemented as monitor
        // objects.
        else if (schedMechanism.equals("MONOBJ")) {
            BinarySemaphore pingSema = new BinarySemaphore(false);
            BinarySemaphore pongSema = new BinarySemaphore(true);

            pingPongThreads[PING_THREAD] = new PingPongThreadMonObj("ping",
                                                                    pingSema,
                                                                    pongSema,
                                                                    true);
            pingPongThreads[PONG_THREAD] = new PingPongThreadMonObj("pong",
                                                                    pongSema,
                                                                    pingSema,
                                                                    false);
            pingPongThreads[PING_THREAD]
                .setOtherThreadId(pingPongThreads[PONG_THREAD].getId());
            pingPongThreads[PONG_THREAD]
                .setOtherThreadId(pingPongThreads[PING_THREAD].getId());
        }
        // Use a pair of Java LinkedBlockingQueue objects.
        else if (schedMechanism.equals("QUEUE")) {
            // TODO - You fill in here.
        }
        // Use a pair of PingPongPaddles
        else if (schedMechanism.equals("PADDLE")) {
            // TODO - You fill in here.
        }
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() function in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Indicate a new game is beginning.
        mPlatformStrategy.begin();

        // Let the user know we're starting. 
        mPlatformStrategy.print("Ready...Set...Go!");

        // Create the ping and pong threads. 
        PingPongThread pingPongThreads[] = new PingPongThread[2];
        pingPongThreads[PING_THREAD] = null;
        pingPongThreads[PONG_THREAD] = null;

        // Create the appropriate type of threads with the designated
        // scheduling mechanism (e.g., "SEMA" for Semaphores, "COND"
        // for ConditionObjects, "MONOBJ" for monitor objects, etc.).
        makePingPongThreads(mSyncMechanism,
                            pingPongThreads);

        // Start ping and pong threads, which calls their run()
        // methods.
        pingPongThreads[PING_THREAD].start();
        pingPongThreads[PONG_THREAD].start();

        // Barrier synchronization to wait for all work to be done
        // before exiting play().
        mPlatformStrategy.awaitDone();

        // Let the user know we're done.
        mPlatformStrategy.print("Done!");
    }
}
