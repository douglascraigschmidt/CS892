package edu.vanderbilt.a4_android.bouncy;

/**
 * @class BarrierObserver
 *
 * @brief The Observer in the Observer Pattern. Used by BarrierManager
 *        to notify any interested parties when a Barrier is
 *        added/removed from the list.
 */
public interface BarrierObserver {
    /**
     * Hook method called when a Barrier is added. 
     */
    public void onBarrierAdded(BouncyBarrier b);
	
    /**
     * Hook method called when a Barrier is removed. 
     */
    public void onBarrierRemoved(BouncyBarrier b);
}
