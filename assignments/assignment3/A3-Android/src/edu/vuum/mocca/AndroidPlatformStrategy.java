package edu.vuum.mocca;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

import android.util.Log;

/**
 * @class AndroidPlatformStrategy
 * 
 * @brief Provides methods that define a platform-independent API for
 *        output data to Android UI thread and synchronizing on thread
 *        completion in the ping/pong game. It plays the role of the
 *        "Concrete Strategy" in the Strategy pattern.
 */
public class AndroidPlatformStrategy extends PlatformStrategy {
    /**
     * Latch to decrement each time a thread exits to control when the
     * play() method returns.
     */
    private CountDownLatch mLatch = null;

    /** Activity variable finds GUI widgets by view. */
    private final WeakReference<PingPongOutputInterface> mOuterClass;

    public AndroidPlatformStrategy(final Object output) {
        /** The current activity window (succinct or verbose). */
        mOuterClass = new WeakReference<PingPongOutputInterface>
            ((PingPongOutputInterface) output);
    }

    /** Do any initialization needed to start a new game. */
    public void begin() {
        /** (Re)initialize the CountDownLatch. */
        // TODO - You fill in here.
    }

    /** 
     * Print the outputString to the display. Blocks for 0.5 seconds
     * to let the user see what's going on.
     */
    public void print(final String outputString) {
        // TODO - You fill in here.
    }

    /** Indicate that a game thread has finished running. */
    public void done() {
        // TODO - You fill in here.
    }

    /** Barrier that waits for all the game threads to finish. */
    public void awaitDone() {
        // TODO - You fill in here.
    }

    /**
     * Error log formats the message and displays it for the debugging
     * purposes.
     */
    public void errorLog(String javaFile, String errorMessage) {
        Log.e(javaFile, errorMessage);
    }
}
