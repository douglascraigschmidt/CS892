package edu.vanderbilt.a4_android.bouncy;

import edu.vanderbilt.a4_android.ui.Options;

/**
 * @class BouncyBalloonRunnable
 * 
 * @brief A runnable that manages the actions of a single
 *        BouncyBalloon. It will continue running until the
 *        BouncyBalloon explodes, and then it will stop. Each frame,
 *        the runnable will see if the balloon will collide with a
 *        barrier and then bounce it if necessary.
 */
public class BouncyBalloonRunnable implements Runnable {
    /**
     * The balloon we're managing.
     */
    BouncyBalloon mBalloon;

    /**
     * A barrier manager that tells us where the walls are and when to
     * bounce.
     */
    BarrierManager mManager;

    /**
     * Constructor initializes the data members. 
     */
    public BouncyBalloonRunnable(BouncyBalloon mBalloon,
                                 BarrierManager mManager) {
        super();
        this.mBalloon = mBalloon;
        this.mManager = mManager;
    }

    /**
     * This method runs until the balloon we're managing explodes. 
     */
    @Override
    public void run() {
        while (true) {
            // Bounce the ball if necessary. The manager takes care of
            // removing barriers.
            if (mBalloon.getBouncesLeft() > 0)
                mManager.bounceAndRemoveIfNecessary(mBalloon);
	
            // If we've exploded
            if (mBalloon.getBouncesLeft() <= 0) {			
                return;
            }
            // Otherwise, move the balloon.
            else {
                mBalloon.move();
            }
			
            try {
                Thread.sleep(Options.BOMB_MOVEMENT_SPEED);
            } catch (InterruptedException e) {
                // If someone interrupted us, then they're probably
                // trying to stop us.
                return;
            }
        }
    }
}
