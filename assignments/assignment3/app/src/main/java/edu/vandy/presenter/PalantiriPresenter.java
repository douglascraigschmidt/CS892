package edu.vandy.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Stream;

import android.content.Intent;
import android.util.Log;
import edu.vandy.R;
import edu.vandy.utils.UiUtils;
import edu.vandy.model.PalantiriModel;
import edu.vandy.utils.Options;
import edu.vandy.view.DotArrayAdapter.DotColor;
import edu.vandy.view.GazingSimulationActivity;

import static java.util.stream.Collectors.toList;

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
     * The ExecutorService contains a cached pool of threads.
     */
    private ExecutorService mExecutor;

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
     * A CyclicBarrier entry barrier that ensures all background
     * threads start running at the same time.
     */
    private CyclicBarrier mEntryBarrier;

    /**
     * A CountDownLatch exit barrier that ensures the waiter thread
     * doesn't finish until all the BeingAsyncTasks finish.
     */
    private CountDownLatch mExitBarrier;

    /**
     * This reference points to the PalantiriModel in the Model layer.
     */
    private PalantiriModel mModel;

    /**
     * A ThreadFactory object that spawns an appropriately named
     * Thread for each Being.
     */
    private ThreadFactory mThreadFactory = 
        // TODO -- you fill in here by replacing "null" with a
        // ThreadFactory implementation that creates a new Thread each
        // time it's called.
        null;
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
     * This method is called if an unrecoverable exception occurs or
     * the user explicitly stops the simulation.  It interrupts all
     * the other threads and notifies the UI.
     */
    public void shutdown() {
        synchronized(this) {
            // Cancel all the BeingAsyncTasks.
            // TODO - you fill in here.

            // Inform the user that we're shutting down the
            // simulation.
            mView.get().shutdownOccurred(mBeingsAsyncTasks.size());
        }
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

        // Initialize an entry barrier that ensures all background
        // threads start running at the same time.
        // TODO -- you fill in here.

        // Initialize an exit barrier to ensure the waiter thread
        // doesn't finish until all the BeingAsyncTasks finish.
        // TODO -- you fill in here.

        // Create and execute an AsyncBeingAsyncTask for each Being.
        beginBeingTasksGazing(Options.instance().numberOfBeings());

        // Spawn a thread that waits for all the Being threads to
        // finish.
        waitForBeingTasksToFinishGazing();
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
        // Generate beingCount BeingAsyncTasks that are stored in a
        // List and then execute all these tasks in a ExecutorService
        // that (1) uses a factory method in the Executors class to
        // create a cached pool of threads and (2) contains the
        // ThreadFactory instance.  Students taking the class for
        // graduate credit should use Java 8 streams to generate the
        // list of BeingAsyncTasks.
        // TODO - You fill in here.
    }

    /**
     * Spawn a thread to wait for all the Being threads to finish.
     */
    private void waitForBeingTasksToFinishGazing() {
        // Create/start a waiter thread that uses mExitBarrier to wait
        // for all the BeingAsyncTasks to finish.  After they are all
        // finished then tell the UI thread this simulation is done.
        new Thread(() -> {
            try {
                // Let all the BeingAsyncTasks start gazing.
                // TODO -- you fill in here.

                // Wait for all BeingAsyncTasks to stop gazing.
                // TODO -- you fill in here.
            } catch (Exception e) {
                Log.d(TAG,
                      "joinBeingAsyncTasks() received exception");
                // If we get interrupted while waiting, stop
                // everything. This call will also cleanly shutdown
                // the executor service.
                shutdown();
            } finally {
                // Shut down the ExecutorService *now* so it releases
                // the threads in the thread pool.
                // TODO -- you fill in here.

                // Tell the UI thread this simulation is done.
                mView.get().done();
            }
        }).start();
    }

    /**
     * Get a reference to the PalantiriModel.
     */
    PalantiriModel getModel() {
        return mModel;
    }
}
