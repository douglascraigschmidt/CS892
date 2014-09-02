package edu.vanderbilt.semaphore;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * @class SimpleSemaphoreUnitTest
 *
 * @brief Simple unit test for the SimpleSemaphore that just tests
 *        single-threaded logic.
 */
public class SimpleSemaphoreUnitTest {
    @Test
    public void testSimpleSemaphore() {
        SimpleSemaphore simpleSemaphore = 
            new SimpleSemaphore(2, true);
        assertNotNull(simpleSemaphore);
    }

    @Test
    public void testAcquire() throws InterruptedException {
        SimpleSemaphore simpleSemaphore =
            new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 0);
    }

    @Test
    public void testAcquireUninterruptibly() throws InterruptedException {
        SimpleSemaphore simpleSemaphore =
            new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquireUninterruptibly();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquireUninterruptibly();
        assertEquals(simpleSemaphore.availablePermits(), 0);
    }

    @Test
    public void testRelease() throws InterruptedException {
        SimpleSemaphore simpleSemaphore =
            new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 0);
        simpleSemaphore.release();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.release();
        assertEquals(simpleSemaphore.availablePermits(), 2);
    }
	
    @Test
    public void testAvailablePermits() throws InterruptedException{
        SimpleSemaphore simpleSemaphore =
            new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
    }
    
    /**
     * Keep track of if a runtime exception occurs
     */
    boolean mFailed = false;
    
    @Test
    public void testFairness() throws InterruptedException {
        final SimpleSemaphore simpleSemaphore = 
            new SimpleSemaphore(1, true);

        final int MAX_THREADS = 4;
    	
    	final FairnessChecker checker =
            new FairnessChecker(MAX_THREADS);
    	
    	// Acquire the only permit so that the threads block
    	simpleSemaphore.acquire();
    	
    	ArrayList<Thread> threads =
            new ArrayList<Thread>(MAX_THREADS);
    	
    	// Create four threads that try to acquire a permit.
    	for (int i = 0; i < MAX_THREADS; ++i) {
            Thread t = new Thread (new Runnable () {
                    public void run () {
                        for (int i = 0;
                             i < (MAX_THREADS - 1);
                             ++i) {
                            // Attempt to get a permit.
                            simpleSemaphore.acquireUninterruptibly();    				
	    				
                            // Once we've acquired a permit, check to
                            // make sure that we were next in line to
                            // receive a permit.
                            mFailed = mFailed 
                                || !checker.checkOrder(Thread.currentThread().getName());
	    				
                            // We should be the only thread here, so
                            // it's safe to add our name to the list
                            // again.
                            checker.addNewThread(Thread.currentThread().getName());
	    				
                            // Wait for a little bit, just to be sure
                            // the last thread has enough time to try
                            // and re-acquire.
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException e) {}
	    				
                            // Release the permit for the next thread.
                            simpleSemaphore.release();
                        }
                    }
    		});
    		
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                        public void uncaughtException(Thread t, Throwable e) {
                        mFailed = true;
                    }
                });

            // Add the current thread's name to the checker queue
            checker.addNewThread(t.getName());
    		
            // Add the current thread to our list of threads, so we
            // can wait for them to finish later.
            threads.add(t);
    		
            // Start the thread.
            t.start();
    		
            // Wait to make sure that the thread locks before we start
            // the next one.
            Thread.sleep(250);
    	}
    	
    	// Release the permit so the first Thread starts to run.
    	simpleSemaphore.release();
    	
    	// Wait for all the Threads to exit.
    	for (Thread t : threads)
            t.join();
    	
    	// Check if we failed.
    	assertTrue(!mFailed);
    }
}
