package edu.vanderbilt.a4_android.bouncy;

import java.util.List;

/**
 * @class BarrierManager
 *
 * @brief A BarrierManager manages the barriers currently on the grid. It can
 *        check if a given movement crosses any of the barriers on the grid and
 *        it should be synchronized to allow concurrent access from multiple
 *        threads.
 * 
 *        Given a BouncyBalloon, the BarrierManager can check if the balloon
 *        will cross a barrier the next time it moves. If it will, it will
 *        "bounce" the balloon. If the balloon explodes, then the manager should
 *        handler removing the appropriate barrier.
 *
 *        BarrierManager also uses the Observer pattern, allowing an Observer to
 *        be notified when barriers are added/removed.
 */
public interface BarrierManager {
    /**
     * Add a barrier to the manager.
     */
    public void addBarrier(BouncyBarrier b);

    /**
     * Check if the BouncyBalloon will bounce off of any of the barriers we're
     * managing. If it will, change the BouncyBalloon's movement appropriately
     * (make it bounce). If the BouncyBalloon explodes, this method will remove
     * the appropriate barrier.
     * 
     * To keep this fairly simple, if the balloon will bounce off of multiple
     * barriers, only consider the first one you encounter. Also, if the balloon
     * crosses a barrier both horizontally and vertically, then it should bounce
     * both horizontally and vertically at the same time.
     */
    public void bounceAndRemoveIfNecessary(BouncyBalloon b);

    /**
     * Adds an observer that will be notified when Barriers are added/removed.
     */
    public void setObserver(BarrierObserver o);

    /**
     * Clear all the barriers from the list
     */
    public void clear();
}
