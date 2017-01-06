package edu.vandy.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.util.Log;
import edu.vandy.R;
import edu.vandy.model.PalantiriModel;
import edu.vandy.utils.UiUtils;
import edu.vandy.utils.Options;
import edu.vandy.view.DotArrayAdapter.DotColor;
import edu.vandy.view.GazingSimulationActivity;

/**
 * This class manages the Palantiri simulation.  The simulation begins
 * in the start() method, which is called by the UI thread and is
 * provided a reference to the GazingSimulationActivity, which is used
 * to manipulate the UI.  The Options singleton contains the number of
 * beings to simulate and the number of palantiri to simulate.
 * 
 * The simulation should run as follows: the configured number of
 * palantiri should be instantiated and added to the PalantiriManager
 * in the Model layer.  A Java thread should be created for each
 * Being.  Each Being thread should attempt to acquire a palantir a
 * certain number of times.  As this is happening, Being threads
 * should call the appropriate methods in GazingSimulationActivity to
 * demonstrate which palantiri are being used and which Beings
 * currently own a palantir.
 *
 * This class plays the "Presenter" role in the Model-View-Presenter
 * (MVP) pattern by acting upon the Model and the View, i.e., it
 * retrieves data from the Model (e.g., PalantiriModel) and formats it
 * for display in the View (e.g., GazingSimulationActivity).
 */
public class PalantiriPresenter {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        PalantiriPresenter.class.getName();

    /**
     * Keeps track of whether a runtime configuration change ever
     * occurred.
     */
    private boolean mConfigurationChangeOccurred;

    /**
     * The list of Beings (implemented as concurrent Java Threads)
     * that are attempting to acquire Palantiri for gazing.  This
     * field must be public so BeingRunnable can access/update it.
     */
    public List<Thread> mBeingThreads;

    /**
     * Tracks whether a simulation is currently running or not.
     */
    private boolean mRunning = false;

    /**
     * This List keeps track of how many palantiri we have and whether
     * they're in use or not.
     */
    private List<DotColor> mPalantiriColors =
        new ArrayList<>();
	
    /**
     * This List keeps track of how many beings we have and whether
     * they're gazing or not.
     */
    private List<DotColor> mBeingsColors =
        new ArrayList<>();

    /**
     * This WeakReference points back to the GazingSimulationActivity
     * in the View layer.
     */
    WeakReference<GazingSimulationActivity> mView;

    /**
     * This reference points to the PalantiriModel in the Model layer.
     */
    private PalantiriModel mModel;

    /**
     * Constructor called when a new instance of PalantiriPresenter is
     * created.  Initialization code goes here, e.g., storing a
     * WeakReference to the View layer and initializing the Model
     * layer.
     *
     * @param view
     *            A reference to the activity in the View layer.
     */
    public PalantiriPresenter(GazingSimulationActivity view) {
        // Set the WeakReference.
        mView = new WeakReference<>(view);

        // Initialize the model.
        mModel = new PalantiriModel();

        // Get the intent used to start the Activity.
        Intent intent = view.getIntent();

        // Initialize the Options singleton using the extras contained
        // in the intent.
        if (!Options.instance().parseArgs(view,
                                          makeArgv(intent)))
            UiUtils.showToast(view,
                              R.string.toast_incorrect_arguments);

        // A runtime configuration change has not occurred (yet).
        mConfigurationChangeOccurred = false;
    }

    /**
     * Hook method dispatched to reinitialize the PalantiriPresenter
     * object after a runtime configuration change.
     *
     * @param view         
     *          The currently active activity view.
     */
    public void onConfigurationChange(GazingSimulationActivity view) {
        Log.d(TAG,
              "onConfigurationChange() called");

        // Reset the WeakReference.
        mView = new WeakReference<>(view);

        // A runtime configuration change occurred.
        mConfigurationChangeOccurred = true;
    }

    /**
     * Returns true if a configuration change has ever occurred, else
     * false.
     */
    public boolean configurationChangeOccurred() {
        return mConfigurationChangeOccurred;
    }

    /**
     * Factory method that creates an Argv string containing the
     * options.
     */
    private String[] makeArgv(Intent intent) {
        // Create the list of arguments to pass to the Options
        // singleton.
        return new String[]{
                "-b", // Number of Being threads.
                intent.getStringExtra(GazingSimulationActivity.BEINGS),
                "-p", // Number of Palantiri.
                intent.getStringExtra(GazingSimulationActivity.PALANTIRI),
                "-i", // Gazing iterations.
                intent.getStringExtra(GazingSimulationActivity.GAZING_ITERATIONS),
        };
    }

    /**
     * Returns true if the simulation is currently running, else false.
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Sets whether the simulation is currently running or not.
     */
    public void setRunning(boolean running) {
        mRunning = running;
    }

    /**
     * Returns the List of Palantiri and whether they are gazing.
     */
    public List<DotColor> getPalantiriColors() {
        return mPalantiriColors;
    }

    /**
     * Returns the List of Beings and whether they are gazing.
     */
    public List<DotColor> getBeingsColors() {
        return mBeingsColors;
    }

    /**
     * This method is called when the user asks to start the
     * simulation in the UI thread.  It creates the designated number
     * of Palantiri and adds them to the PalantiriManager.  It then
     * creates a thread for each Being and has each Being attempt to
     * acquire a Palantir for gazing, mediated by the
     * PalantiriManager.  These threads call GazingSimluationActivity
     * methods to visualize what is happening for the user.
     **/
    public void start() {
        // Initialize the Palantiri.
        mModel.makePalantiri(Options.instance().numberOfPalantiri());

        // Show the Beings on the UI.
        mView.get().showBeings();

        // Show the palantiri on the UI.
        mView.get().showPalantiri();

        // Create and start a Thread for each Being.
        beginBeingThreads(Options.instance().numberOfBeings());

        // Start a thread to wait for all the Being threads to finish
        // and then inform the View layer that the simulation is done.
        waitForBeingThreads();
    }

    /**
     * Create/start a list of threads that represent the Beings in
     * this simulation.  Each thread is passed a BeingRunnable
     * parameter that performs the Being gazing logic.
     * 
     * @param beingCount
     *            Number of threads to create.
     */
    private void beginBeingThreads(int beingCount) {
        // Generate beingCount number of threads that are stored in a
        // list and then start all the threads in the List.
        // TODO - You fill in here.
    }

    /**
     * Start a thread to wait for all the Being threads to finish and
     * then inform the View layer that the simulation is done.
     */
    private void waitForBeingThreads() {
        // Create and start a Java Thread that waits for all the
        // Threads to finish and then calls mView.get().done() to
        // inform the View layer that the simulation is done.
        // TODO -- you fill in here.
    }

    /**
     * This method is called if an unrecoverable exception occurs or
     * the user explicitly stops the simulation.  It shuts down all
     * threads and notifies the View layer the simulation is done.
     */
    public void shutdown() {
        synchronized(this) {
            // Interrupt all the Threads.
            // TODO -- you fill in here.

            // Inform the user that we're shutting down the
            // simulation.
            mView.get().shutdownOccurred(mBeingThreads.size());
        }
    }

    /**
     * Get a reference to the PalantiriModel.
     */
    public PalantiriModel getModel() {
        return mModel;
    }
}

