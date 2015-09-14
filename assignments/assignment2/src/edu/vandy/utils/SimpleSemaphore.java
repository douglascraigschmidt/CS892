package edu.vandy.utils;

import java.util.ArrayList;

/**
 * Implements a counting semaphore that uses Java built-in monitor
 * objects to provide "fair" and "non-fair" semantics.  Fair semantics
 * are implemented via the Specific Notification pattern at
 * www.dre.vanderbilt.edu/~schmidt/PDF/specific-notification.pdf.
 */
public class SimpleSemaphore 
       implements ISemaphore {
    /**
     * Keeps track of whether "fair" or "non-fair" semantics are
     * requested.  Plays the role of the Implementator class in the
     * Bridge pattern.
     */
    private SemaphoreBase mSemaphoreBase;

    /**
     * @class SemaphoreBase
     *
     * @brief This class abstracts the behavior of the fair/nonfair
     *        semaphore using the bridge pattern.
     */
    private abstract static class SemaphoreBase {
        /**
         * Define a count of the number of available permits.
         */
        protected volatile int mAvailablePermits;		

        /**
         * Initialize the mAvailablePermits data member.
         */
        SemaphoreBase (int availablePermits) {
            mAvailablePermits = availablePermits;
        }

        /**
         * Return the number of available permits.
         */
        protected int availablePermits() {
            return mAvailablePermits;
        }

        /**
         * Acquire one permit from the semaphore in a manner that can
         * be interrupted.  Must be implemented by a subclass.
         */
        abstract protected void acquire() throws InterruptedException;

        /**
         * Acquire one permit from the semaphore in a manner that
         * cannot be interrupted.  It a template method that calls the
         * acquire() hook method.
         */
        protected void acquireUninterruptibly() {
            // TODO -- you fill in here, using a loop to ignore
            // InterruptedExceptions.
        }

        /**
         * Return one permit to the semaphore.  Must be implemented by
         * a subclass.
         */
        abstract protected void release();
    }

    /** 
     * Initialize the SimpleSemaphore.
     * @param Initialpermits Initial number of permits assigned to the
     *        semaphore, which can be < 0 
     * @parame Fair {@code true} if this lock should use a fair
     *         ordering policy.
     */
    public SimpleSemaphore(int initialPermits,
                           boolean fair) {
        // TODO - you fill in here.
    }
	
    /** 
     * @class NonFairSemaphore
     * 
     * @brief Implements the non-fair Semaphore using a Java
     *        built-in monitor object.
     */
    static final class NonFairSemaphore extends SemaphoreBase {
        /**
         * Initialize the superclass.
         */
        NonFairSemaphore (int availablePermits) {
            // TODO -- you fill in here.
        }

        /**
         * Acquire one permit from the semaphore in a manner that can
         * be interrupted.
         */
        @Override
        protected void acquire() throws InterruptedException {
            // TODO -- you fill in here by waiting while no permit is
            // available and then decrement the number of permits by
            // one.
        }

        /**
         * Return one permit to the semaphore.
         */
        @Override
        protected void release() {
            // TODO -- you fill in here by incrementing the number of
            // permits by one and then notifying some thread waiting
            // on this object.
        }
    }

    /** 
     * @class FairSemaphore
     * 
     * @brief Implements the fair Semaphore using a Java built-in
     *        monitor object and the Specific Notification pattern.
     */
    private static final class FairSemaphore extends SemaphoreBase {
        /**
         * Keep track of the waiters in FIFO order for "fair" semantics.
         */
        // TODO - you fill in here.

        /**
         * Initialize the superclass.
         */
        FairSemaphore (int availablePermits) {
            // TODO - you fill in here.
        }

        /**
         * Acquire one permit from the semaphore in a manner that can
         * be interrupted.
         */
        @Override
        protected void acquire() throws InterruptedException {
            // TODO -- implement "fair" semaphore acquire semantics
            // using the Specific Notification pattern.
        }

        /**
         * Return one permit to the semaphore.
         */
        @Override
        public void release() {
            // TODO -- implement "fair" semaphore release semantics
            // using the Specific Notification pattern.
        }
    }

    /**
     * Acquire one permit from the semaphore in a manner that can be
     * interrupted.  Simply forwards to the concrete implementator
     * object.
     */
    @Override
    public void acquire() throws InterruptedException {
        // TODO - you fill in here.
    }

    /**
     * Acquire one permit from the semaphore in a manner that cannot
     * be interrupted.  Simply forwards to the concrete implementator
     * object.
     */
    @Override
    public void acquireUninterruptibly() {
        // TODO - you fill in here.
    }
	
    /**
     * Return one permit to the semaphore.  Simply forwards to the
     * concrete implementator object.
     */
    @Override
    public void release() {
        // TODO - you fill in here.
    }

    /**
     * Return the number of permits available.  Simply forwards to the
     * concrete implementator object.
     */
    @Override
    public int availablePermits() {
        // TODO - you fill in here.
    }
}
