package edu.vandy.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.util.Log;
import edu.vandy.R;
import edu.vandy.utils.UiUtils;
import edu.vandy.model.PalantiriModel;
import edu.vandy.utils.Options;
import edu.vandy.view.DotArrayAdapter.DotColor;
import edu.vandy.view.GazingSimulationActivity;

/**
 * This class manages the Palantiri simulation.  The simulation begins
 * in the start() method, which is called by the UI Thread and is
 * provided a reference to GazingSimulationActivity, which is used to
 * manipulate the UI.  The Options singleton contains the number of
 * beings to simulate and the number of palantiri to simulate.
 * 
 * The simulation should run as follows: the correct number of
 * palantiri should be instantiated and added to the LeasePool in the
 * Model layer.  A Java thread should be created for each Being.  Each
 * Being thread should attempt to acquire a palantir a certain number
 * of times (defined via the GAZE_ATTEMPTS constant below).  As this
 * is happening, Being threads should call the appropriate methods in
 * GazingSimulationActivity to demonstrate which palantiri are being
 * used and which Beings currently own a palantir.
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
     * Used to simplify actions performed by the UI, so the
     * application doesn't have to worry about it.
     */
    WeakReference<GazingSimulationActivity> mView;

    /**
     * The list of Beings (implemented as concurrently executing Java
     * Threads) that are attempting to acquire Palantiri for gazing.
     */
    private List<BeingAsyncTask> mBeingsAsyncTasks;

    /**
     * A custom ThreadPoolExecutor that contains a fixed-size pool
     * of Threads corresponding to the number of Beings.
     */
    private ThreadPoolExecutor mThreadPoolExecutor;

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
     * This reference points to the PalantiriModel in the Model layer.
     */
    private PalantiriModel mModel;

    /**
     * A ThreadFactory object that spawns an appropriately named
     * Thread for each Being.
     */
    private ThreadFactory mThreadFactory = 
        // TODO -- you fill in here by replacing "return null" with a
        // ThreadFactory implementation that creates a new Thread each
        // time it's called.
        (runnable) -> {
            return null;
    };

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
                intent.getStringExtra("BEINGS"),
                "-p", // Number of Palantiri.
                intent.getStringExtra("PALANTIRI"),
                "-i", // Gazing iterations.
                intent.getStringExtra("GAZING_ITERATIONS"),
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
     * simulation in the context of the main UI Thread.  It creates
     * the designated number of Palantiri and adds them to the
     * PalantiriManager.  It then creates a Thread for each Being and
     * has each Being attempt to acquire a Palantir for gazing,
     * mediated by the PalantiriManager.  The Threads call methods
     * from the MVP.RequiredViewOps interface to visualize what is
     * happening to the user.
     **/
    public void start() {
        // Initialize the Palantiri.
        getModel().makePalantiri(Options.instance().numberOfPalantiri());

        // Show the Beings on the UI.
        mView.get().showBeings();

        // Show the palantiri on the UI.
        mView.get().showPalantiri();

        // Create and execute an AsyncBeingAsyncTask for each Being.
        beginBeingTasksGazing(Options.instance().numberOfBeings());

        // Spawn a thread that waits for all threads in the
        // ThreadPoolExecutor to terminate.
        awaitTerminationOfThreadPoolExecutor();
    }

    /**
     * Create a List of Threads that will be used to represent the
     * Beings in this simulation.  Each Thread is passed a
     * BeingRunnable parameter that takes the index of the Being in
     * the list as a parameter.
     * 
     * @param beingCount
     *            Number of Being Threads to create.
     */
    private void beginBeingTasksGazing(int beingCount) {
        // Create a new ArrayList that will store beingCount number of
        // BeingAsyncTasks and then execute all these tasks in a
        // custom ThreadPoolExecutor that contains (1) a fixed-size
        // pool of Threads corresponding to the number of Beings, (2)
        // a LinkedBlockingQueue, and (3) the ThreadFactory instance.
        // TODO - You fill in here.
    }

    /**
     * Spawn a thread that waits for all threads in the
     * ThreadPoolExecutor to terminate.
     */
    private void awaitTerminationOfThreadPoolExecutor() {
        // Create/start a waiter thread that waits for all the threads
        // in the ThreadPoolExecutor to terminate.  After they're all
        // finished then tell the UI thread this simulation is done.
        new Thread(() -> {
            try {
                // Calls the awaitTermination() method of
                // ThreadPoolExecutor to wait for all the threads in
                // the pool to terminate.
                // TODO -- you fill in here.
            } catch (Exception e) {
                Log.d(TAG,
                      "joinBeingAsyncTasks() received exception");
                // If we get interrupted while waiting, stop
                // everything. This call will also cleanly shutdown
                // the executor service.
                shutdown(true);
            } finally {
                // Tell the UI thread this simulation is done.
                mView.get().done();
            }
        }).start();
    }

    /**
     * This method is called if an unrecoverable exception occurs or
     * the user explicitly stops the simulation.  It interrupts all
     * the other threads and notifies the UI.
     */
    public void shutdown(boolean abnormalShutdown) {
        synchronized(this) {
            // Cancel all the BeingAsyncTasks.
            // TODO - you fill in here.

            // We need to shutdown the executor service immediately so
            // it will release all allocated threads in the thread
            // pool.
            // TODO - you fill in here.

            if (abnormalShutdown)
                // Inform the user that we're shutting down the
                // simulation due to an error.
                mView.get().shutdownOccurred(mBeingsAsyncTasks.size());
        }
    }

    /**
     * Get a reference to the PalantiriModel.
     */
    PalantiriModel getModel() {
        return mModel;
    }
}
