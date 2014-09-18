package edu.vanderbilt.a2b_android.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import edu.vanderbilt.a2b_android.R;
import edu.vanderbilt.a2b_android.SimulationManager;
import edu.vanderbilt.a2b_android.ui.DotArrayAdapter.DotColor;

/**
 * @class MainActivity
 *
 * @brief This is the main activity that allows the user to configure
 *        simulation options.  Once the user hits the
 *        "Simulate" button, the configuration data is
 *        gathered and passed to the SimulationManager
 *        via the start() hook method.
 * 		
 *        This activity implements the UIControls interface, which
 *        easily allows the SimulationManager to display data to the
 *        user without knowing any implementation details about the
 *        activity.
 */
public class MainActivity extends Activity implements UIControls {
    /**
     * Whether or not a simulation is currently running or not.
     */
    boolean mRunning = false;

    /**
     * The edit texts that contain the number of palantiri and beings
     * we're simulating.
     */
    EditText mPalantirEditText,
             mBeingEditText;
	
    /**
     * The list views that will display our palantiri and beings.
     */
    ListView mPalantirListView, 
             mBeingListView;
	
    /**
     * The array adapters that will convert our booleans to red and
     * green dots.
     */
    ArrayAdapter<DotColor> mPalantirAdapter, 
                           mBeingAdapter;
    
    /**
     * The Start/Stop Simulation button.
     */
    Button mSimulationButton;
	
    /**
     * This keeps track of how many palantiri we have, and whether
     * they're being used or not.
     */
    List<DotColor> mPalantiri = new ArrayList<DotColor>();
	
    /**
     * This keeps track of how many beings we have, and whether
     * they're gazing or not.
     */
    List<DotColor> mBeings = new ArrayList<DotColor>();
	
    /**
     * The label and dot associated with fairness on the screen.
     */
    TextView mFairLabel;
    ImageView mFairDot;	
	
    /**
     * The queue we're going to use to track if the manager is being
     * fair.
     */
    Queue<Integer> mFairnessQueue = new LinkedList<Integer>();
    
    /**
     *	This hook method is called when the activity is instantiated
     *	in memory. We must get references to the UI objects that the
     *	framework has created based on our activity_main.xml file and
     *	cache them for later use.
     */ 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
        mPalantirEditText =
            (EditText) findViewById(R.id.edittext_number_of_palantiri);
        mBeingEditText =
            (EditText) findViewById(R.id.edittext_number_of_beings);
		
        mPalantirListView =
            (ListView) findViewById(R.id.list_view_palantiri);
        mBeingListView =
            (ListView) findViewById(R.id.list_view_beings);
        
        mSimulationButton =
            (Button) findViewById(R.id.button_simulation);

        // These adapters are used to convert our boolean arrays into
        // UI elements.
        mPalantirAdapter =
            new DotArrayAdapter(this,
                                R.layout.palantir_list_element,
                                mPalantiri);
        mBeingAdapter =
            new DotArrayAdapter(this,
                                R.layout.being_list_element,
                                mBeings);
	
        // These ListViews get the UI elements from our adapters.
        mPalantirListView.setAdapter(mPalantirAdapter);
        mBeingListView.setAdapter(mBeingAdapter);
		
        mFairLabel = (TextView) findViewById(R.id.label_fair);
        mFairDot = (ImageView) findViewById(R.id.fair_dot);
		
    }

    /**
     *	This hook method is called when the user hits the "Simulate"
     *	button.
     */ 
    public void simulationButtonPressed (View v) { 
        int palantiri, beings;
        // Convert the input into integers
        try {
            palantiri =
                Integer.parseInt(mPalantirEditText.getText().toString());
            beings =
                Integer.parseInt(mBeingEditText.getText().toString());
        }
        catch (NumberFormatException e) {
            Toast.makeText(this,
                           "Can't parse integers.",
                           Toast.LENGTH_SHORT).show();
            return;
        }
		
        if (palantiri < 1 || palantiri > 4) {
            Toast.makeText(this,
                           "Please enter a number between 1 and 4 for # of Palantiri",
                           Toast.LENGTH_SHORT).show();
            return;
        }
		
        if (beings < 1 || beings > 6) {
            Toast.makeText(this,
                           "Please enter a number between 1 and 6 for # of Beings",
                           Toast.LENGTH_SHORT).show();
            return;
        }
		
        if (!mRunning) {
            mRunning = true;
			
            // Show the fairness dot
            setFairGreen();
            mFairLabel.setVisibility(TextView.VISIBLE);
            mFairDot.setVisibility(TextView.VISIBLE);
			
            // Clear out the fairness queue
            mFairnessQueue.clear();
			
            // Change the button to say "Stop Simulation"
            mSimulationButton.setText("Stop Simulation");
            
            try {
                SimulationManager.start(palantiri,
                                        beings,
                                        this);
            }
            catch (InterruptedException e) {
                Toast.makeText(this,
                               "The simulation was interrupted for some reason",
                               Toast.LENGTH_LONG).show();
            }
        }
        else {
            // Stop the simulation.
            SimulationManager.panic();        	
        }
    }
    
    /**
     * If we're paused, we need to stop everything.
     */
    @Override
        public void onPause() {
    	SimulationManager.panic();
    }
   
    /**
     *	This simply sets the fairness dot to be green. 
     */ 
    private void setFairGreen() {
        mFairDot.setImageDrawable(getResources().getDrawable(R.drawable.green_dot));
    }
	
    /**
     *	This helper method simply set the fairness dot to be red. 
     */ 
    private void setFairYellow() {
        mFairDot.setImageDrawable(getResources().getDrawable(R.drawable.yellow_dot));
    }
	
    /**
     * This method shows a certain number of palantiri on the
     * screen. By default, all palantiri are unused (green).
     */ 
    @Override
        public void showPalantiri(final int palantiri) {
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    // Clear the previous list.
                    mPalantirAdapter.clear();
				
                    // Add the new palantiri
                    for (int i = 0; i < palantiri; ++i)
                        mPalantiri.add(DotColor.GREEN);
				
                    // Update the list view.
                    mPalantirAdapter.notifyDataSetChanged();
                }
            });
    }

    /**
     *	Mark a specific palantir as being currently used. (Makes the dot red) 
     */ 
    @Override
        public void markUsed(final int index) {
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    // Set the appropriate value
                    mPalantiri.set(index, DotColor.RED);
				
                    // Update the list view.
                    mPalantirAdapter.notifyDataSetChanged();
                }
            });	
    }

    /**
     * Mark a specific palantir as being available for use. (Makes the
     * dot green).
     */ 
    @Override
    public void markFree(final int index) {
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    // Set the appropriate value
                    mPalantiri.set(index, DotColor.GREEN);
				
                    // Update the list view.
                    mPalantirAdapter.notifyDataSetChanged();
                }
            });	
    }

    /**
     * Shows a certain number of beings on the screen. By default, all
     * beings are not gazing (red).
     */ 
    @Override
    public void showBeings(final int beings) {
        runOnUiThread(new Runnable () {
		@Override
                    public void run() {
                    // Clear the previous list.
                    mBeingAdapter.clear();
			
                    // Add the new beings
                    for (int i = 0; i < beings; ++i) {
                        mBeings.add(DotColor.YELLOW);
                    }
			
                    // Update the list view.
                    mBeingAdapter.notifyDataSetChanged();
		}
            });
		
    }

    /**
     * Mark a certain being as gazing at a palantir (makes the dot green) 
     */ 
    @Override
    public void markGazing(final int index) {
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    // Set the appropriate value
                    mBeings.set(index, DotColor.GREEN);
				
                    // Update the list view.
                    mBeingAdapter.notifyDataSetChanged();
				
                    // Check if they were next in line, but only check
                    // if we've seen at least the mazimum number of
                    // waiters.
                    if (mFairnessQueue.size() >= mBeings.size() - mPalantiri.size() 
                        && mFairnessQueue.peek().intValue() != index) 
                        // We're not fair, set the dot to red
                        setFairYellow();
				
                    // Remove them from the queue either way
                    mFairnessQueue.poll();
                }
            });	
    }

    /**
     * Marks a certain being as not gazing at a palantir (makes the dot red). 
     */ 
    @Override
    public void markWaiting(final int index) {
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    // Set the appropriate value
                    mBeings.set(index, DotColor.RED);
				
                    // Update the list view.
                    mBeingAdapter.notifyDataSetChanged();
				
                    // Add this guy to the line of beings waiting for a turn
                    mFairnessQueue.add(index);
                }
            });
    }
	
    /**
     * Marks a certain being as idle (makes the dot yellow). 
     */ 
    @Override
    public void markIdle(final int index) {
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    // Set the appropriate value
                    mBeings.set(index, DotColor.YELLOW);
				
                    // Update the list view.
                    mBeingAdapter.notifyDataSetChanged();
                }
            });
    }
    
    /**
     * Called when all threads are done working. Throws up a toast to
     * notify the user.
     */ 
    @Override
        public void done() {
		
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    Toast.makeText(MainActivity.this, "Simulation complete.", Toast.LENGTH_LONG).show();
                    mRunning = false;
                    mSimulationButton.setText("Start Simulation");
                }
            });
		
    }

    /**
     * Called when a thread throws an unexpected exception. Throws up
     * a toast to notify the user.
     */ 
    @Override
        public void exceptionThrown() {
		
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    Toast.makeText(MainActivity.this,
                                   "An exception was thrown or you hit stop. Stopping simulation.",
                                   Toast.LENGTH_LONG).show();
                }
            });
		
    }
    
    /**
     * Called when a thread is interrupted. Throws up a toast to
     * notify the user.
     */ 
    @Override
        public void threadInterrupted(final int index) {
		
        runOnUiThread(new Runnable () {
                @Override
                    public void run() {
                    Toast.makeText(MainActivity.this,
                                   "Being " + index + " was interrupted.",
                                   Toast.LENGTH_SHORT).show();
                    mRunning = false;
                }
            });
    }
}
