package edu.vandy.model;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines a mechanism that mediates thread-safe access to a fixed
 * number of available Palantiri using a Java ConcurrentHashMap.  This
 * implementation is intentionally very simple and is not meant to be
 * used in production code.
 */
public class PalantiriManager {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        PalantiriManager.class.getSimpleName();

    /**
     * A ConcurrentHashMap that associates the @a Palantiri key to
     * the @a boolean values that keep track of whether the key is
     * available.
     */
    protected final ConcurrentHashMap<Palantir, Boolean> mPalantiriMap;

    /**
     * Constructor creates a PalantiriManager for the List of @a
     * palantiri passed as a parameter and initializes the fields.
     */
    public PalantiriManager(List<Palantir> palantiri) {
        // Create a new ConcurrentHashMap, iterate through the List of
        // Palantiri and initialize each key in the HashMap with
        // "true" to indicate it's available.

        // Create a new ConcurrentHashMap.
        mPalantiriMap = new ConcurrentHashMap<>();

        // Iterate through the List of Palantiri and initialize each
        // key in the mPalanatiriMap with "true" to indicate it's
        // available.
        palantiri.forEach(palantir
                          -> mPalantiriMap.put(palantir, true));
    }

    /**
     * Try to get the next available Palantir from the resource pool.
     * Returns null immediately (i.e., does not block) if there are no
     * palantir available.
     */
    public Palantir acquire() {
        // Use ConcurrentHashMap.search() to ensure thread-safety.
        return mPalantiriMap.search(1, (palantir, available) -> {
                    // If the palantir is available mark it as being
                    // not available and return the palantir.
                    if (available) {
                        mPalantiriMap.put(palantir, false);
                        return palantir;
                    } else
                        return null;
                });
    }

    /**
     * Returns the designated @code palantir to the PalantiriManager
     * so that it's available for other Threads to use.
     */
    public void release(final Palantir palantir) {
        // Put the "true" value back into HashMap for the palantir key
        // in a thread-safe manner.
        // TODO -- you fill in here.

        // Do a simple sanity check!
        if (palantir != null) {
            // Hold the intrinsic lock for the duration of this call
            // so it operates in a thread-safe manner.
            synchronized (this) {
                // Put the "true" value back into HashMap for the
                // palantir key, which also atomically returns the
                // boolean associated with the palantir.
                mPalantiriMap.put(palantir, true);
            }
        }
    }

    /*
     * The following method is just intended for use by the regression
     * tests, not by applications.
     */

    /**
     * Returns the number of available Palantiri.
     */
    public int availablePermits() {
        // Returns a count of the number of palantiri whose
        // values are "true" (i.e., available for use).
        return mPalantiriMap.reduceValuesToInt(1,
                                               v -> v ? 1 : 0,
                                               0,
                                               (x, y) -> x + y);
    }
}
