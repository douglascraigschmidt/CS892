package edu.vanderbilt.a2b_android.ui;

/**
 * @class UIControls
 *
 * @brief This interface will allow you to display Palantirs and
 *	  Beings on the UI without knowing any of the implementation
 *	  details of the UI.  Calls to this interface will be
 *	  converted to Runnable commands and run on the UI thread
 *	  using the HaMmer framework.
 */
public interface UIControls {
    /**
     * Show a certain number of palantiri on the screen.  All
     * palantiri will be marked as unused by default.
     */
    public void showPalantiri(int palantiri);
	
    /**
     * Mark a certain palantir as being used.
     */
    public void markUsed(int index);
	
    /**
     * Mark a certain palantir as free.
     */
    public void markFree(int index);
	
    /**
     * Show a certain number of beings on the screen.  All beings will
     * be marked as not gazing by default.
     */
    public void showBeings(int beings);
	
    /**
     * Mark a certain being as gazing at a palantir.
     */
    public void markGazing(int index);
	
    /**
     * Mark a certain being as waiting for a Palantir palantir.
     */
    public void markWaiting(int index);
	
    /**
     * Mark a certain being as idle (i.e. not gazing or waiting)
     */
    public void markIdle(int index);
    
    /**
     * Tell the user that the simulation is done.
     */
    public void done();
    
    /**
     * Tell the user that an unexpected exception was thrown.
     */
    public void exceptionThrown();
    
    /**
     * Tell the user that a thread was interrupted.
     */
    public void threadInterrupted(int index);
}
