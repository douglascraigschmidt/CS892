package edu.vandy.model;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Graduate students must implement this class to define a counting
 * semaphore with "fair" semantics using Java a ReentrantLock and a
 * ConditionObject.
 */
public class FairSemaphore {
    /**
     * Define a count of the number of available permits.
     */
    // TODO - you fill in here.  Make sure that this data member will
    // ensure its values aren't cached by multiple Threads..
    
    /**
     * Define a ReentrantLock to protect critical sections.
     */
    // TODO - you fill in here

    /**
     * Define a Condition that waits while the number of permits is 0.
     */
    // TODO - you fill in here

    /**
     * Constructor initialize the data members.  
     */
    public FairSemaphore (int permits) {
        // TODO -- you fill in here. Make sure the ReentrantLock has
        // "fair" semantics.
    }

    /**
     * Acquire one permit from the semaphore in a manner that can be
     * interrupted.
     */
    public void acquire() throws InterruptedException {
        // TODO -- you fill in here.
    }

    /**
     * Acquire one permit from the semaphore in a manner that cannot
     * be interrupted.
     */
    public void acquireUninterruptibly() {
        // TODO -- you fill in here.
    }

    /**
     * Return one permit to the semaphore.
     */
    public void release() {
        // TODO -- you fill in here.
    }

    /**
     * Returns the current number of permits.
     */
    public int availablePermits() {
        // TODO -- you fill in here.  
    }
}
