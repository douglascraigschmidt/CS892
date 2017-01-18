package edu.vandy.model;

import java.util.ArrayList;
import java.util.List;

import edu.vandy.utils.UiUtils;

/**
 * Defines a mechanism that mediates thread-safe access to a fixed
 * number of available Palantiri using a Java list that's synchronized
 * by a volatile "spin lock".  This implementation is intentionally
 * very simple and is not meant to be used in production code.
 */
public class PalantiriManager {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        PalantiriManager.class.getSimpleName();

    /**
     * Keeps track of whether a Palantir is available.
     */
    private class PalantirEntry {
        /**
         * The Palantir itself.
         */
        Palantir mPalantir;

        /**
         * A flag that keeps track of whether the Palantir is
         * available.
         */
        boolean mAvailable;

        /**
         * Constructor initializes the field.
         */
        public PalantirEntry(Palantir palantir, boolean available) {
            mPalantir = palantir;
            mAvailable = available;
        }
    }

    /**
     * A volatile flag that's used as a "spin lock" to ensure that
     * threads serialize on a critical section.
     */
    volatile boolean mLocked;

    /**
     * A list that associates the @a Palantiri key to the @a boolean
     * values that keep track of whether the key is available.
     */
    protected final List<PalantirEntry> mPalantiriList;

    /**
     * Constructor creates a PalantiriManager for the List of @a
     * palantiri passed as a parameter and initializes the fields.
     */
    public PalantiriManager(List<Palantir> palantiri) {
        // Create a new List, iterate through the List of Palantiri
        // and initialize each key in the HashList with "true" to
        // indicate it's available.

        // Create a new List.
        mPalantiriList = new ArrayList<>();

        // Iterate through the List of Palantiri and initialize each
        // key in the mPalanatiriList with "true" to indicate it's
        // available.
        palantiri.forEach(palantir
                          -> mPalantiriList.add(new PalantirEntry(palantir,
                                                                  true)));
    }

    /**
     * Try to get the next available Palantir from the resource pool.
     * Returns null immediately (i.e., does not block) if there are no
     * palantir available.
     */
    public Palantir acquire() {
        Palantir palantir = null;

        // Spin until we get the lock.
        while (mLocked == true)
            UiUtils.pauseThread(100);

        // "Acquire" the lock.
        mLocked = true;

        // Find an available palantiri if one exists.
        for (PalantirEntry pe : mPalantiriList) {
            // If the palantir is available mark it as being not
            // available and return the palantir.
            if (pe.mAvailable == true) {
                pe.mAvailable = false;
                palantir = pe.mPalantir;
                break;
            }
        }
            
        // "Release" the lock.
        mLocked = false;
        return palantir;
    }

    /**
     * Returns the designated @code palantir to the PalantiriManager
     * so that it's available for other Threads to use.
     */
    public void release(final Palantir palantir) {
        // Put the "true" value back into List for the palantir key in
        // a thread-safe manner.

        // Do a simple sanity check!
        if (palantir != null) {
            // Spin until we get the lock.
            while (mLocked == true)
                UiUtils.pauseThread(100);

            // "Acquire" the lock.
            mLocked = true;

            // Iterate through all the entries in the list.
            for (PalantirEntry pe : mPalantiriList) {
                // Make the palantir available (i.e., set it to
                // "true") when the palantir matches.
                if (pe.mPalantir == palantir) {
                    pe.mAvailable = true;
                    break;
                }
            }
            
            // "Release" the lock.
            mLocked = false;
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

        // Keeps track of the palantiri that are available.
        int availablePalantiri = 0;

        // Spin until we get the lock.
        while (mLocked == true)
            UiUtils.pauseThread(100);

        // "Acquire" the lock.
        mLocked = true;

        // Find an available palantiri if one exists.
        for (PalantirEntry pe : mPalantiriList) {
            // If the palantir is available then increment the count.
            if (pe.mAvailable == true) 
                ++availablePalantiri;
        }

        // "Release" the lock.
        mLocked = false;

        return availablePalantiri;
    }
}
