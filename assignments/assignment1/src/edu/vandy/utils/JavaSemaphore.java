package edu.vandy.utils;

import java.util.concurrent.Semaphore;

/**
 * Implements a counting semaphore that uses a Java Semaphore provide
 * "fair" and "non-fair" semantics.  
 */
public class JavaSemaphore
       implements ISemaphore {
    /**
     * Keeps track of whether "fair" or "non-fair" semantics are
     * requested.  Plays the role of the Implementator class in the
     * Bridge pattern.
     */
    private Semaphore mSemaphore;

    /** 
     * Initialize the JavaSemaphore.
     * @param Initialpermits Initial number of permits assigned to the
     *        semaphore, which can be < 0 
     * @parame Fair {@code true} if this lock should use a fair
     *         ordering policy.
     */
    public JavaSemaphore(int initialPermits,
                         boolean fair) {
        // @@ TODO - you fill in here.
        mSemaphore = new Semaphore(initialPermits,
                                   fair);
    }
	
    /**
     * Acquire one permit from the semaphore in a manner that can be
     * interrupted.
     */
    @Override
    public void acquire() throws InterruptedException {
        // @@ TODO - you fill in here.
        mSemaphore.acquire();
    }

    /**
     * Acquire one permit from the semaphore in a manner that cannot be
     * interrupted.
     */
    @Override
    public void acquireUninterruptibly() {
        // @@ TODO - you fill in here.
        mSemaphore.acquireUninterruptibly();
    }
	
    /**
     * Return one permit to the semaphore.
     */
    @Override
    public void release() {
        // @@ TODO - you fill in here.
        mSemaphore.release();
    }

    /**
     * Return the number of permits available.
     */
    @Override
    public int availablePermits() {
        // @@ TODO - you fill in here.
        return mSemaphore.availablePermits();
    }
}
