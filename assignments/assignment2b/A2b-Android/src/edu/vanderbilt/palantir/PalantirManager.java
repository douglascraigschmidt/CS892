package edu.vanderbilt.palantir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.vanderbilt.semaphore.SimpleSemaphore;

/**
 * @class PalantirManager
 *
 * @brief Uses a "fair" Semaphore to control access to the
 *        available Palantiri.  Implements the "Pooling" pattern
 *        in POSA3.
 */
public class PalantirManager {
    /**
     * Max number of Palantiri available.
     */
    private int mMaxPalantiri = 0;

    /**
     * Simple implementation of a Semaphore that can be configured
     * to use the "fair" policy.
     */
    private SimpleSemaphore mAvailable = null;

    /**
     * The Palantiri this class manages. Each palantir maps to a
     * boolean, which indicates whether or not the palantir is
     * currently in use.
     */
    protected HashMap<Palantir, Boolean> mPalantiri = null;

    /**
     * Create a resource manager for the palantiri passed as a
     * parameter.
     * 
     * Note: when instantiating the Semaphore, use "fair" semantics.
     */
    public PalantirManager(final List<Palantir> palantiri) {
    	// @@ TODO - You fill in here.
    }

    /**
     * Get the next available Palantir from the resource pool,
     * blocking until one is available.
     * 
     * @throws InterruptedException	 
     */
    public Palantir acquirePalantir() throws InterruptedException {
    	// @@ TODO - You fill in here (replacing return null);
    	return null;
    }

    /**
     * Returns the designated @code palantir so that it's
     * available for others to use.
     */
    public void releasePalantir(final Palantir palantir) {
        // @@ TODO - You fill in here.
    }

    /**
     * Get the next available Palantir from the resource pool.  If
     * there are no available Palantir (which shouldn't happen),
     * return null.  This method can be called from multiple threads,
     * so make sure that any shared state is protected from race
     * conditions.
     */
    protected Palantir getNextAvailablePalantir() {
    	// @@ TODO - You fill in here (replacing return null);
    	return null;
    }

    /**
     * Return the @code palantir back to the resource pool.  This
     * method can be called from multiple threads, so make sure that
     * any shared state is protected from race conditions.
     */
    protected boolean markAsUnused(final Palantir palantir) {
    	// @@ TODO - You fill in here (replacing return false);
    	return false;
    }
    
    /**
     * Generate a list of palantiri with random gaze times between 1
     * and 5 milliseconds.
     * 
     * Each palantir's id will be its position in the list.
     */
    public static List<Palantir> generatePalantiri(int size) {
        // @@ TODO - you fill in here. (replacing return null);
    	return null;
    }
}

