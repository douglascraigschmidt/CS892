package edu.vandy.test;

import com.robotium.solo.Solo;

import edu.vandy.view.GazingSimulationActivity;
import junit.framework.Assert;

public class Test_Edited_Values extends Test_for_Palantiri {
    public void testRun() {
        // Click on url text view.
        solo.clickOnView(solo.getView(edu.vandy.R.id.edittext_lease_duration));
        // Ensure that the edit view is clear.
        solo.clearEditText((android.widget.EditText)
                           solo.getView(edu.vandy.R.id.edittext_lease_duration));

        // Add a url into the url text view.
        solo.enterText((android.widget.EditText)
                       solo.getView(edu.vandy.R.id.edittext_lease_duration), "1000");
        solo.clickOnView(solo.getView(edu.vandy.R.id.button_simulation));
		
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.sleep(shortDelay);
	    
        Assert.assertTrue("Test failed: GazingSimulationActivity did not load",
                          solo.waitForActivity(GazingSimulationActivity.class));
	    
        // Rotate the screen back to portrait.
        solo.setActivityOrientation(Solo.PORTRAIT);

        // Give the rotation time to settle.
        solo.sleep(shortDelay);

        // Wait for activity
        Assert.assertTrue("Test failed: GazingSimulationActivity did not",
                          solo.waitForActivity(GazingSimulationActivity.class));
        solo.clickOnView(solo.getView(edu.vandy.R.id.button_simulation));
	    
        // Wait for activity
        Assert.assertTrue("Test failed: Unable to Stop Simulation",
                          solo.waitForText(" simulation threads are being halted"));
		
    }
}
