package edu.vandy.presenter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import edu.vandy.MVP;
import edu.vandy.common.GenericModel;
import edu.vandy.common.Utils;
import edu.vandy.model.PalantiriModel;
import edu.vandy.utils.Options;
import edu.vandy.view.DotArrayAdapter.DotColor;

/**
 * This class manages the Palantiri simulation.  The simulation begins
 * in the start() method, which is called by the UI Thread and is
 * provided a reference to MVP.RequiredViewOps, which is used to
 * manipulate the UI.  The Options singleton contains the number of
 * beings to simulate and the number of palantiri to simulate.
 * 
 * The simulation should run as follows: the correct number of
 * palantiri should be instantiated and added to the LeasePool in the
 * Model layer.  A Java thread should be created for each Being.  Each
 * Being thread should attempt to acquire a palantir a certain number
 * of times (defined via the GAZE_ATTEMPTS constant below).  As this
 * is happening, Being threads should call the appropriate methods in
 * MVP.RequiredViewOps to demonstrate which palantiri are being used
 * and which Beings currently own a palantir.
 *
 * This class plays the "Presenter" role in the Model-View-Presenter
 * (MVP) pattern by acting upon the Model and the View, i.e., it
 * retrieves data from the Model (e.g., PalantiriModel) and formats it
 * for display in the View (e.g., PalantiriActivity).  It expends the
 * GenericModel superclass and implements MVP.ProvidedPresenterOps and
 * MVP.RequiredModelOps so it can be created/managed by the
 * GenericModel framework.
 */
public class PalantiriPresenter 
       extends GenericModel<MVP.RequiredPresenterOps,
                            MVP.ProvidedModelOps,
                            PalantiriModel>
       implements MVP.ProvidedPresenterOps, 
                  MVP.RequiredPresenterOps {
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
    public WeakReference<MVP.RequiredViewOps> mView;

    /**
     * Defines a pool of Threads that allow Beings to gaze at a fixed
     * number of Palantiri.
     */
    private ExecutorService mExecutor;

    // A CyclicBarrier that's used as an entry barrier to ensure all
    // Threads start running at the same time.
    // TODO -- you fill in here.

    // A CountDownLatch that's used as an exit barrier to wait until
    // all the BeingRunnable tasks have stopped their gazing logic
    // before informing the View layer that the simulation is done.
    // group.
    // TODO -- you fill in here.

    /**
     * A ThreadFactory object that spawns an appropriately named
     * Thread for each Being.
     */
    private ThreadFactory mThreadFactory = 
        new ThreadFactory() {
            /**
             * Give each Being a uniquely numbered name.
             */
            private final AtomicInteger mBeingCount =
                new AtomicInteger(1);

            /**
             * Construct a new Thread.
             */
            public Thread newThread(Runnable runnable) {
                // Create a new BeingThread whose name uniquely
                // identifies each Being.
                // TODO -- you fill in here by replacing "return null"
                // with the appropriate code.
                return null;
            }
        };

    /**
     * The number of Beings that currently have a Palantir.
     */
    public AtomicLong mGazingThreads;

    /**
     * Tracks whether a simulation is currently running or not.
     */
    private boolean mRunning = false;

    /**
     * Tracks the fairness color, which is either green or yellow.
     */
    private Drawable mFairColor;

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
     * Default constructor that's needed by the GenericActivity
     * framework.
     */
    public PalantiriPresenter() {
    }

    /**
     * Hook method called when a new instance of PalantiriPresenter is
     * created.  One time initialization code goes here, e.g., storing
     * a WeakReference to the View layer and initializing the Model
     * layer.
     * 
     * @param view
     *            A reference to the View layer.
     */
    @Override
    public void onCreate(MVP.RequiredViewOps view) {
        // Set the WeakReference.
        mView =
            new WeakReference<>(view);

        // Invoke the special onCreate() method in GenericModel,
        // passing in the PalantiriModel class to instantiate/manage
        // and "this" to provide this MVP.RequiredModelOps instance.
        super.onCreate(PalantiriModel.class,
                       this);

        // Get the intent used to start the Activity.
        final Intent intent = view.getIntent();

        // Initialize the Options singleton using the extras contained
        // in the intent.
        if (Options.instance().parseArgs(view.getActivityContext(), 
                                         makeArgv(intent)) == false)
            Utils.showToast(view.getActivityContext(),
                            "Arguments were incorrect");

        // A runtime configuration change has not yet occurred.
        mConfigurationChangeOccurred = false;
    }

    /**
     * Hook method dispatched by the GenericActivity framework to
     * initialize the PalantiriPresenter object after it's been
     * created.
     *
     * @param view         
     *          The currently active MVP.RequiredViewOps.
     */
    @Override
    public void onConfigurationChange(MVP.RequiredViewOps view) {
        Log.d(TAG,
              "onConfigurationChange() called");

        // Reset the WeakReference.
        mView =
            new WeakReference<>(view);

        // A runtime configuration change occurred.
        mConfigurationChangeOccurred = true;
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Destroy the model.
        getModel().onDestroy(isChangingConfigurations);
    }

    /**
     * Returns true if a configuration change has ever occurred, else
     * false.
     */
    @Override
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
        String argv[] = {
            "-b", // Number of Being threads.
            intent.getStringExtra("BEINGS"),
            "-p", // Number of Palantiri.
            intent.getStringExtra("PALANTIRI"),
            "-l", // Lease duration.
            intent.getStringExtra("LEASE_DURATION"),
            "-i", // Gazing iterations.
            intent.getStringExtra("GAZING_ITERATIONS")
        };
        return argv;
    }

    /**
     * Return true if the simulation is currently running, else false.
     */
    public boolean isRunning() {
        return mRunning;
    }

    /**
     * Set whether the simulation is currently running or not.
     */
    public void setRunning(boolean running) {
        mRunning = running;
    }

    /**
     * Set the fair color, which is either green or yellow.
     */
    public void setFairColor(Drawable fairColor) {
        mFairColor = fairColor;
    }

    /**
     * Get the fair color, which is either green or yellow.
     */
    public Drawable getFairColor() {
        return mFairColor;
    }

    /**
     * Return the List of Palantiri and whether they are gazing.
     */
    public List<DotColor> getPalantiriColors() {
        return mPalantiriColors;
    }

    /**
     * Return the List of Beings and whether they are gazing.
     */
    public List<DotColor> getBeingsColors() {
        return mBeingsColors;
    }

    /**
     * This method is called if an unrecoverable exception occurs or
     * the user explicitly stops the simulation.  It interrupts all
     * the other threads and notifies the UI.
     */
    @Override
    public void shutdown() {
        synchronized(this) {
            // Bail out if we're no longer running.
            if (!isRunning())
                return;
            else
                // Indicate we're no longer running.
                setRunning(false);

            Log.d(TAG, "shutdown() called in "
                  + Thread.currentThread().getId());

            // Inform the user that we're shutting down the
            // simulation.
            mView.get().exceptionThrown(Options.instance().numberOfBeings());

            // Set a flag that will cause all the BeingThreads to
            // shutdown when they are interrupted.
            BeingThread.shutdown();

            // Shutdown the ExecutorService.
            mExecutor.shutdownNow();

            // Tell the UI we're done.
            mView.get().done();
        }
    }

    /**
     * This method is called when the user starts the simulation in
     * the context of the UI Thread.  It requests the Model layer to
     * create the designated number of Palantiri and add them to the
     * PalantiriManager.  It then initializes the designed number of
     * BeingThreads and spawns a Thread that waits for all these
     * BeingThreads to complete gazing.
     */
    @Override
    public void start() {
        // Store the number of Beings requested by the user.
        final int numberOfBeings = 
            Options.instance().numberOfBeings();

        // Create a ThreadPoolExecutor that runs the Being tasks in a
        // fixed-sized thread pool as large as the number of Beings.
        // TODO -- you fill in here.

        // Initialize the entry barrier to ensure that no
        // BeingRunnable tasks start to run until they are all
        // created.
        // TODO -- you fill in here.

        // Initialize the exit barrier that's used to wait until all
        // the BeingRunnable tasks have stopped their gazing logic
        // before informing the View layer that the simulation is
        // done.
        // TODO -- grad students you fill in here.

        // Initialize the PalantiriManager in the Model layer.
        getModel().makePalantiri
            (Options.instance().numberOfPalantiri(),
             // Create a callback object that is invoked to change the
             // fairness indicator if the PalantiriManager is unfair.
             new Runnable() {
                 @Override
                 public void run() {
                     // Set the fairness indicator to yellow.
                     mView.get().setFairYellow();    
                 }});

        // Initialize the count of the number of BeingThreads used to
        // gaze.
        mGazingThreads = new AtomicLong(0);

        // Show the Beings on the UI.
        mView.get().showBeings();

        // Show the Palantiri on the UI.
        mView.get().showPalantiri();

        // Create Runnables for each Beings so they can perform the
        // gazing logic, which attempts to acquire a lease on a
        // Palantir and gaze into it for a random amount of time.
        beginBeingsGazing(numberOfBeings);

        // Start a thread to wait for all the BeingRunnable tasks to
        // finish and then inform the View layer that the simulation
        // is done.
        waitForBeingTasks();
    }

    /**
     * Create and execute a BeingRunnable for all the Beings so they
     * can attempt to acquire a lease on a Palantir and gaze into it.
     */
    private void beginBeingsGazing(int beingCount) {
        // Use the ExecutorService framework to execute all
        // BeingRunnable tasks that gaze into the Palantiri.
        // TODO -- you fill in here.
    }

    /**
     * Start a thread to wait for all the Being tasks to finish and
     * then inform the View layer that the simulation is done.
     */
    private void waitForBeingTasks() {
        // Create a new Runnable that uses the following barrier
        // synchronizers as follows:
        // 
        // * A CyclicBarrier is used as an "entry barrier" to wait
        //   until all the BeingRunnable tasks are started before
        //   executing any of the gazing logic.
        //
        // * Undergrads use the ExecutorService's awaitTermination()
        //   method to wait for all the tasks in the thread pool to
        //   complete.
        // 
        // * Grad students use a CountDownLatch as an "exit barrier"
        //   to wait until all the BeingRunnable tasks have stopped
        //   their gazing logic before informing the View layer that
        //   the simulation is done.
        // 
        // This Runnable runs in its own Thread to avoid the dreaded
        // "application not responding" dialog.
        // 
        // TODO -- you fill in here.
    }

    /**
     * Return the Activity context.
     */
    @Override
    public Context getActivityContext() {
        return mView.get().getActivityContext();
    }
    
    /**
     * Return the Application context.
     */
    @Override
    public Context getApplicationContext() {
        return mView.get().getApplicationContext();
    }
}
