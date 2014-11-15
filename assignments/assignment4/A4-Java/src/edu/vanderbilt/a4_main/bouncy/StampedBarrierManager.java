package edu.vanderbilt.a4_main.bouncy;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.locks.StampedLock;

import edu.vanderbilt.a4_android.bouncy.BarrierManagerStrategy.BarrierManagerBase;
import edu.vanderbilt.a4_android.bouncy.BouncyBalloon;
import edu.vanderbilt.a4_android.bouncy.BouncyBarrier;

/**
 * @class StampedBarrierManager
 *
 * @brief An implementation of BarrierManager that uses the StampedLocks.
 */
class StampedBarrierManager extends BarrierManagerBase {
    /**
     * A StampedLock to restrict access to the barriers.
     */
    StampedLock mLock;

    /**
     * Constructor initializes the data members.
     */
    public StampedBarrierManager() {
        // TODO - You fill in here.
        mLock = null;
    }

    /**
     * 
     */
    @Override
    public void addBarrier(BouncyBarrier b) {
        // TODO - You fill in here.
    }

    /**
     *
     */
    private boolean tryOptimisticBounce(BouncyBalloon b) {
        // If we have 2 bounces left, don't even try optimistic
        // reading.
        if (b.getBouncesLeft() <= 2)
            return false;

        // TODO - You fill in here to fix this implementation. Some comments are left to guide you. 
		
        // Get an optimstic reading lock

        // Iterate over all the barriers.
        boolean bouncedX = false;
        boolean bouncedY = false;

        try {
            for (Iterator<BouncyBarrier> i = mBarriers.iterator(); i.hasNext();) {
                BouncyBarrier barrier = i.next();

                // Sometimes returns null pointers because we're
                // modifying it concurrently.
                if (barrier == null)
                    return false;

                if (barrier.crossesHorizontally(b)) {
                    bouncedX = true;
                }
                if (barrier.crossesVertically(b)) {
                    bouncedY = true;
                }

                if (bouncedX || bouncedY) {
                    break;
                }
            }
        } catch (ConcurrentModificationException e) {
            return false;
        }

        // Return false if the lock fails to validate

        // Otherwise bounce the balloons and return true.
    }

    /**
     * Uses a StampedLock to use optimistic locking
     */
    @Override
    public void bounceAndRemoveIfNecessary(BouncyBalloon b) {
		
        // Try doing things optimistically first.
        if (tryOptimisticBounce(b))
            return;

        // TODO - You fill in here. Some comments are left to guide you.
		
        // If that doesn't work, grab a read lock.

        // Bounce the balloon and figure out if we need to remove a barrier.

        // If we have to remove a barrier.
        // Try upgrading to a write lock.

        // If we didn't acquire the write lock on the first try,
        // release the read lock and explicity acquire the write lock.

        // Remove the barrier.
        // Release the lock
		
        // Else just release the read lock

    }

    /**
     * Removes all barriers from this manager. Coordinate access using the
     * StampedLock.
     */
    @Override
    public void clear() {
        // TODO - You fill in here.
    }

}
