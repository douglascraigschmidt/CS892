package edu.vandy.presenter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;
import android.util.Log;
import edu.vandy.utils.UiUtils;
import edu.vandy.model.Palantir;
import edu.vandy.utils.Options;

/**
 * This class implements a BeingAsyncTask, which performs the Being
 * gazing logic and provides a means for canceling an AsyncTask.
 */
class BeingAsyncTask
       extends AsyncTask<PalantiriPresenter,
                         Runnable,
                         Void> {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        BeingAsyncTask.class.getName();

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
     * The number of Beings that currently have a Palantir.
     * Initialized to 0.
     */
    private static AtomicInteger mGazingTasks =
        new AtomicInteger(0);

    /**
     * The ID of this Being.
     */
    private final int mBeingId;

    /**
     * The number of total Beings from 0 to n - 1;
     */
    private static AtomicInteger mBeingCount =
        new AtomicInteger(0);

    /**
     * Constructor initializes the fields.
     */
    BeingAsyncTask(CyclicBarrier entryBarrier,
                   CountDownLatch exitBarrier) {
        mEntryBarrier = entryBarrier;
        mExitBarrier = exitBarrier;
        mBeingId = mBeingCount.getAndIncrement();
    }

    /**
     * Return the being Id, which is transformed to fit within the
     * total number of Beings.
     */
    private int getBeingId() {
        return mBeingId % Options.instance().numberOfBeings();
    }

    /**
     * Perform the Being gazing logic.
     */
    @Override
    public Void doInBackground(PalantiriPresenter... presenters) {
        try {
            // Don't start gazing until all BeingAsyncTasks are ready to
            // run.
            // TODO -- you fill in here.
        } catch (Exception e) {
            Log.d(TAG,
                  "Exception " 
                  + e 
                  + " caught in Being "
                  + getBeingId());
            return null;
        }
        UiUtils.pauseThread(500);

        // Initialize local variables.
        int i = 0;

        // Try to gaze at a palantir the designated number of times.
        for (;
             i < Options.instance().gazingIterations();
             ++i)
            if (!gazeIntoPalantir(getBeingId(),
                                  Thread.currentThread().getName(),
                                  presenters[0]))
                break;

        Log.d(TAG,
              "Being "
              + getBeingId()
              + " has finished "
              + i 
              + " of its "
              + Options.instance().gazingIterations()
              + " gazing iterations");
        return null;
    }

    /**
     * Perform the Being gazing logic.
     *
     * @return True if gazing completed normally, else false.
     */
    private boolean gazeIntoPalantir(int beingId,
                                     String threadName,
                                     PalantiriPresenter presenter) {
        Palantir palantir = null;

        try {
            // Break out of the loop if the BeingAsyncTask has
            // been cancelled.
            // TODO -- you fill in here by replacing "false" with
            // the appropriate method call to an AsyncTask method.
            if (false) {
                // If we've been instructed to stop gazing, notify
                // the UI and exit gracefully.
                presenter.mView.get().threadShutdown(beingId);
                return false;
            }

            // Show that we're waiting on the screen.
            // TODO -- you fill in here with the appropriate
            // call to an AsyncTask method.

            // Get a Palantir - this call blocks if there are no
            // available Palantiri.
            palantir =
                presenter.getModel().acquirePalantir();

            if (palantir == null) {
                Log.d(TAG,
                      "Received a null palantir in "
                      + threadName
                      + " for Being "
                      + beingId);
                return false;
            }

            // Make sure we were supposed to get a Palantir.
            if (!incrementGazingCountAndCheck(beingId,
                                              palantir,
                                              presenter))
                return false;

            // Mark it as used on the screen.
            // TODO -- you fill in here with the appropriate
            // call to an AsyncTask method.

            // Show that we're gazing on the screen.
            // TODO -- you fill in here with the appropriate
            // call to an AsyncTask method.

            // Gaze at my Palantir for the alloted time.
            palantir.gaze();

            // Show that we're no longer gazing.
            // TODO -- you fill in here with the appropriate
            // call to an AsyncTask method.
            UiUtils.pauseThread(500);

            // Mark the Palantir as being free.
            // TODO -- you fill in here with the appropriate call
            // to an AsyncTask method.
            UiUtils.pauseThread(500);

            // Tell the double-checker that we're about to
            // give up a Palantir.
            decrementGazingCount();
        } catch (Exception e) {
            Log.d(TAG,
                  "Exception " 
                  + e 
                  + " caught in beingId "
                  + beingId);

            // If we're interrupted by an exception, notify the UI and
            // exit gracefully.
            presenter.mView.get().threadShutdown(beingId);
        } finally {
            // Give it back to the PalantiriManager (which ignores
            // null palantiri).
            presenter.getModel().releasePalantir(palantir);
        }
        return true;
    }

    /**
     * Hook method invoked by the AsyncTask framework when
     * doInBackground() calls publishProgress().  
     */
    @Override
    public void onProgressUpdate(Runnable ...runnableCommands) {
        // TODO -- you fill in here with the appropriate call to
        // the runnableCommands that will cause the progress
        // update to be displayed in the UI thread.
    }

    /**
     * Hook method invoked by the AsyncTask framework after
     * doInBackground() completes successfully.
     */
    @Override
    public void onPostExecute(Void v) {
        // Indicate to the waiter thread in PalantiriPresenter that
        // this AsyncTask is done.
        // TODO -- You fill in here.
    }

    /**
     * Hook method invoked by the AsyncTask framework if
     * doInBackground() is cancelled.
     */
    @Override
    public void onCancelled(Void v) {
        // Indicate to the waiter thread in PalantiriPresenter that
        // this AsyncTask is done.
        // TODO -- You fill in here.
    }

    /**
     * This method is called each time a Being acquires a Palantir.
     * Since each Being is a Java Thread, it will be called
     * concurrently from different threads.  This method increments
     * the number of threads gazing and checks that the number of
     * threads gazing does not exceed the number of Palantiri in the
     * simulation using an AtomicInteger object instantiated above
     * (mGazingThreads).  If the number of gazing threads exceeds the
     * number of Palantiri, this thread will call shutdown and return
     * false.
     * 
     * @param beingId
     *         The Id of the current Being.
     * @param palantir
     *         The Palantir that was just acquired.
     * @param presenter
     *         The PalantiriPresenter.
     *         
     * @return false if the number of gazing threads is greater
     *         than the number of Palantiri, otherwise true.
     */
    private boolean incrementGazingCountAndCheck(int beingId,
                                                 Palantir palantir,
                                                 PalantiriPresenter presenter) {
        // TODO -- you fill in here.        
    }

    /**
     * This method is called each time a Being is about to release a
     * Palantir.  It should simply decrement the number of gazing
     * threads in mGazingThreads.
     */
    private void decrementGazingCount() {
        // TODO -- you fill in here.
    }
}
