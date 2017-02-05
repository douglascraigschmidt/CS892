package edu.vandy.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;

import edu.vandy.R;
import edu.vandy.utils.UiUtils;

/**
 * This activity allows the user to configure simulation options.
 * Once the user presses the "Simulate" button, the configuration data
 * is gathered and the GazingSimulationActivity is launched to run the
 * simulation.
 */
public class PalantiriActivity
        extends LifecycleLoggingActivity {
    /**
     * The edit texts that contain the number of Palantiri, Beings,
     * lease duration, gazing iterations, and type of ISemaphore that
     * we're simulating.
     */
    private EditText mPalantirEditText;
    private EditText mBeingEditText;
    private EditText mGazingIterationsEditText;
	
    /**
     * The start floating action button.
     */
    private FloatingActionButton mStartFab;

    /**
     * This hook method is called when the Activity is instantiated in
     * memory.  We must get references to the UI objects that the
     * framework has created based on our activity_main.xml file and
     * cache them for later use.
     */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Call up to initialize the superclass.
        super.onCreate(savedInstanceState);

        // Set the default content view.
        setContentView(R.layout.palantiri_activity);

        // Initialize the Views.
        initializeViews();
    }

    /**
     * Initialize the Views and GUI widgets.
     */
    private void initializeViews() {
        // Initialize various Views.
        mPalantirEditText =
            (EditText) findViewById(R.id.edittext_number_of_palantiri);
        mBeingEditText =
            (EditText) findViewById(R.id.edittext_number_of_beings);
        mGazingIterationsEditText =
            (EditText) findViewById(R.id.edittext_gazing_iterations);
        mStartFab =
            (FloatingActionButton) findViewById(R.id.start_fab);
    }

    /**
     *	This method is called when the user presses the "start"
     *	floating action button (FAB).
     */ 
    public void startSimulation(View v) {
        // Create an intent to launch the GazingSimulationActivity.
        final Intent intent = 
            GazingSimulationActivity.makeIntent
            (mBeingEditText.getText().toString(),
             mPalantirEditText.getText().toString(),
             mGazingIterationsEditText.getText().toString());

        // Verify that the intent will resolve to an Activity.
        if (intent.resolveActivity(getPackageManager()) != null) 
            // Launch Activity.
            startActivity(intent);
        else
            UiUtils.showToast(this,
                              "Intent did not resolve to an Activity");
    }
}
