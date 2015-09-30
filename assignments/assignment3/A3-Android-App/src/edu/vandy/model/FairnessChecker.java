package edu.vandy.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * This class attempts to check whether the PalantiriManager
 * implementation is "fair".  
 */
public class FairnessChecker {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        FairnessChecker.class.getSimpleName();
    
    /**
     * List of the waiting threads, which are stored in FIFO order to
     * see if the PalantiriManager implementation is "fair".
     */
    private List<Long> mFairnessQueue;

    /**
     * Initialize the FairnessChecker
     */
    public FairnessChecker(final int totalEntries) {
        mFairnessQueue = new ArrayList<>(totalEntries);
    }

    /**
     * Add the Id of a thread that's about to wait to acquire a
     * Palantir.  .
     */
    public void addCurrentThread() {
        // Add the current thread id to the fairness queue in a
        // thread-safe manner.
        // TODO -- you fill in here.
    }

    /**
     * Remove the id of a thread, e.g., if an InterruptedException
     * occurs.
     */
    public void removeCurrentThread() {
        // Remove the current thread id from the fairness queue in a
        // thread-safe manner.
        // TODO -- you fill in here.
    }

    /**
     * Returns true if the current thread's id is the same as the
     * first thread id in the list, else false.
     */
    public boolean isFifoOrder() {
        // Check to see if the thread id removed from the front of the
        // queue matches the current thread id in a thread-safe
        // manner.
        // TODO -- you fill in here.
    }

    /**
     * Reset the entries in the FairnessChecker.
     */
    public void reset() {
        mFairnessQueue.clear();
    }
}

