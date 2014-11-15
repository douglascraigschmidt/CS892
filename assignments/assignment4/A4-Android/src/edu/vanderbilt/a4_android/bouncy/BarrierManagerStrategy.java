package edu.vanderbilt.a4_android.bouncy;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * @class BarrierManagerStrategy
 *
 * @brief Uses the strategy pattern to provide various implementations of a
 *        BarrierManager. For more information on what a BarrierManager does,
 *        please see the BarrierManager interface comments.
 */
public class BarrierManagerStrategy implements BarrierManager {
    /**
     * The implementation that we'll forward the function calls to.
     */
    BarrierManager mImplementation;

    /**
     * Enumerate our various implementation strategies.
     */
    public static enum Strategy {
        Synchronized, ReentrantLock, RWLock, CopyOnWrite
            };

    /**
     * Construct an implementation.
     */
    public BarrierManagerStrategy(Strategy strategy) {
        switch (strategy) {
        case Synchronized:
            mImplementation = new SynchronizedBarrierManager();
            break;
        case ReentrantLock:
            mImplementation = new ReentrantBarrierManager();
            break;
        case RWLock:
            mImplementation = new RWBarrierManager();
            break;
        case CopyOnWrite:
            mImplementation = new CopyOnWriteManager();
            break;
        default:
            mImplementation = new SynchronizedBarrierManager();
            break;
        }
    }

    /**
     * Simply forward to the internal implementation.
     */
    @Override
    public void addBarrier(BouncyBarrier b) {
        mImplementation.addBarrier(b);
    }

    /**
     * Simply forward to the internal implementation.
     */
    @Override
    public void bounceAndRemoveIfNecessary(BouncyBalloon b) {
        mImplementation.bounceAndRemoveIfNecessary(b);
    }

    /**
     * Simply forward to the internal implementation.
     */
    @Override
    public void clear() {
        mImplementation.clear();
    }
	
    /**
     * Forward to the internal implementation
     */
    @Override
    public void setObserver(BarrierObserver o) {
        mImplementation.setObserver(o);
    }

    /**
     * @class BarrierManagerBase
     *
     * @brief An abstract base class that has some traits that are common to
     *        most BarrierManagers. This does not include any synchronization,
     *        but must be added by extending classes.
     *
     *        Note: this needs to be public so the StampedLock implementation
     *        can see it in the other test project.
     **/
    static public abstract class BarrierManagerBase implements BarrierManager {
        /**
         * Our list of barriers that we're managing.
         */
        protected List<BouncyBarrier> mBarriers = new ArrayList<BouncyBarrier>();

        /**
         * An observer that we will notify when adding/removing.
         */
        private BarrierObserver mObserver = null;

        /**
         * Sets the observer that we will be notifying.
         */
        public void setObserver(BarrierObserver o) {
            mObserver = o;
        }

        /**
         * Adds a barrier to the list.
         */
        public void addBarrier(BouncyBarrier b) {
            mBarriers.add(b);

            if (mObserver != null)
                mObserver.onBarrierAdded(b);
        }

        /**
         * Removes a barrier from the list.
         */
        protected void removeBarrier(BouncyBarrier b) {
            mBarriers.remove(b);

            if (mObserver != null)
                mObserver.onBarrierRemoved(b);
        }

        /**
         * Clears the barriers.
         */
        public void clear() {
            for (BouncyBarrier b : mBarriers)
                mObserver.onBarrierRemoved(b);

            mBarriers.clear();
        }

        /**
         * Bounces a ball if necessary. Returns the barrier that should be
         * removed, if any.
         */
        protected BouncyBarrier bounceIfNecessary(BouncyBalloon b) {
            // Iterate over all the barriers.
            boolean bounced = false;
            BouncyBarrier toRemove = null;
            for (Iterator<BouncyBarrier> i = mBarriers.iterator(); i.hasNext();) {
                BouncyBarrier barrier = i.next();
                if (barrier.crossesHorizontally(b)) {
                    b.bounceX();
                    bounced = true;
                }
                if (barrier.crossesVertically(b)) {
                    b.bounceY();
                    bounced = true;
                }

                if (bounced) {
                    toRemove = barrier;
                    break;
                }
            }

            // If we bounced off the barrier and exploded, we should remove that
            // barrier.
            if (bounced && b.getBouncesLeft() <= 0 && !toRemove.isInvincible())
                return toRemove;
            else
                return null;
        }

        /**
         * Bounces a ball/removes a barrier if necessary.
         */
        public void bounceAndRemoveIfNecessary(BouncyBalloon b) {
            // Bounce the balloon and see if we have to remove a barrier
            BouncyBarrier toRemove = bounceIfNecessary(b);
            // If we do, remove it.
            if (toRemove != null)
                removeBarrier(toRemove);
        }
		
    }

    /**
     * @class SynchronizedBarrierManager
     * 
     * @brief An implemenation of BarrierManager that uses the synchronized
     *        keyword.
     */
    static private class SynchronizedBarrierManager extends BarrierManagerBase {
        /**
         * Adds a barrier to this manager concurrently using the synchronized
         * keyword.
         */
        @Override
            public synchronized void addBarrier(BouncyBarrier b) {
                super.addBarrier(b);
            }

        /**
         * Checks if a balloon needs to bounce off a barrier and bounces it. If
         * the balloon explodes, it will remove the barrier that was bounced off
         * of. Coordinates access to the manager using the synchronized keyword.
         */
        @Override
            public synchronized void bounceAndRemoveIfNecessary(BouncyBalloon b) {
                super.bounceAndRemoveIfNecessary(b);
            }

        /**
         * Removes all barriers from this manager. Coordinates access using the
         * synchronized keyword.
         */
        @Override
            public synchronized void clear() {
                super.clear();
            }

    }

    /**
     * @class ReentrantBarrierManager
     * 
     * @brief An implemenation of BarrierManager that uses the ReentrantLocks.
     */
    static private class ReentrantBarrierManager extends BarrierManagerBase {
        // TODO - You fill in here

        /**
         * A reentrant lock to restrict access to the barriers.
         */
        ReentrantLock mLock = null; 

        /**
         * Adds a barrier to this manager concurrently using reentrant locks to
         * coordinate access.
         */
        @Override
            public void addBarrier(BouncyBarrier b) {
            // TODO - You fill in here
        }

        /**
         * Checks if a balloon needs to bounce off a barrier and bounces it. If
         * the balloon explodes, it will remove the barrier that was bounced off
         * of. Coordinates access to the manager using the reentrant locks.
         */
        @Override
            public void bounceAndRemoveIfNecessary(BouncyBalloon b) {
            // TODO - You fill in here
        }

        /**
         * Removes all barriers from this manager. Coordinates access using a
         * ReentrantLock.
         */
        @Override
            public void clear() {
            // TODO - You fill in here
        }
    }

    /**
     * @class RWBarrierManager
     *
     * @brief An implemenation of BarrierManager that uses
     *        ReentrantReadWriteLocks.
     */
    static private class RWBarrierManager extends BarrierManagerBase {

        /**
         * A RWLock to restrict access to the barriers.
         */
        ReentrantReadWriteLock mLock;

        /**
         * Cache the read and write locks.
         */
        ReadLock mReadLock;
        WriteLock mWriteLock;

        /**
         * Constructor initializes the data members.
         */
        public RWBarrierManager() {
            // TODO - You fill in here
        }

        /**
         * Adds a barrier to this manager concurrently using a ReadWriteLock to
         * coordinate concurrent access.
         */
        @Override
            public void addBarrier(BouncyBarrier b) {
            // TODO - You fill in here
        }

        /**
         * Checks if a balloon needs to bounce off a barrier and bounces it. If
         * the balloon explodes, it will remove the barrier that was bounced off
         * of. Coordinates access to the manager using a ReentrantReadWriteLock
         */
        @Override
            public void bounceAndRemoveIfNecessary(BouncyBalloon b) {
            // TODO - You fill in here. If the balloon has more than two bounces
            // left, use a read lock. Otherwise, use a write lock.
        }

        /**
         * Removes all barriers from this manager. Coordinates access using a
         * ReentrantReadWriteLock.
         */
        @Override
            public void clear() {
            // TODO - You fill in here
        }
    }

    /*
     * @class CopyOnWriteManager
     * 
     * @brief An implementation of BarrierManager that simply uses a
     * CopyOnWriteArrayList to handle concurrent access. This is by far the
     * fastest implementation in the set.
     */
    static private class CopyOnWriteManager extends BarrierManagerBase {

        /**
         * Makes BarrierManagerBase us a CopyOnWriteArrayList instead of a plain
         * ArrayList. This eliminates any need for synchronization, although it
         * may in some cases take up an unnecessary amount of memory.
         */
        public CopyOnWriteManager() {
            mBarriers = new CopyOnWriteArrayList<BouncyBarrier>();
        }
    }
}
