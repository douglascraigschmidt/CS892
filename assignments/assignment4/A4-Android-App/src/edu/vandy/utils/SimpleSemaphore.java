package edu.vandy.utils;

import java.util.ArrayList;

/**
 * Implements a counting semaphore that uses Java built-in monitor
 * objects to provide "fair" and "non-fair" semantics.  Fair semantics
 * are implemented via the Specific Notification pattern at
 * www.dre.vanderbilt.edu/~schmidt/PDF/specific-notification.pdf.
 */
public class SimpleSemaphore 
       implements ISemaphore {
    /**
     * Keeps track of whether "fair" or "non-fair" semantics are
     * requested.  Plays the role of the Implementator class in the
     * Bridge pattern.
     */
    private SemaphoreBase mSemaphoreBase;

    /**
     * @class SemaphoreBase
     *
     * @brief This class abstracts the behavior of the fair/nonfair
     *        semaphore using the bridge pattern.
     */
    private abstract static class SemaphoreBase {
        /**
         * Define a count of the number of available permits.
         */
        protected volatile int mAvailablePermits;		

        /**
         * Initialize the mAvailablePermits data member.
         */
        SemaphoreBase (int availablePermits) {
            mAvailablePermits = availablePermits;
        }

        protected int availablePermits() {
            return mAvailablePermits;
        }

        abstract protected void acquire() throws InterruptedException;

        protected void acquireUninterruptibly() {
            for (boolean keepLooping = true;
                 keepLooping != false;
                 ) {
                try {
                    acquire();
                    keepLooping = false;
                }
                catch (InterruptedException e) {
                    // Implicit "keepLooping = true" if an interrupt
                    // occurs.
                }
            }
        }

        abstract protected void release();
    }

    /** 
     * Initialize the SimpleSemaphore.
     * @param Initialpermits Initial number of permits assigned to the
     *        semaphore, which can be < 0 
     * @parame Fair {@code true} if this lock should use a fair
     *         ordering policy.
     */
    public SimpleSemaphore(int initialPermits,
                           boolean fair) {
        // @@ TODO - you fill in here.
 		
        // If we're trying to be fair, we need a list of monitor
        // objects for each thread.

        mSemaphoreBase = fair 
            ? new FairSemaphore(initialPermits) 
            : new NonFairSemaphore(initialPermits);
    }
	
    /** 
     * @class NonFairSemaphore
     * 
     * @brief Implements the non-fair Semaphore using a Java
     *        built-in monitor object.
     */
    static final class NonFairSemaphore extends SemaphoreBase {
        NonFairSemaphore (int availablePermits) {
            super (availablePermits);
        }

        @Override
        protected void acquire() throws InterruptedException {
            synchronized (this) {
                // If there are no permits available, wait for one.
                while (mAvailablePermits <= 0)
                    wait();
				
                // Acquire the permit
                --mAvailablePermits;
            }
        }

        @Override
        protected void release() {
            synchronized(this) {
                // Release the permit
                ++mAvailablePermits;

                // Notify some thread waiting on this object.
                notify();
            }		
        }
    }

    /** 
     * @class FairSemaphore
     * 
     * @brief Implements the fair Semaphore using a Java built-in
     *        monitor object and the Specific Notification pattern.
     */
    private static final class FairSemaphore extends SemaphoreBase {
        /**
         * Keep track of the waiters in FIFO order for "fair" semantics.
         */
        // @@ TODO - you fill in here.
        private ArrayList<Object> mWaitQueue;

        FairSemaphore (int availablePermits) {
            super (availablePermits);
            mWaitQueue = new ArrayList<Object>();
        }

        @Override
        protected void acquire() throws InterruptedException {
            // We need this flag to avoid deadlock.
            boolean mustWait;

            // This is the "specific-notification lock".
            Object snLock = new Object();
            
            // Synchronize on object so we can call wait() on it
            // below.
            synchronized (snLock) {
                // Synchronize access to the queue to protect its
                // state from race conditions.
                synchronized (this) {
                    // We must wait if there are already conditions in
                    // the queue or if there are no permits available.
                    mustWait = !mWaitQueue.isEmpty() || mAvailablePermits <= 0;
				
                    // If we have to wait, add our thread to the queue
                    // in FIFO order.
                    if (mustWait)
                        mWaitQueue.add(snLock);
                    else {
                        // No need to wait, so decrement and return.
                        mAvailablePermits--;
                        return;
                    }
                }
				
                // The mustWait flag is used to ensure that wait()
                // isn't called on snLock with "this" lock held to
                // prevent deadlock.
                if (mustWait) {
                    try {
                        // Wait until we're notified via release().
                        snLock.wait();
                    } catch (InterruptedException e) {
                        // Try and remove ourselves from the queue.
                        synchronized (this) {
                            boolean removed =
                                mWaitQueue.remove(snLock);
                			
                            // If we're not on the queue, then
                            // someone released us.  Give back the
                            // permit.
                            if (!removed)
                                release();
                        }

                        // Rethrow the exception.
                        throw e;
                    }
                    // If we were waiting let the Thread calling
                    // release() decrement the permit count for us
                    // since we try to do it here we'll end up in a
                    // race with other Threads who might have entered
                    // the critical section above.
                }
            }		
        }

        /**
         * Return one permit to the semaphore.
         */
        @Override
        public void release() {
            synchronized(this) {
                // Release a permit.
                mAvailablePermits++;
		
                // Check if any other Thread is waiting on a permit.
                if (!mWaitQueue.isEmpty()) {
                    // If so, get the next waiter (in FIFO order).
                    Object nextWaiter = mWaitQueue.get(0);
                    
                    // Synchronize on the first item in the queue so
                    // that we can notify the next waiting Thread that
                    // a permit is available.
                    synchronized (nextWaiter) {
                    	// Remove the lock from the queue to avoid
                    	// race conditions with other Threads calling
                    	// release().
                    	mWaitQueue.remove(0);
                    	
                    	// Inform the next Thread blocked in acquire()
                    	// know a permit is available for it.
                        nextWaiter.notify();
                        
                        // Decrement the permit count here with the
                        // monitor lock held so the acquire() method
                        // doesn't incur a race condition.
                        mAvailablePermits--;
                    }
                }
            }
        }
    }

    /**
     * Acquire one permit from the semaphore in a manner that can be
     * interrupted.
     */
    @Override
    public void acquire() throws InterruptedException {
        // @@ TODO - you fill in here.
        mSemaphoreBase.acquire();
    }

    /**
     * Acquire one permit from the semaphore in a manner that cannot be
     * interrupted.
     */
    @Override
    public void acquireUninterruptibly() {
        // @@ TODO - you fill in here.
        mSemaphoreBase.acquireUninterruptibly();
    }
	
    /**
     * Return one permit to the semaphore.
     */
    @Override
    public void release() {
        // @@ TODO - you fill in here.
        mSemaphoreBase.release();
    }

    /**
     * Return the number of permits available.
     */
    @Override
    public int availablePermits() {
        // @@ TODO - you fill in here.

        return mSemaphoreBase.availablePermits();
    }
}
