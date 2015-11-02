package edu.vandy.model.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.os.RemoteException;
import android.util.Log;
import edu.vandy.model.aidl.LeaseCallback;
import edu.vandy.model.aidl.Palantir;
import edu.vandy.utils.ISemaphore;
import edu.vandy.utils.Options;

/**
 * Defines a mechanism that mediates concurrent access to a fixed
 * number of available Palantiri.  Each Palantir can be leased for a
 * designated amount of time.  If the lease expires before the Thread
 * that acquired it has released it, the leaseExired() method will be
 * invoked on a LeaseCallback object, which will cause the
 * implementation of this object in the client to interrupt the thread
 * that's gazing at a Palantir so it can release the lease.
 *
 * PalantiriLeasePool uses a "fair" Semaphore and a ConcurrentHashMap
 * to implement the "Pooling" pattern from the POSA3 book (see
 * www.kircher-schwanninger.de/michael/publications/Pooling.pdf).
 */
public class PalantiriLeasePool {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        PalantiriLeasePool.class.getSimpleName();

    /**
     * A counting Semaphore that limits concurrent access to the
     * number of available Palantiri managed by PalantiriLeasePool.
     */
    private final ISemaphore mAvailablePalantiri;

    /**
     * Associates the @a Resource key to the @a LeaseState values.
     */
    protected final ConcurrentHashMap<Palantir, LeaseState> mPalantiriMap;

    /**
     * Exception thrown if a lease is broken.
     */
    public static class BrokenLeaseException extends Throwable {}

    /**
     * @class LeaseState
     *
     * @brief This class contains the values associated with a
     *        @code Palantir key in the Java HashMap that are used to
     *        implement the timed lease semantics.
     */
    protected static class LeaseState implements AutoCloseable {
        /**
         * Create a ScheduledThreadPoolExecutor with size 1 to enforce
         * the lease expiration semantics.
         */
        private static ScheduledExecutorService sScheduledExecutorService 
            = Executors.newScheduledThreadPool(1);

        /**
         * Stores how long a Thread can hold a lease on a Palantir.
         */
        private final long mLeaseDuration;

        /**
         * Keep track of the @a ScheduledFuture returned from the @a
         * ScheduledExecutorService.
         */
        private ScheduledFuture<?> mFuture;

        /**
         * LeaseCallback associated with this LeaseState object.
         */
        private LeaseCallback mLeaseCallback;

        /**
         * The Palantir that's associated with the LeaseState.
         */
        private Palantir mPalantir;

        /**
         * Default constructor.
         */
        public LeaseState() {
            mLeaseDuration = 0;
            mLeaseCallback = null;
            mFuture = null;
            mPalantir = null;
        }

        /**
         * Obtain a lease on @a Palantir in the HashMap.
         *
         * @param leaseDurationInMillis 
         *            The (relative) duration of time the lease is held.
         */
        public LeaseState(long leaseDurationInMillis,
                          LeaseCallback leaseCallback) {
            // Set the LeaseCallback.
            mLeaseCallback = leaseCallback;

            // A command dispatched by the ScheduledExecutorService
            // after the lease expires to callback to the client
            // informing it to interrupt the thread holding the lease.
            final Runnable leaseCallbackRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,
                          "Thread " 
                          + Thread.currentThread().getId()
                          + " is calling leaseExpired() for palantir "
                          + mPalantir.getId() 
                          + " with generation count "
                          + mPalantir.getGenerationCount());

                    if (mLeaseCallback != null
                        && mFuture != null
                        && !mFuture.isCancelled()) {
                        try {
                            // Inform the Model layer that the lease
                            // has expired on this Palantir.
                            // TODO -- you fill in here.
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            // The (absolute) time over which this Thread leases the
            // Palantir.
            mLeaseDuration =
                leaseDurationInMillis 
                + System.currentTimeMillis();

            // Schedule the Runnable command to fire at the absolute
            // mLeaseDurationInMillis time in the future.
            mFuture =
                sScheduledExecutorService.schedule(leaseCallbackRunnable,
                                                   leaseDurationInMillis,
                                                   TimeUnit.MILLISECONDS);
        }

        /**
         * Release the lease by cleaning up the values associated with
         * a Palantir key in the HashMap.
         */
        @Override
        public void close() {
            mLeaseCallback = null;
            mPalantir = null;
            if (mFuture != null) {
                // Cancel the timer associated with the
                // threadInterruptRunnable command.
                mFuture.cancel(true);
                mFuture = null;
            }
        }

        /**
         * Return the remaining lease duration.
         */
        public long remainingTime() {
            return mLeaseDuration - System.currentTimeMillis();
        }

        /**
         * Set the @a palantir associated with the LeaseState.
         */
        public void setPalantir(Palantir palantir) {
            mPalantir = palantir;
        }
    }

    /**
     * A special value that's used to indicate when a Palantir is not
     * in use.
     */
    protected final LeaseState mNotInUse = new LeaseState();

    /**
     * Constructor creates a LeasePool for the List of @a palantiri
     * passed as a parameter.
     */
    public PalantiriLeasePool(List<Palantir> palantiri) {
        // Create a new ConcurrentHashMap.
        mPalantiriMap = new ConcurrentHashMap<>();

        // Insert all Palantiri into the mPalatiriMap, using mNotInUse
        // to indicate that all Palantir are available for use.
        for (Palantir key : palantiri)
            mPalantiriMap.put(key,
                              mNotInUse);

        // Create a counting Semaphore that uses a "fair"
        // (i.e., FIFO) policy.
        mAvailablePalantiri =
            Options.instance().makeSemaphore(palantiri.size());
    }

    /**
     * Get a Palantir from the PalantiriLeasePool, blocking until one
     * is available.  
     *
     * @param leaseDurationInMillis
     *        Specifies the maximum amount of time the lease for the Palantir
     *        will be valid.  When this time is expires the Thread that
     *        acquired the @a leaseExpired() method of the @a
     *        leaseCallback is dispatched.
     *
     * @param leaseCallback
     *        An object provided by the client whose @a leaseExpired()
     *        method is dispatched when a lease expires.
     */
    public Palantir acquire(long leaseDurationInMillis,
                            LeaseCallback leaseCallback) {
        // Acquire the Semaphore.
        mAvailablePalantiri.acquireUninterruptibly();

        // Create a new LeaseState object.
        final LeaseState leaseState =
            new LeaseState(leaseDurationInMillis,
                           leaseCallback);

        // Iterate through the HashMap.
        for (Palantir palantir : mPalantiriMap.keySet())
            // Find the first key in the ConcurrentHashMap whose value
            // is the mNotInUse (which indicates it's available for
            // use) and atomically replace it with the new LeaseState.
            // TODO -- you fill in here, replacing "false" with the
            // appropriate ConcurrentHashMap cal..
            if (false) {
                // Increment the generation count by one.
                palantir.incrementGenerationCount();

                // Now that we've got a Palantir associate it with the
                // leaseState.
                leaseState.setPalantir(palantir);

                /*
                Log.d(TAG,
                      "Returning Palantir "
                      + palantir.getId()
                      + " in Thread "
                      + Thread.currentThread().getId());
                */

                return palantir;
            }

        // This shouldn't happen, but we need this here to make the
        // compiler happy.
        return null; 
    }

    /**
     * Returns the designated @a palantir to the PalantiriLeasePool so
     * that it's available for other Threads to use.  If @a palantir
     * is null it is ignored.
     */
    public void release(final Palantir palantir) {
        // Sanity check.
        if (palantir == null)
            return;

        // Put the mNotInUse value back into ConcurrentHashMap for the
        // Palantir key, which also atomically returns the LeaseState
        // associated with the Palantir back to mNotInUse to indicate
        // it's available again.
        // TODO -- you fill in here, replacing "null" with the
        // appropriate call to a ConcurrentHashMap method.
        try (LeaseState values = null) {
            if (values != mNotInUse)
                // Release the semaphore if the @a palantir parameter
                // was previously in use.
                mAvailablePalantiri.release();

            // The close() method of LeaseState clean ups the values
            // associated with the palantir in the ConcurrentHashMap.
        } 
    }

    /**
     * Returns the amount of time (in milliseconds) remaining on the
     * lease held on the @a palantir.
     */
    public long remainingTime(Palantir palantir) {
        // Sanity check.
        if (palantir == null)
            return 0;
        else
            // Get the LeaseState associated with the palantir and
            // compute the remaining lease duration.
            return mPalantiriMap.get(palantir).remainingTime();
    }
}
