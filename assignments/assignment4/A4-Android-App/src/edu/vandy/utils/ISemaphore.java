package edu.vandy.utils;

/**
 * This interface provides a simple counting semaphore to manage
 * concurrent access to a set of resources.  A semaphore may implement
 * "Fair" or "Unfair" policies.
 */
public interface ISemaphore {
    /**
     * Acquire one permit from the semaphore in a manner that can be
     * interrupted.
     */
    public void acquire() throws InterruptedException; 

    /**
     * Acquire one permit from the semaphore in a manner that cannot
     * be interrupted.
     */
    public void acquireUninterruptibly(); 
        
    /**
     * Return one permit to the semaphore.
     */
    public void release(); 

    /**
     * Return the number of permits available.
     */
    public int availablePermits();
}
