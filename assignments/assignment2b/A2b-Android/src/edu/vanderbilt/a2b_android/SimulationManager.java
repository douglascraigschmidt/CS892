package edu.vanderbilt.a2b_android;

import java.util.ArrayList;
import java.util.List;

import edu.vanderbilt.a2b_android.ui.UIControls;
import edu.vanderbilt.palantir.Palantir;
import edu.vanderbilt.palantir.PalantirManager;
import edu.vanderbilt.semaphore.SimpleAtomicLong;

/**
 * @class SimulationManager
 *
 * @brief This class manages the Palantiri acquisition simulation.
 *        The simulation begins in the start() method, which is called
 *        by the UI Thread and is provided three parameters: (1) the
 *        number of beings to simulate, (2) the number of palantiri to
 *        simulate, and (3) a reference to UIControls, which will be
 *        used to manipulate the UI.
 * 
 *        The simulation should run as follows: the correct number of
 *        palantiri should be instantiated and added to the
 *        PalantirManager.  A Java Thread should be created for each
 *        Being.  Each Thread should attempt to acquire a palantir a
 *        certain number of times (defined as a constant below).  As
 *        this is happening, Threads should call the appropriate
 *        methods in UIControls to demonstrate which palantiri are
 *        being used and which Beings currently own a palantir.
 */
public class SimulationManager {
	/**
	 * Used to simplify actions performed by the UI, so the
	 * application doesn't have to worry about it.
	 */
	static UIControls mControls;

	/**
	 * The PalantirManager used to ensure Threads don't
	 * concurrently access the same Palantir.
	 */
	static PalantirManager mPalantirManager;

	/**
	 * The list of Beings (implemented as Java Threads) that are
	 * attempting to acquire Palantiri for gazing.
	 */
	static List<Thread> mBeings;

	/**
	 * The number of times a Being attempts to gaze at a Palantir.
	 */
	static final int GAZE_ATTEMPTS = 5;

	/**
	 * The number of Beings that currently have a Palantir.
	 */
	static SimpleAtomicLong mGazingThreads;

	/**
	 * The number of Palantiri in this simulation.
	 */
	static int mPalantirCount;

	/**
	 * This method is called when the user asks to start the
	 * simulation in the context of the main UI Thread.  It
	 * creates the designated number of Palantiri and adds them to
	 * the PalantirManager.  It then creates a Thread for each
	 * Being and has each Being attempt to acquire a Palantir for
	 * gazing, mediated by the PalantirManager.  The Being Theads
	 * call key methods from the UIControls interface to visualize
	 * what is happening to the user.
	 **/
	public static void start(int palantirCount, 
                                 int beingCount,
                                 UIControls controls) throws InterruptedException {
            // Initialize the data members.
            mControls = controls;

            mPalantirCount = palantirCount;

            mGazingThreads = new SimpleAtomicLong(0);

            // Initialize the palantiri.
            mPalantirManager = initializePalantiri(palantirCount);

            // Show the palantiri on the UI.
            controls.showPalantiri(palantirCount);

            // Create a Thread for each being.
            mBeings = createBeingThreads(beingCount);

            // Show the beings on the UI.
            controls.showBeings(beingCount);

            // Set the exception handler for all the threads.
            for (Thread t : mBeings) {
                t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread thread,
                                                      Throwable ex) {
                            panic();
                        }
                    });
            }

            // Start the threads.
            startThreads(mBeings);

            // Start another thread that waits on the previous threads
            // to finish.
            new Thread(new Runnable() {
            @Override
                public void run() {
                        for (Thread t : mBeings)
                            try {
                                t.join();
                            } catch (InterruptedException e) {
                                // If we get interrupted while
                                // waiting, stop everything.
                                panic();
                            }

                        // Tell the UI we're done.
                        mControls.done();
                    }
		}).start();
	}

	/**
	 * Return an instance of PalantirManager that contains a the
	 * designated number of Palantir. Hint: use the
	 * PalantirManager.generatePalantiri() factory method.
	 * 
	 * @param palantirCount
	 *            The number of Palantir to be added to the PalantirManager.
	 */
	private static PalantirManager initializePalantiri(int palantirCount) {
		// @@ TODO - You fill in here (replacing return null).
            return null;
	}

	/**
	 * Create a List of Threads that will be used to represent the
	 * Beings in this simulation. When instantiating the Threads,
	 * use the makeBeingRunnable() helper method defined below,
	 * which takes the index of the Being in the list as a
	 * parameter.
	 * 
	 * @param The
	 *            Number of Being Threads to create.
	 */
	private static List<Thread> createBeingThreads(int beingCount) {
		// @@ TODO - You fill in here (replacing return null);
            return null;
	}

	/**
	 * This method should start all the threads in the provided
	 * list of threads.
	 */
	private static void startThreads(List<Thread> threads) {
		// @@ TODO - You fill in here.
	}

	/**
	 * This method is called each time a Being acquires a
	 * Palantir. Since each Being is a Thread, it will be called
	 * concurrently from different Threads.  This method
	 * increments the number of Threads gazing and checks that the
	 * number of Threads gazing does not exceed the number of
	 * Palantiri in the simulation using a SimpleAtomicLong object
	 * instantiated above (mGazingThreads).  If the number of
	 * gazing Threads exceeds the number of Palantiri, this Thread
	 * should call panic() and then return false.
	 * 
	 * @return false if the number of gazing threads is greater
	 *         than the number of Palantiri, otherwise true.
	 */
	private static boolean incrementGazingCountAndCheck() {
		// @@ TODO - You fill in here. (Graduate students)
		return true;
	}

	/**
	 * This method is called each time a Being is about to release
	 * a Palantir.  It should simply decrement the number of
	 * gazing threads in mGazingThreads.
	 */
	private static void decrementGazingCount() {
		// @@ TODO - You fill in here. (Graduate students)
	}

	/**
	 * This factory helper method makes a Runnable that does
	 * everything a Being thread should do. However, a Being is
	 * identified by his/her index in the list. Therefore, you
	 * must supply the Being's index when creating it.
	 * 
	 * @param index The index of the Being in the list. This is
	 *            used to properly update the Being's status in
	 *            the UI.
	 * @return
	 */
	private static Runnable makeBeingRunnable(final int index) {
            return new Runnable() {

                @Override
                    public void run() {

                    try {
                        // Try to get a palantir a certain number of times
                        for (int j = 0; j < GAZE_ATTEMPTS; ++j) {

                            // Show that we're waiting on the screen.
                            mControls.markWaiting(index);
						
                            // Get a palantir
                            Palantir mine;
						
                            mine = mPalantirManager.acquirePalantir();

                            // Make sure we were supposed to get a
                            // Palantir.
                            if (!incrementGazingCountAndCheck())
                                return;

                            // Mark it as used on the screen.
                            mControls.markUsed(mine.getId());

                            // Show that we're gazing on the screen
                            mControls.markGazing(index);

                            // Use it for a while
                            mine.gaze();

                            // Mark it as free on the screen.
                            mControls.markFree(mine.getId());

                            // Show that we're no longer gazing.
                            mControls.markIdle(index);

                            // Tell the double-checker that we're
                            // about to give up a Palantir
                            decrementGazingCount();

                            // Give it back to the manager.
                            mPalantirManager.releasePalantir(mine);
                        }
                    } catch (InterruptedException e) {
                        // If we're interrupted, notify the UI and
                        // exit gracefully
                        mControls.threadInterrupted(index);
                        return;
                    }
                }
            };
	}

	/**
	 * If a thread throws an exception, it should panic, stop all the other
	 * threads, and notify the UI.
	 */
	public static synchronized void panic() {
            mControls.exceptionThrown();
		
            for (Thread t : mBeings) {
                if (t.getId() != Thread.currentThread().getId())
                    t.interrupt();
            }
	}
}
