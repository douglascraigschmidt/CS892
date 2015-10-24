package edu.vandy.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// import android.util.Log;
import edu.vandy.presenter.BeingThread;

/**
 * Defines a mechanism that mediates concurrent access to a (smaller)
 * fixed number of available Palantiri.  Each Palantiri can be leased
 * for a designated amount of time.  If the lease expires before the
 * thread that acquired it has released it, this thread will be sent
 * an interrupt request that will cause it to receive the
 * InterruptedException so it can release the lease.
 *
 * This class implements the "Specific Notification" pattern
 * (www.dre.vanderbilt.edu/~schmidt/PDF/specific-notification.pdf) by
 * using a queue of WaitNode objects to ensure waiting threads acquire
 * Palantiri in FIFO order.  A HashMap and a ReentrantReadWriteLock
 * are used to mediate concurrent access to the Palantiri.  This class
 * also implements the "Pooling" pattern
 * (kircher-schwanninger.de/michael/publications/Pooling.pdf).
 */
public class PalantiriManager {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        PalantiriManager.class.getSimpleName();

    /**
     * Current number of available Palantiri.
     */
    private long mAvailablePalantiri; 

    /**
     * FIFO queue of Threads waiting to obtain a Palantir.
     */
    private final List<WaitNode> mWaitQueue = 
        new LinkedList<>();

    /**
     * A map that associates the @a Palantiri key to the @a LeaseState
     * values.
     */
    private final HashMap<Palantir, LeaseState> mPalantiriMap;

    /**
     * Synchronizes access to the HashMap and other fields shared by
     * multiple thread.
     */
    // TODO -- you fill in here (grad students use a
    // ReentrantReadWriteLock and undergrads can use ReentrantLock).

    /**
     * A callback object that is invoked to change the fairness
     * indicator if the PalantiriManager is unfair.
     */
    private Runnable mUnfairnessCallback;

    /**
     * Used to track if the PalantiriManager is fair.
     */
    private final FairnessChecker mFairnessChecker;

    /**
     * A special value that's used to indicate when a palantir is not
     * in use.
     */
    protected final LeaseState mNotInUse = new LeaseState();

    /**
     * Constructor creates a PalantiriManager.
     *
     * @param palantiri
     *        The List of palantiri to use as keys in the HashMap.
     */
    public PalantiriManager(List<Palantir> palantiri) {
        this(palantiri, null);
    }
    
    /**
     * Constructor creates a PalantiriManager.
     *
     * @param palantiri
     *        The List of palantiri to use as keys in the HashMap.
     * 
     * @param unfairnessCallback
     *        A Runnable whose run() method is invoked if the
     *        PalantiriManager implementation is not "fair".
     */
    public PalantiriManager(List<Palantir> palantiri,
                            Runnable unfairnessCallback) {
        // Initialize the HashMap.
        mPalantiriMap = new HashMap<>();

        // Insert the palantiri into the mPalantiriMap, using
        // mNotInUse to indicate that all palantiri are available for
        // use.
        for (Palantir key : palantiri)
            mPalantiriMap.put(key, 
                              mNotInUse);

        // Store the unfairness callback.
        mUnfairnessCallback = unfairnessCallback;

        // Create the FairnessChecker to ensure the PalantiriManager
        // provides Palantiri in FIFO order.
        mFairnessChecker =
            new FairnessChecker((int) mAvailablePalantiri);

        // Store the total number of available Palantiri.
        mAvailablePalantiri = palantiri.size();
        
        // Initialize the lock.
        // TODO -- you fill in here to create the appropriate lock.
    }

    /**
     * Get a Palantir from the PalantiriManager, blocking until one is
     * available.  The @code leaseDurationInMillis parameter specifies
     * the maximum amount of time the lease for the palantir will be
     * valid.  When this time is expires the Thread that acquired the
     * lease will be sent an interrupt request.
     */
    public Palantir acquire(long leaseDurationInMillis) {
        // TODO -- you fill in here, using the Specific Notification
        // pattern.  The code path is very simple if there are no
        // waiters in the queue and a Palantir is available.
        // Otherwise, use an instance of the WaitNode to get a
        // Palantir when it becomes available.
    }		
        
    /**
     * Gets an available Palantir from the HashMap and returns it.
     * Must be called with the PalantiriManager mLock held since this
     * method itself is not synchronized.
     */
    private Palantir getPalantir(long leaseDurationInMillis) {
        // Iterate through every entry in the HashMap.
        for (Map.Entry<Palantir, LeaseState> entry : mPalantiriMap.entrySet()) {
            // Find the first key in the HashMap whose value is
            // mNotInUse (which indicates it's available for use).
            if (entry.getValue() == mNotInUse) {
                // Replace the mNotInUse value with a new
                // LeaseState object.
                entry.setValue(new LeaseState(leaseDurationInMillis));

                // Return the palantir.
                return entry.getKey();
            }
        }

        // This shouldn't happen, but we need this here to make the
        // compiler happy.
        return null; 
    }

    /**
     * Puts the designated @code palantir back into the
     * PalantiriManager so other BeingThreads can use it.
     */
    public void release(final Palantir palantir) {
        // TODO -- you fill in here.
    }

    /**
     * Put the @a palantir (which must not be null) back into the
     * HashMap.  Must be called with the PalantiriManager mLock held
     * since this method itself is not synchronized.
     */
    private void putPalantir(Palantir palantir) {
        // Put the mNotInUse value back into HashMap for the palantir
        // key, which also atomically returns the LeaseState
        // associated with the palantir.
        try (LeaseState values = mPalantiriMap.put(palantir,
                                                   mNotInUse)) {
            // The close() method of LeaseState clean ups the values
            // associated with palantiri in the HashMap.
        } 
    }

    /**
     * Returns the amount of time (in milliseconds) remaining on the
     * lease held on the @a palantir.
     */
    public long remainingTime(Palantir palantir) {
        // TODO -- you fill in here.  Grad student will use the
        // ReadWriteLock, whereas undergrads can Lock.
    }

    /**
     * When all available Palantiri are in use any subsequent threads
     * that want to acquire a Palantir use instances of this class to
     * wait for the next Palantir to become available.
     */
    private class WaitNode {
        /**
         * Keep track of whether the WaitNode has already been
         * released.
         */
        private boolean mReleased;

        /**
         * Condition used to wait until a Palantir is available.
         */
        private final Condition mCondition;

        /**
         * Constructor initializes the fields.
         */
        public WaitNode() {
            mReleased = false;
            mCondition = 
                // TODO -- you fill in here, initializing the
                // Condition with PalantiriManager's lock.
        }

        /** 
         * Waits for a Palantir to become available and returns it.
         * The PalantiriManager's mLock must be held when this method
         * is called since this method is not synchronized.
         */
        Palantir getPalantirWhenAvailable(long leaseDurationInMillis) {
            // Keep looping until wakeupWaitingThread() is called.
            while (!mReleased) 
                try {
                    // Add this object to the FIFO queue of threads
                    // waiting for a turn.
                    mFairnessChecker.addCurrentThread();
                    
                    // Wait for a Palantir to become available.
                    // @@ TODO -- you fill in here.
                    mCondition.await();
                } catch (InterruptedException ie) {
                    // Ignore interrupts, but remove the Thread id
                    // from queue so it won't appear multiple times.
                    mFairnessChecker.removeCurrentThread();
                }

            // Check to ensure the thread was signaled in FIFO order.
            if (!mFairnessChecker.isFifoOrder()) {
                // If signaling wasn't fair/FIFO invoke the
                // callback to inform the user.
                if (mUnfairnessCallback != null)
                    mUnfairnessCallback.run();
                    
                // Reset the FairnessChecker.
                mFairnessChecker.reset();
            }

            // Call getPalantir() return the next available palantir.
            return getPalantir(leaseDurationInMillis);
        }

        /**
         * Signal a waiting thread when a Palantir has been released.
         * The PalantiriManager's mLock must be held when this method
         * is called since this method is not synchronized.
         */
        void wakeupWaitingThread() {
            // Indicate the node is released.
            mReleased = true;

            // Signal next waiting thread in queue to proceed.
            // @@ TODO -- you fill in here.
        }
    }

    /*
     * The following method is just intended for use by the regression
     * tests, not by applications.
     */

    /**
     * Returns the number of available Palantiri.
     */
    public long availablePalantiri() {
        // TODO -- you fill in here (grad students must use a
        // ReadWriteLock, undergraduates can use a Lock).
    }
}
