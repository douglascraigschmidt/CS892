package edu.vandy.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.vandy.R;
import edu.vandy.presenter.PalantiriPresenter;
import edu.vandy.utils.Options;
import edu.vandy.utils.UiUtils;
import edu.vandy.view.DotArrayAdapter.DotColor;

/**
 * This activity runs the palantiri simulation.  The number of Beings,
 * Palantiri, and gazing iterations are "extra" parameters to the
 * intent used to start this activity.  They are used to initialize an
 * Options singleton and are available to Presenter layer when the
 * PalantiriPresenter's start() hook method is called.
 * 
 * This activity plays the "View" role in the Model-View-Presenter
 * (MVP) pattern.  It handles runtime configuration changes via
 * onRetainNonConfigurationInstance() and
 * getLastNonConfigurationInstance() methods.  These methods return a
 * PalantiriPresenter object, which plays the "Presenter" role in the
 * MVP pattern.
 */
public class GazingSimulationActivity
        extends LifecycleLoggingActivity {
    /**
     * Name of the intent action that starts this activity.
     */
    private static String ACTION_GAZING_SIMULATION =
            "android.intent.action.GAZING_SIMULATION";

    /**
     * The list views that display the Palantiri and Beings.
     */
    private ListView mPalantirListView;
    private ListView mBeingListView;

    /**
     * The array adapters that will convert booleans to various
     * colored dots.
     */
    private ArrayAdapter<DotColor> mPalantiriAdapter;
    private ArrayAdapter<DotColor> mBeingAdapter;

    /**
     * The Start/Stop floating action button.
     */
    private FloatingActionButton mStartOrStopFab;

    /**
     * Reference to the PalantiriPresenter that implements the entry
     * point into the Presenter layer in the MVP pattern.
     */
    private PalantiriPresenter mPalantiriPresenter;

    /**
     * Default parameters.
     */
    private final static String DEFAULT_BEINGS = "6";
    private final static String DEFAULT_PALANTIRI = "4";
    private final static String DEFAULT_GAZING_ITERATIONS = "5";

    /**
     * Extra symbolic constant names.
     */
    public final static String BEINGS = "BEINGS";
    public final static String PALANTIRI = "PALANTIRI";
    public final static String GAZING_ITERATIONS = "GAZING_ITERATIONS";

    /**
     * Factory method that returns an intent that starts the
     * GazingSimulationActivity.
     */
    public static Intent makeIntent(String beings,
                                    String palantiri,
                                    String gazingIterations) {
        // Handle default cases.
        if (beings.isEmpty())
            beings = DEFAULT_BEINGS;
        if (palantiri.isEmpty())
            palantiri = DEFAULT_PALANTIRI;
        if (gazingIterations.isEmpty())
            gazingIterations = DEFAULT_GAZING_ITERATIONS;

        // Return the intent with the extras added.
        return new Intent(ACTION_GAZING_SIMULATION)
                .putExtra(BEINGS, beings)
                .putExtra(PALANTIRI, palantiri)
                .putExtra(GAZING_ITERATIONS, gazingIterations);
    }

    /**
     * This lifecycle hook method is automatically called when the
     * activity is created to perform initialization operations.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call up to initialize the superclass.
        super.onCreate(savedInstanceState);

        // Set the default content view.
        setContentView(R.layout.gazing_simulation_activity);

        // Set mPalantiriPresenter to the object that was stored by
        // onRetainNonConfigurationInstance().
        setPresenter((PalantiriPresenter) getLastCustomNonConfigurationInstance());

        // Check to see if this is the first time in.
        if (getPresenter() == null) 
            // This is the first time in, so create a new
            // PalantiriPresenter.
            setPresenter(new PalantiriPresenter(this));
        else
            // Reinitialize the PalantiriPresenter with the new
            // instance of this activity.
        getPresenter().onConfigurationChange(this);

        // Initialize the Views.
        initializeViews();

        // Check to see if the Presenter layer is still running the
        // simulation.
        if (getPresenter().isRunning()) {
            // Note that we must display toast after changing button
            // text to ensure the Espresso tests get are able to
            // detect and test for the correct sequence of events.
            UiUtils.showToast(this,
                              R.string.toast_simulation_resume);
        } else if (!getPresenter().configurationChangeOccurred())
            // Run the simulation if this is the first time in.
            runSimulation();
    }

    /**
     * This hook method is called by Android as part of destroying an
     * activity due to a configuration change, when it is known that a
     * new instance will immediately be created for the new
     * configuration.
     */
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        // Returns mPalantiriPresenter so that it will be saved across
        // runtime configuration changes.
        return getPresenter();
    }

    /**
     * Initialize the views from the gazing_simulation_activity.xml
     * file and store them for later use.
     */
    private void initializeViews() {
        // Initialize various views.
        mPalantirListView =
                (ListView) findViewById(R.id.list_view_palantiri);
        mBeingListView =
                (ListView) findViewById(R.id.list_view_beings);
        mStartOrStopFab =
                (FloatingActionButton) findViewById(R.id.start_or_stop_fab);

        // These adapters convert the colors of the Palantiri and
        // Beings into UI elements.
        mPalantiriAdapter =
                new DotArrayAdapter(this,
                                    R.layout.palantir_list_element,
                                    getPresenter().getPalantiriColors());
        mBeingAdapter =
                new DotArrayAdapter(this,
                                    R.layout.being_list_element,
                                    getPresenter().getBeingsColors());

        // These ListViews get the UI elements from our adapters.
        mPalantirListView.setAdapter(mPalantiriAdapter);
        mBeingListView.setAdapter(mBeingAdapter);

        // Set the simulation button text to reflect the operation
        // that will be performed based on whether or not the
        // simulation is currently running.
        updateSimulationButtonText();
    }

    /**
     * Run the simulation.
     */
    private void runSimulation() {
        // Indicate that the simulation is running.
        getPresenter().setRunning(true);

        // Start the simulation in the Presenter layer.
        getPresenter().start();

        // Change the button to say "Stop Simulation."
        updateSimulationButtonText();
    }

    /**
     * Updates the simulation button text display either a start or
     * stop string based on the simulation running state.
     */
    private void updateSimulationButtonText() {
        mStartOrStopFab.setImageResource(getPresenter().isRunning()
                                         ? R.drawable.ic_media_stop
                                         : android.R.drawable.ic_media_play);
    }

    /**
     * This hook method is called when the user hits the "Start
     * Simulate" button.
     */
    public void startOrStopSimulation(View v) {
        if (getPresenter().isRunning()) {
            // Disable the simulation button until all being threads
            // have been gracefully terminated.
            mStartOrStopFab.setEnabled(false);

            // Request a shutdown.
            getPresenter().shutdown();
        } else 
            // Run a new simulation.
            runSimulation();
    }

    /**
     * Shows the Beings on the screen.  By default, all beings are not
     * gazing (yellow).
     */
    public void showBeings() {
        final Runnable runnable = () -> {
            // Clear the previous list.
            mBeingAdapter.clear();

            // Add the new beings.
            for (int i = 0;
                 i < Options.instance().numberOfBeings();
                 ++i)
                getPresenter().getBeingsColors().add(DotColor.YELLOW);

            // Update the list view.
            mBeingAdapter.notifyDataSetChanged();
        };

        // Use the HaMeR framework to run runnable on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * This method shows the palantiri on the screen.  By default, all
     * palantiri are unused (gray).
     */
    public void showPalantiri() {
        Runnable runnable = () -> {
            // Clear the previous list.
            mPalantiriAdapter.clear();

            // Add the new palantiri.
            for (int i = 0;
                 i < Options.instance().numberOfPalantiri();
                 ++i)
                getPresenter().getPalantiriColors().add(DotColor.GRAY);

            // Update the list view.
            mPalantiriAdapter.notifyDataSetChanged();
        };

        // Use the HaMeR framework to run runnable on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Mark a Palantir at location @a index the given @a color.
     */
    private Runnable markPalantir(final int index,
                              final DotColor color) {
        return () -> {
            // Set the appropriate value
            getPresenter().getPalantiriColors().set(index,
                                                    color);

            // Update the list view.
            mPalantiriAdapter.notifyDataSetChanged();
        };
    }

    /**
     * Mark a Being at location @a index the given @a color.
     */
    private Runnable markBeing(final int index,
                           final DotColor color) {
        return () -> {
            // Set the appropriate value.
            getPresenter().getBeingsColors().set(index,
                                                 color);

            // Update the list view.
            mBeingAdapter.notifyDataSetChanged();
        };
    }

    /**
     * Mark a specific palantir as being available for use (makes the
     * dot green).
     */
    public Runnable markFree(final int index) {
        return markPalantir(index,
                     DotColor.GREEN);
    }

    /**
     * Mark a specific palantir as being currently used (makes the
     * dot red).
     */
    public Runnable markUsed(final int index) {
        return markPalantir(index,
                     DotColor.RED);
    }

    /**
     * Marks a certain being as idle (makes the dot yellow).
     */
    public Runnable markIdle(int index) {
        return markBeing(index,
                  DotColor.YELLOW);
    }

    /**
     * Mark a specific palantir as being interrupted (makes the
     * dot purple).
     */
    public Runnable markInterrupted(final int index) {
        return markBeing(index,
                         DotColor.PURPLE);
    }

    /**
     * Mark a certain being as gazing at a palantir (makes the dot
     * green).
     */
    public Runnable markGazing(final int index) {
        return markBeing(index,
                  DotColor.GREEN);
    }

    /**
     * Marks a certain being as not gazing at a palantir (makes the
     * dot red).
     */
    public Runnable markWaiting(int index) {
        return markBeing(index,
                  DotColor.RED);
    }

    /**
     * Called when all threads are done working.  Pops a toast to
     * notify the user.
     */
    public void done() {
        final Runnable runnable = () -> {
            // Make the Palantiri gray again.
            showPalantiri();

            // Make the Beings yellow again.
            showBeings();

            // Indicate the current simulation is no longer
            // running.
            getPresenter().setRunning(false);

            // Now that all beings are ready to run again, enable the
            // start button so that the simulation can be run again.
            updateSimulationButtonText();
            mStartOrStopFab.setEnabled(true);

            UiUtils.showToast(GazingSimulationActivity.this,
                              R.string.toast_simulation_complete);
        };

        // Use the HaMeR framework to run runnable on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Called when a shutdown occurs.  Pops a toast to notify the
     * user.
     */
    public void shutdownOccurred(final int numberOfSimulationThreads) {
        final Runnable runnable = () ->
                UiUtils.showToast(GazingSimulationActivity.this,
                                  R.string.toast_simulation_stopped,
                                  numberOfSimulationThreads);

        // Use the HaMeR framework to run runnable on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Called when a thread is shutdown.  Pops a toast to notify the
     * user.
     */
    public void threadShutdown(final int index) {
        final Runnable runnable = () -> {
            Log.d(TAG,
                  "Being "
                          + index
                          + " was shutdown");

            // Mark the Being as idle (yellow).
            getPresenter().getBeingsColors().set(index,
                                                 DotColor.YELLOW);

            // Update the list view.
            mBeingAdapter.notifyDataSetChanged();

            // Indicate the current simulation is no longer
            // running.
            getPresenter().setRunning(false);
        };

        // Use the HaMeR framework to run runnable on the UI Thread.
        runOnUiThread(runnable);
    }

    /**
     * Get the PalantiriPresenter.
     */
    private PalantiriPresenter getPresenter() {
        return mPalantiriPresenter;
    }

    /**
     * Set the PalantiriPresenter.
     */
    private void setPresenter(PalantiriPresenter presenter) {
        mPalantiriPresenter = presenter;
    }
}
