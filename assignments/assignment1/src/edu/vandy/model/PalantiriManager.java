package edu.vandy.model;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.vandy.presenter.BeingThread;
import edu.vandy.utils.ISemaphore;

/**
 * Defines a mechanism that mediates concurrent access to a (smaller)
 * fixed number of available Palantiri.  Each Palantiri can be leased
 * for a designated amount of time.  If the lease expires before the
 * thread that acquired it has released it, this thread will be sent
 * an interrupt request that will cause it to receive the
 * InterruptedException so it can release the lease.
 *
 * Internally, this class uses a "fair" Semaphore, a HashMap, and
 * synchronized statements to mediate concurrent access to the
 * Palantiri.  This class implements a variant of the "Pooling"
 * pattern (kircher-schwanninger.de/michael/publications/Pooling.pdf).
 */
public class PalantiriManager {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        PalantiriManager.class.getSimpleName();

    /**
     * A counting Semaphore that limits concurrent access to the fixed
     * number of available palantiri managed by the PalantiriManager.
     */
    private final ISemaphore mAvailablePalantiri;

    /**
     * A map that associates the @a Palantiri key to the @a LeaseState
     * values.
     */
    protected final HashMap<Palantir, LeaseState> mPalantiriMap;

    /**
     * @class LeaseState
     *
     * @brief This class contains the values associated with a
     *        @code Palantiri key in the Java HashMap that are used to
     *        implement the timed lease semantics.
     */
    protected static class LeaseState 
              implements AutoCloseable {
        /**
         * Create a ScheduledThreadPoolExecutor with size 1 to enforce
         * the lease expiration semantics.
         */
        private static ScheduledExecutorService sScheduledExecutorService 
            = Executors.newScheduledThreadPool(1);

        /**
         * Stores how long a Thread can hold a lease on a palantiri.
         */
        private final long mLeaseDuration;

        /**
         * Keep track of the @a ScheduledFuture returned from the @a
         * ScheduledExecutorService.
         */
        private ScheduledFuture<?> mFuture;

        /**
         * Thread associated with this LeaseState object.
         */
        private BeingThread mLeasorThread;

        /**
         * Default constructor.
         */
        public LeaseState() {
            mLeaseDuration = 0;
            mLeasorThread = null;
            mFuture = null;
        }

        /**
         * Obtain a lease on @a Palantiri in the HashMap.
         *
         * @param leaseDurationInMillis 
         *            The (relative) duration of time the lease is held.
         */
        public LeaseState(long leaseDurationInMillis) {
            // The (absolute) time over which this Thread leases the
            // palantir.
            mLeaseDuration =
                leaseDurationInMillis + System.currentTimeMillis();

            // Get the current Thread and set it as the Leasor of this
            // palantir.
            mLeasorThread = (BeingThread) Thread.currentThread();

            // This Runnable command is dispatched by the
            // ScheduledExecutorService after the lease expires to
            // interrupt the Thread that's leasing the palantir.
            final Runnable expirationRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mLeasorThread != null
                            && mFuture != null
                            && !mFuture.isCancelled()) 
                            mLeasorThread.leaseExpired();
                    }
                };

            // Schedule the Runnable command to fire at the absolute
            // mLeaseDurationInMillis time in the future.
            mFuture =
                sScheduledExecutorService.schedule
                    (expirationRunnable,
                     leaseDurationInMillis,
                     TimeUnit.MILLISECONDS);
        }

        /**
         * Release the lease by cleaning up the values associated with
         * a @a Palantiri key in the HashMap.
         */
        @Override
        public void close() {
            mLeasorThread = null;
            
            if (mFuture != null) {
                // Cancel the timer associated with the
                // expirationRunnable command.
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
    }

    /**
     * A special value that's used to indicate when a palantir is not
     * in use.
     */
    protected final LeaseState mNotInUse = new LeaseState();

    /**
     * Constructor creates a PalantiriManager for the List of @a
     * palantiri passed as a parameter.
     */
    public PalantiriManager(List<Palantir> palantiri,
                            ISemaphore semaphore) {
        // @@ TODO -- you fill in here.
    }

    /**
     * Get a Palantir from the PalantiriManager, blocking until one is
     * available.  The @code leaseDurationInMillis parameter specifies
     * the maximum amount of time the lease for the palantir will be
     * valid.  When this time is expires the Thread that acquired the
     * lease will be sent an interrupt request.
     */
    public Palantir acquire(long leaseDurationInMillis) {
        // @@ TODO -- you fill in here.
    }

    /**
     * Returns the designated @code palantir to the PalantiriManager
     * so that it's available for other Threads to use.
     */
    public void release(final Palantir palantir) {
        // @@ TODO -- you fill in here.
    }

    /**
     * Returns the amount of time (in milliseconds) remaining on the
     * lease held on the @a palantir.
     */
    public long remainingTime(Palantir palantir) {
        // @@ TODO -- you fill in here.
    }
}
