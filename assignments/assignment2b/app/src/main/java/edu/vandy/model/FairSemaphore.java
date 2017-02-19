package edu.vandy.model;

import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implements a fair semaphore using the Specific Notification pattern
 * (www.dre.vanderbilt.edu/~schmidt/PDF/specific-notification.pdf).
 * Undergrads should use the Java built-in monitor object and grads
 * should use ReentrantLock/ConditionObject (which is more
 * complicated).
*/
public class FairSemaphore {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        FairSemaphore.class.getSimpleName();

    /**
     * Define a count of the number of available permits.
     */
    // TODO - you fill in here.  Make sure that this field will ensure
    // its values aren't cached by multiple threads..

    /**
     * Define a class containing a Lock and a Condition for use in the
     * "WaitQueue".  This class is only used in the grad student
     * version - undergrads can ignore it entirely.
     */
    private static class Waiter {
        /**
         * Constructor initializes the fields.
         */
        Waiter() {
            mLock = new ReentrantLock();
            mCondition = mLock.newCondition();
        }

        /**
         * A lock used to synchronize access to the condition below.
         */
        final Lock mLock;
        
        /**
         * A condition that's used to wait in FIFO order.
         */
        final Condition mCondition;
    }

    /**
     * Grad students define a ReentrantLock to protect critical
     * sections.
     */
    // TODO - you fill in here

    /**
     * Define a "WaitQueue" that keeps track of the waiters in a FIFO
     * List to ensure "fair" semantics.  Graduate students should make
     * this a List of "Waiter" objects, whereas undergrads should make
     * this a List of "Object" objects.
     */
    // TODO - you fill in here.

    /**
     * Initialize the fields in the class.
     */
    public FairSemaphore(int availablePermits) {
        // TODO - you fill in here.
    }

    /**
     * Acquire one permit from the semaphore in a manner that cannot
     * be interrupted.
     */
    public void acquireUninterruptibly() {
        // TODO -- you fill in here, using a loop to ignore
        // InterruptedExceptions.
    }

    /**
     * Acquire one permit from the semaphore in a manner that can be
     * interrupted.
     */
    public void acquire() throws InterruptedException {
        // Bail out quickly if we've been interrupted.
        if (Thread.interrupted())
            throw new InterruptedException();

        // Try to get a permit without blocking.
        else if (!tryToGetPermit())
            // Block until a permit is available.
            waitForPermit();
    }            

    /**
     * Handle the case where we can get a permit without blocking.
     *
     * @return Returns true if the permit was obtained, else false.
     */
    private boolean tryToGetPermit() {
        // TODO -- first try the "fast path" where the method doesn't
        // need to block if there are no waiters in the queue or if
        // there are permits available.
    }            

    /**
     * Handle the case where we need to block since there are already
     * waiters in the queue or no permits are available.
     */
    private void waitForPermit() throws InterruptedException {
        // TODO -- implement "fair" semaphore acquire semantics using
        // the Specific Notification pattern.
    }

    /**
     * Return one permit to the semaphore.
     */
    public void release() {
        // TODO -- implement "fair" semaphore release semantics using
        // the Specific Notification pattern.
    }

    /**
     * @return The number of available permits.
     */
    public int availablePermits() {
        return mAvailablePermits;
    }
}

