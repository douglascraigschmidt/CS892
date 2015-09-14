package edu.vandy.utils;

import java.util.concurrent.locks.StampedLock;

// Import the necessary Java synchronization and scheduling classes.

/**
 * Implements a subset of java.util.concurrent.atomic.SimpleAtomicLong
 * using a StampedLock to illustrate how optimistic locking works.
 */
public class SimpleAtomicLong {
    /**
     * The value that's manipulated atomically via the methods.
     */
    // TODO - add the mValue data member here.

    /**
     * Java StampedLock used to serialize access to mValue.
     */
    // TODO - replace the null with the appropriate initialization:
    private final StampedLock mStampedLock = null;

    /**
     * Creates a new SimpleAtomicLong with the given initial value.
     */
    public SimpleAtomicLong(long initialValue) {
        // TODO - you fill in here
    }

    /**
     * @brief Gets the current value
     * 
     * @returns The current value
     */
    public long get() {
        // TODO - you fill in here

        // Do an optimistic read and store the current value.

        // Check to see if a write lock has been acquired.  If not,
        // then return the stored value.  Otherwise, acquire a
        // readLock (which may block) and get the value.  Make sure to
        // release the readLock if it was acquired.
    }

    /**
     * @brief Atomically decrements by one the current value
     *
     * @returns the updated value
     */
    public long decrementAndGet() {
        return addAndGet(-1);
    }

    /**
     * @brief Atomically increments by one the current value
     *
     * @returns the previous value
     */
    public long getAndIncrement() {
        return getAndAdd(1);
    }

    /**
     * @brief Atomically decrements by one the current value
     *
     * @returns the previous value
     */
    public long getAndDecrement() {
        return getAndAdd(-1);
    }

    /**
     * @brief Atomically increments by one the current value
     *
     * @returns the updated value
     */
    public long incrementAndGet() {
        return addAndGet(1);
    }
    
    /**
     * Atomically adds the given value to the current value. ...
     *
     * @param delta the value to add (may be negative)
     * @return the previous value
     */
    private long getAndAdd(long delta) {
        // TODO - you fill in here using a conditional writeLock.

        // Start out with a readLock and then read the current value
        // with only a readLock held.

        // Attempt to upgrade to a writeLock.  If the upgrade to a
        // writeLock worked then we can increment the value in the
        // critical section since it's protected from concurrent
        // access.  Otherwise, fall back to using writeLock, which
        // involves releasing the readLock and acquiring a writeLock
        // (this call may block).  Release the writeLock if it's been
        // acquired.
    }

    /**
     * Atomically adds the given value to the current value.
     *
     * @param delta the value to add (may be negative)
     * @return the updated value
     */
    private long addAndGet(long delta) {
        // TODO - you fill in here

        // Acquire a writeLock, increment mValue, and release the
        // writeLock.
    }
}

