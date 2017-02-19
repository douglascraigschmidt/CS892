package edu.vandy.PalantiriManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.vandy.model.FairSemaphore;

/**
 * Unit test for the FairSemaphore.
 */
public class FairSemaphoreUnitTest {
    /** 
     * Keep track of if a runtime exception occurs
     */
    volatile boolean mFailed = false;
    
    /**
     * Keep track of if a thread is interrupted.
     */
    volatile boolean mInterrupted = false;
    
    @Test
    public void testFairSemaphore() {
        FairSemaphore fairSemaphore = 
            new FairSemaphore(2);
        assertNotNull(fairSemaphore);
    }

    @Test
    public void testAcquire() throws InterruptedException {
        FairSemaphore fairSemaphore =
            new FairSemaphore(2);
        assertEquals(fairSemaphore.availablePermits(), 2);
        fairSemaphore.acquire();
        assertEquals(fairSemaphore.availablePermits(), 1);
        fairSemaphore.acquire();
        assertEquals(fairSemaphore.availablePermits(), 0);
    }

    @Test
    public void testAcquireUninterruptibly() throws InterruptedException {
        FairSemaphore fairSemaphore =
            new FairSemaphore(2);
        assertEquals(fairSemaphore.availablePermits(), 2);
        fairSemaphore.acquireUninterruptibly();
        assertEquals(fairSemaphore.availablePermits(), 1);
        fairSemaphore.acquireUninterruptibly();
        assertEquals(fairSemaphore.availablePermits(), 0);
    }

    @Test
    public void testRelease() throws InterruptedException {
        FairSemaphore fairSemaphore =
            new FairSemaphore(2);
        assertEquals(fairSemaphore.availablePermits(), 2);
        fairSemaphore.acquire();
        assertEquals(fairSemaphore.availablePermits(), 1);
        fairSemaphore.acquire();
        assertEquals(fairSemaphore.availablePermits(), 0);
        fairSemaphore.release();
        assertEquals(fairSemaphore.availablePermits(), 1);
        fairSemaphore.release();
        assertEquals(fairSemaphore.availablePermits(), 2);
    }
	
    @Test
    public void testAvailablePermits() throws InterruptedException{
        FairSemaphore fairSemaphore =
            new FairSemaphore(2);
        assertEquals(fairSemaphore.availablePermits(), 2);
        fairSemaphore.acquire();
        assertEquals(fairSemaphore.availablePermits(), 1);
    }
    
    @Test
    public void testNegativePermits() throws InterruptedException {
    	FairSemaphore fairSemaphore =
            new FairSemaphore(-1);
    	assertEquals(fairSemaphore.availablePermits(), -1);
    	fairSemaphore.release();
    	assertEquals(fairSemaphore.availablePermits(), 0);
    	fairSemaphore.release();
    	assertEquals(fairSemaphore.availablePermits(), 1);
    	fairSemaphore.acquire();
    	assertEquals(fairSemaphore.availablePermits(), 0);
    	
    	FairSemaphore fairSemaphore2 = 
            new FairSemaphore(-1);
    	
    	// This thread should block indefinitely.
    	Thread t = new Thread(() -> {
                try {
                    fairSemaphore2.acquire();
                } catch (InterruptedException e) {
                    return;
                }
    			
                // If we get here, something went wrong.
                mFailed = true;
            });
    	
    	// Start the thread.
    	t.start();
    	
    	// Wait two seconds.
    	t.join(2000);
    	
    	// The thread we were waiting on should never have returned.
    	assertFalse(mFailed);
    	
    	// Interrupt the thread, if it hasn't been already.
    	t.interrupt();    	
    }
   
    @Test
    public void testConcurrentAccess() {
    	// The number of threads that will be trying to run at once.
    	final int THREAD_COUNT = 5;

    	// The number of threads that we want to let run at once.
    	final int PERMIT_COUNT = 2;

    	// The number of times each thread will try to access the semaphore.
    	final int ACCESS_COUNT = 5;
    	
    	assertTrue(THREAD_COUNT > PERMIT_COUNT);
    	
    	// The semaphore we're testing.
    	FairSemaphore semaphore =
            new FairSemaphore(PERMIT_COUNT);

    	// The number of threads that currently have a permit.
    	final AtomicLong runningThreads = new AtomicLong(0);

    	// Keep track of the threads we have so we can wait for them
    	// to finish later.
    	Thread [] threads = new Thread[THREAD_COUNT];
    	
        // Iterate through all the threads, define their behavior, and
        // start them.
    	for (int i = 0; i < THREAD_COUNT; ++i) {
            Thread t = new Thread(() -> {
                    Random rand = new Random();
                    for (int j = 0; j < ACCESS_COUNT; ++j) {
		                    
                        try { 
                            // Acquire a permit from the semaphore.
                            semaphore.acquire();
                        }
                        catch (InterruptedException e) {
                            mInterrupted = true;
                            return;
                        }
		                    
                        // Increment the number of threads that have a permit.
                        long running = runningThreads.incrementAndGet();
	                        
                        // If there are more threads running than are
                        // supposed to be, throw an error.
                        if (running > PERMIT_COUNT)
                            throw new RuntimeException();
	                        
                        // Wait for an indeterminate amount of time.
                        try {
                            Thread.sleep(rand.nextInt(140) + 10);
                        } catch (InterruptedException e) {
                            System.out.println("In testConcurrentAccess(), where thread "
                                               + Thread.currentThread().getId()
                                               + " got an InterruptedException");
                        }
	                        
                        // Decrement the number of threads that have a
                        // permit.
                        runningThreads.decrementAndGet();
	                        
                        // Release the permit
                        semaphore.release();
                    }
                });
    		
            // If any of the threads throw an exception, then we
            // failed.
            t.setUncaughtExceptionHandler((thread, ex) -> mFailed = true);
                
    		
            threads[i] = t;
    		
            t.start();
    	}
    	
    	for (Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("Thread " 
                     + thread
                     + " was interrupted for some reason.");
            }
    	
    	assertFalse(mFailed);
    	assertFalse("One of the threads was interrupted while calling acquire(). "
                    + "This shouldn't happen (even if your Semaphore is wrong).",
                    mInterrupted);
    }
    
    @Test
    public void testFairness() throws InterruptedException {
        FairSemaphore fairSemaphore = 
            new FairSemaphore(1);

        int MAX_THREADS = 4;
    	
    	FairnessChecker checker =
            new FairnessChecker(MAX_THREADS);
    	
    	// Acquire the only permit so that the threads block when
    	// started.
    	fairSemaphore.acquire();
    	
    	List<Thread> threads =
            new ArrayList<>(MAX_THREADS);
    	
    	// Create MAX_THREADS that try to acquire a multiple times.
    	for (int i = 0; i < MAX_THREADS; ++i) {
            Thread t = new Thread (() -> {
                    for (int j = 0;
                         j < (MAX_THREADS - 1);
                         ++j) {
                            
                        try {    
                            // Attempt to get a permit.
                            fairSemaphore.acquire();    				
                        } catch (InterruptedException e) {
                            mInterrupted = true;
                            return;
                        }
                    		
                        // Once we've acquired a permit, check to make
                        // sure that we were next in line to receive a
                        // permit.
                        mFailed = mFailed || !checker.isFifoOrder();
	    				
                        // We should be the only thread running, so
                        // it's safe to add our name to the list
                        // again.
                        checker.addCurrentThread();
	    				
                        // Wait for a little bit, just to be sure the
                        // last thread has enough time to try and
                        // re-acquire.
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            System.out.println("In testFairness(), where thread "
                                               + Thread.currentThread().getId()
                                               + " got an InterruptedException");
                        }
	    				
                        // Release the permit for the next thread.
                        fairSemaphore.release();
                    }
                });

            // Set the uncaught exception handler for this thread. 
            t.setUncaughtExceptionHandler((thread, ex) -> mFailed = true);

            // Add the current thread's name to the checker queue.
            checker.addNewThread(t.getId());
    		
            // Add the current thread to our list of threads, so we
            // can wait for them to finish later.
            threads.add(t);
    		
            // Start the thread.
            t.start();
    		
            // Wait to make sure the thread locks before we start the
            // next one.
            Thread.sleep(250);
        }
    	
    	// Release the permit so the first Thread starts to run.
    	fairSemaphore.release();
    	
    	// Wait for all the threads to exit.
    	for (Thread t : threads)
            t.join();
    	
    	// Check if we failed.
    	assertTrue(!mFailed);
    	
    	// Check if anyone was interrupted.
    	assertFalse("One of the threads was interrupted while calling acquire(). "
                    + "This shouldn't happen (even if your Semaphore is wrong).", 
                    mInterrupted);
    }

    @Test
    public void testFairnessWithInterrupts() throws InterruptedException {
        FairSemaphore fairSemaphore = 
            new FairSemaphore(1);

        int MAX_THREADS = 4;
    	
    	FairnessChecker checker =
            new FairnessChecker(MAX_THREADS);
    	
    	// Acquire the only permit so that the threads block when
    	// started.
    	fairSemaphore.acquire();
    	
    	List<Thread> threads =
            new ArrayList<>(MAX_THREADS);
    	
    	// Create MAX_THREADS that try to acquire a multiple times.
    	for (int i = 0; i < MAX_THREADS; ++i) {
            Thread t = new Thread (() -> {
                    for (int j = 0;
                         j < (MAX_THREADS - 1);
                         ++j) {
                            
                        try {    
                            // Attempt to get a permit.
                            fairSemaphore.acquire();    				
                        } catch (InterruptedException e) {
                            mInterrupted = true;
                            System.out.println("In testFairnessWithInterrupts(), where thread "
                                               + Thread.currentThread().getId()
                                               + " got an InterruptedException");
                            return;
                        }
                    		
                        // Once we've acquired a permit, check to make
                        // sure that we were next in line to receive a
                        // permit.
                        mFailed = mFailed || !checker.isFifoOrder();
	    				
                        // We should be the only thread running, so
                        // it's safe to add our name to the list
                        // again.
                        checker.addCurrentThread();
	    				
                        // Wait for a little bit, just to be sure the
                        // last thread has enough time to try and
                        // re-acquire.
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            mInterrupted = true;
                            System.out.println("In testFairnessWithInterrupts(), where thread "
                                               + Thread.currentThread().getId()
                                               + " got an InterruptedException");
                            return;
                        }
	    				
                        // Release the permit for the next thread.
                        fairSemaphore.release();
                    }
                });

            // Set the uncaught exception handler for this thread. 
            t.setUncaughtExceptionHandler((thread, ex) -> mFailed = true);

            // Add the current thread's name to the checker queue.
            checker.addNewThread(t.getId());
    		
            // Add the current thread to our list of threads, so we
            // can wait for them to finish later.
            threads.add(t);
    		
            // Start the thread.
            t.start();
    		
            // Wait to make sure the thread locks before we start the
            // next one.
            Thread.sleep(250);
        }
    	
    	// Release the permit so the first Thread starts to run.
    	fairSemaphore.release();

        // Wait a bit to make sure the thread are running before
        // interrupting them.
        Thread.sleep(2000);
    	
    	// Interrupt all the threads.
        threads.forEach(Thread::interrupt);

    	// Wait for all the threads to exit.
    	for (Thread t : threads)
            t.join();
    	
    	// Check if we failed.
    	assertTrue(!mFailed);

        // Reset this flag.
        mInterrupted = false;
    }
}
