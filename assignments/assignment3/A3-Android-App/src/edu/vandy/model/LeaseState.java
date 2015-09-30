package edu.vandy.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.vandy.presenter.BeingThread;

/**
 * This class contains the values associated with a @code
 * Palantiri key in the Java HashMap that are used to implement
 * the timed lease semantics.
 */
public class LeaseState implements AutoCloseable {
    /**
     * Create a ScheduledThreadPoolExecutor with size 1 to enforce the
     * lease expiration semantics.
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

