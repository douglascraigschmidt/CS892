package edu.vandy.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.StampedLock;

/**
 * Defines a mechanism that mediates concurrent access to a fixed
 * number of available Palantiri.  This class uses a "fair" Semaphore,
 * a HashMap, and synchronized statements to mediate concurrent access
 * to the Palantiri.  This class implements a variant of the "Pooling"
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
    // TODO -- you fill in here.  Ugrads use the Java Semaphore,
    // whereas grads use their FairSemaphore implementation.

    /**
     * A map that associates the @a Palantiri key to the @a boolean
     * values that keep track of whether the key is available.
     */
    private final HashMap<Palantir, Boolean> mPalantiriMap;

    /**
     * Java synchronizer that protects the Palantiri state.
     */
    // TODO -- you fill in here.  Ugrads use a StampedLock, whereas
    // grads can use whatever synchronizer they want.

    /**
     * Constructor creates a PalantiriManager for the List of @a
     * palantiri passed as a parameter and initializes the fields.
     */
    public PalantiriManager(List<Palantir> palantiri) {
        // Create a new HashMap, iterate through the List of Palantiri
        // and initialize each key in the HashMap with "true" to
        // indicate it's available, and initialize the Semaphore to
        // use a "fair" implementation that mediates concurrent access
        // to the given Palantiri.
        // TODO -- you fill in here.
    }

    /**
     * Get a Palantir from the PalantiriManager, blocking until one is
     * available.
     */
    public Palantir acquire() {
        // Acquire the Semaphore uninterruptibly and then iterate
        // through the HashMap in a thread-safe manner to find the
        // first key in the HashMap whose value is "true" (which
        // indicates it's available for use).  Replace the value of
        // this key with "false" to indicate the Palantir isn't
        // available and then return that palantir to the client. 

        // TODO -- you fill in here.  Ugrads should use an upgrading
        // write lock implementation to demonstrate StampedLock's
        // support for upgrading a readLock to a writeLock.  Grads can
        // use whatever synchronizer they choose.

        // This shouldn't happen, but we need this here to make the
        // compiler happy.
        return null; 
    }

    /**
     * Returns the designated @code palantir to the PalantiriManager
     * so that it's available for other Threads to use.
     */
    public void release(final Palantir palantir) {
        // Put the "true" value back into HashMap for the palantir key
        // in a thread-safe manner and release the Semaphore if all
        // works properly.

        // TODO -- you fill in here.  Ugrads should use a StampedLock
        // writeLock.  Grads can use whatever synchronizer they
        // choose.
    }

    /*
     * The following method is just intended for use by the regression
     * tests, not by applications.
     */

    /**
     * Returns the number of available permits on the semaphore.
     */
    public int availablePermits() {
        return mAvailablePalantiri.availablePermits();
    }
}
