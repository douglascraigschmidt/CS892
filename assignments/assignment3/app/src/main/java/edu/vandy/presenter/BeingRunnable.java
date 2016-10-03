package edu.vandy.presenter;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;

import edu.vandy.common.Utils;
import edu.vandy.model.Palantir;
import edu.vandy.utils.Options;

/**
 * This class implements the gazing logic of a Thread.  Since Beings
 * are identified by their indices in the list they must be supplied
 * with an index when created.
 */
public class BeingRunnable
       implements Runnable {
    /**
     * Used for Android debugging.
     */
    private final static String TAG = 
        BeingRunnable.class.getName();

    /**
     * Reference to the enclosing Presenter.
     */
    private final PalantiriPresenter mPresenter;

    /**
     * The number of Beings that currently have a Palantir.  Initially
     * set to 0.
     */
    private static AtomicInteger mGazingThreads =
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
     * A boolean flag used to shutdown all BeingRunnables when the
     * simulation is done.
     */
    // TODO -- you fill in here.

    /**
     * A Phaser entry barrier that ensures all background threads
     * start running at the same time.
     */
    private Phaser mEntryBarrier;

    /**
     * A CountDownLatch exit barrier that ensures the waiter thread
     * doesn't finish until all the BeingAsyncTasks finish.
     */
    private CountDownLatch mExitBarrier;

    /**
     * Constructor initializes the field.
     */
    BeingRunnable(PalantiriPresenter presenter,
                  Phaser entryBarrier,
                  CountDownLatch exitBarrier) {
        // Initialize the fields.
        mEntryBarrier = entryBarrier;
        mExitBarrier = exitBarrier;
        mPresenter = presenter;

        // TODO -- you fill in here.
        // Register ourselves with the Phaser so we're included in the
        // set of registered parties.

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
     * Shutdown the BeingRunnables.
     */
    public static void shutdown() {
        // TODO -- you fill in here.
    }

    /**
     * Called to reset any static members when before a new set of
     * being runnables is about to be run.
     */
    public static void reset() {
        // TODO -- you fill in here.
    }

    /**
     * Return true if the BeingRunnables have been shutdown, else
     * false.
     */
    private static boolean isShutdown() {
        // TODO -- you fill in here.
    }

    /**
     * Run the loop that performs the Being gazing logic.
     */
    @Override
        public void run() {
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
            return;
        }

        // Don't start the threads immediately.
        Utils.pauseThread(500);

        // Try to gaze at a palantir the designated number of times.
        gazeIntoPalantir(Options.instance().gazingIterations(),
                         getBeingId(),
                         Thread.currentThread().getName());
    }

    /**
     * Perform the Being gazing logic.
     */
    private void gazeIntoPalantir(int gazingIterations,
                                  int beingId,
                                  String threadName) {
        // Keep track of how many iterations we complete.
        int completedIterations = 0;

        // Iterate for the designated number of times each Being
        // can gaze into a Palantir.
        for (;
             completedIterations < gazingIterations;
             ++completedIterations) {
            // Return if we've been instructed to stop gazing.
            // TODO -- you fill in here.  Don't use
            // Thread.interrupted() here!
            if (false) {
                Log.d(TAG,
                      "isShutdown() is true for Being "
                      + beingId
                      + " in Thread "
                      + threadName);

                // If we've been instructed to stop gazing, notify the
                // UI and return gracefully.
                mPresenter.mView.get().threadShutdown(beingId);
                break;
            } else {
                Palantir palantir = null;

                try {
                    // Show that we're waiting on the screen.
                    mPresenter.mView.get().markWaiting(beingId);
						
                    // Get a Palantir - this call can block if there are
                    // no available Palantiri.
                    palantir = mPresenter.getModel().acquirePalantir();

                    // Do a sanity check.
                    if (palantir == null) {
                        Log.d(TAG,
                              "Palantir was null in for Being "
                              + beingId
                              + " in thread "
                              + threadName);
                        break;
                    }

                    // Make sure we were supposed to get a Palantir.
                    if (!incrementGazingCountAndCheck(beingId,
                                                      palantir))
                        break;

                    // Mark it as used on the screen.
                    mPresenter.mView.get().markUsed(palantir.getId());

                    // Show that we're gazing on the screen.
                    mPresenter.mView.get().markGazing(beingId);

                    // Gaze at my Palantir for the alloted time.
                    palantir.gaze();

                    // Show that we're no longer gazing.
                    mPresenter.mView.get().markIdle(beingId);
                    Utils.pauseThread(500);

                    // Mark the Palantir as being free.
                    mPresenter.mView.get().markFree(palantir.getId());
                    Utils.pauseThread(500);

                    // Tell the double-checker that we're about to give up
                    // a Palantir.
                    decrementGazingCount();
                } catch (Exception e) {
                    Log.d(TAG,
                          "Exception caught in index "
                          + beingId);

                    // If we're interrupted by an exception, notify the UI and
                    // return gracefully.
                    mPresenter.mView.get().threadShutdown(beingId);
                    break;
                } finally {
                    // Always return Palantir back to PalantiriManager.
                    mPresenter.getModel().releasePalantir(palantir);
                }
            }
        }

        mExitBarrier.countDown();
            
        Log.d(TAG,
              "Being "
              + beingId
              + " has finished "
              + completedIterations
              + " of its "
              + gazingIterations
              + " gazing iterations");
    }

    /**
     * This method is called each time a Thread acquires a Palantir,
     * so it is called concurrently from different threads.  This
     * method increments the number of threads gazing and checks that
     * the number of threads gazing does not exceed the number of
     * Palantiri in the simulation using an AtomicLong object
     * instantiated above (mGazingThreads).  If the number of gazing
     * threads exceeds the number of Palantiri, this thread will call
     * shutdown and return false.
     * 
     * @param beingId
     *         The Id of the current Being.
     * @param palantir
     *         The Palantir that was just acquired.
     *         
     * @return false if the number of gazing threads is greater
     *         than the number of Palantiri, otherwise true.
     */
    private boolean incrementGazingCountAndCheck(int beingId,
                                                 Palantir palantir) {
        // TODO - You fill in here.
    }

    /**
     * This method is called each time a Being is about to release a
     * Palantir.  It should simply decrement the number of gazing
     * threads in mGazingThreads.
     */
    private void decrementGazingCount() {
        // TODO - You fill in here.
    }
}
