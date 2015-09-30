package edu.vandy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.vandy.MVP;

/**
 * Manages access of multiple concurrent Being threads to a limited
 * number of Palantiri.  Internally uses a "fair" Semaphore and a
 * ConcurrentHashMap to control access to the available Palantiri.
 * Implements the "Pooling" pattern in the POSA3 book:
 * www.kircher-schwanninger.de/michael/publications/Pooling.pdf.
 */
public class PalantiriModel
       implements MVP.ProvidedModelOps {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        PalantiriModel.class.getSimpleName();

    /**
     * Controls access of multiple Middle-Earth Beings to a fixed
     * number of available Palantiri.
     */
    private PalantiriManager mPalantiriManager;

    /**
     * Hook method called when a new instance of PalantiriModel is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the Presenter.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    @Override
    public void onCreate(MVP.RequiredPresenterOps presenter) {
        // No-op
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // No-op
    }

    /**
     * Create a PalantiriManager that contains the designated number
     * of Palantir with random gaze times between 1 and 5 milliseconds
     * "Fair" semantics should be used to instantiate the Semaphore.
     *
     * @param palantiriCount
     *        The number of Palantiri to add to the PalantiriManager.
     *
     * @param unfairnessCallback
     *        A Runnable whose run() method is invoked if the
     *        PalantiriManager implementation is not "fair".
     */
    @Override
    public void makePalantiri(int palantiriCount,
                              Runnable unfairnessCallback) {
        // @@ TODO - You fill in here.

    	// Create a list to hold the generated Palantiri.
        final List<Palantir> palantiri =
            new ArrayList<Palantir>();		

        // Create a new Random number generator.
        final Random random = new Random();

        // Create and add each new Palantir into the list.  The id of
        // each Palantir is its position in the list.
        for (int i = 0; i < palantiriCount; ++i) 
            palantiri.add(new Palantir(i,
                                       random));

        // Create a PalantiriManager that's used to control concurrent
        // access to the List of Palantiri.
        mPalantiriManager = 
            new PalantiriManager(palantiri,
                                 unfairnessCallback);
    }

    /**
     * Get the next available Palantir from the PalantiriManager,
     * blocking until one is available.
     */
    @Override
    public Palantir acquirePalantir(long leaseDurationInMillis) {
    	// @@ TODO - You fill in here.
        return mPalantiriManager.acquire(leaseDurationInMillis);
    }

    /**
     * Releases the designated @code palantir so it's available for
     * other Beings to use.
     */
    @Override
    public void releasePalantir(final Palantir palantir) {
        // @@ TODO - You fill in here.
        mPalantiriManager.release(palantir);
    }

    /**
     * Returns the amount of time (in milliseconds) remaining on the
     * lease held on the @a palantir.
     */
    @Override
    public long remainingTime(Palantir palantir) {
        return mPalantiriManager.remainingTime(palantir);
    }
}

