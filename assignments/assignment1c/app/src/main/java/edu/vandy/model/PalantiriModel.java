package edu.vandy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class is a proxy that provides access to the PalantiriManager.
 * It plays the "Model" role in the Model-View-Presenter (MVP) pattern
 * by acting upon requests from the Presenter, i.e., it implements the
 * methods that acquire and release Palantiri from the
 * PalantiriManager and returns the Palantiri to the Presenter.
 */
public class PalantiriModel {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        PalantiriModel.class.getName();

    /**
     * Mediates concurrent access of multiple Middle-Earth Beings to a
     * (smaller) fixed number of available Palantiri.
     */
    private PalantiriManager mPalantiriManager;

    /**
     * Create a PalantiriManager that contains the designated number
     * of Palantir with random gaze times between 1 and 5 milliseconds
     *
     * @param palantiriCount
     *            The number of Palantiri to add to the PalantiriManager.
     */
    public void makePalantiri(int palantiriCount) {
    	// Create a list to hold the generated Palantiri.
        final List<Palantir> palantiri =
            new ArrayList<>(palantiriCount);

        // Create a new Random number generator.
        final Random random = new Random();

        // Create and add each new Palantir into the list.  The id of
        // each Palantir is its position in the list.
        for (int i = 0; i < palantiriCount; ++i) 
            palantiri.add(new Palantir(i,
                                       random));

        // Create a PalantiriManager that is used to mediate
        // concurrent access to the List of Palantiri.
        mPalantiriManager = new PalantiriManager(palantiri);
    }

    /**
     * Get the next available Palantir from the resource pool,
     * blocking until one is available.
     */
    public Palantir acquirePalantir() {
        return mPalantiriManager.acquire();
    }

    /**
     * Releases the designated @code palantir so it's available for
     * other Beings to use.  If @a palantir is null it is ignored.
     */
    public void releasePalantir(final Palantir palantir) {
        mPalantiriManager.release(palantir);
    }
}
