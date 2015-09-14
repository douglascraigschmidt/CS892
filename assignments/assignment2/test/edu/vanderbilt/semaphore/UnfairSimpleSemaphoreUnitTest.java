package edu.vanderbilt.semaphore;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import edu.vandy.utils.ISemaphore;
import edu.vandy.utils.SimpleSemaphore;

/**
 * @class SimpleSemaphoreUnitTest
 *
 * @brief Simple unit test for the SimpleSemaphore that just tests
 *        single-threaded logic.
 */
public class UnfairSimpleSemaphoreUnitTest {
    /** 
     * Keep track of if a runtime exception occurs
     */
    boolean mFailed = false;
    
    /**
     * Keep track of if a thread is interrupted for some reason
     */
    boolean mInterrupted = false;
    
    @Test
    public void testSimpleSemaphore() {
        SimpleSemaphore simpleSemaphore = 
            new SimpleSemaphore(2, false);
        assertNotNull(simpleSemaphore);
    }

    @Test
    public void testAcquire() throws InterruptedException {
        SimpleSemaphore simpleSemaphore =
            new SimpleSemaphore(2, false);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 0);
    }

    @Test
    public void testAcquireUninterruptibly() throws InterruptedException {
        SimpleSemaphore simpleSemaphore =
            new SimpleSemaphore(2, false);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquireUninterruptibly();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquireUninterruptibly();
        assertEquals(simpleSemaphore.availablePermits(), 0);
    }

    @Test
    public void testRelease() throws InterruptedException {
        SimpleSemaphore simpleSemaphore =
            new SimpleSemaphore(2, false);
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
            new SimpleSemaphore(2, false);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
    }
   
    @Test
    public void testNegativePermits() throws InterruptedException {
    	SimpleSemaphore simpleSemaphore =
    			new SimpleSemaphore(-1, false);
    	assertEquals(simpleSemaphore.availablePermits(), -1);
    	simpleSemaphore.release();
    	assertEquals(simpleSemaphore.availablePermits(), 0);
    	simpleSemaphore.release();
    	assertEquals(simpleSemaphore.availablePermits(), 1);
    	simpleSemaphore.acquire();
    	assertEquals(simpleSemaphore.availablePermits(), 0);
    	
    	final SimpleSemaphore simpleSemaphore2 = new SimpleSemaphore(-1, false);
    	
    	// This thread should block indefinitely.
    	Thread t = new Thread(new Runnable() {
    		@Override
    		public void run() {
    			try {
					simpleSemaphore2.acquire();
				} catch (InterruptedException e) {
					return;
				}
    			
    			// If we get here, something went wrong.
    			mFailed = true;
    		}
    	});
    	
    	// Start the thread.
    	t.start();
    	
    	// Wait two seconds for the thread to finish.
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
    	final ISemaphore semaphore = new SimpleSemaphore(PERMIT_COUNT, false);
    	// The number of threads that currently have a permit.
    	final AtomicLong runningThreads = new AtomicLong(0);
    	// Keep track of the threads we have so we can wait for them to finish later.
    	Thread [] threads = new Thread[THREAD_COUNT];
    	
    	for (int i = 0; i < THREAD_COUNT; ++i) {
            Thread t = new Thread(new Runnable() {
				
                    @Override
                        public void run() {
                        Random rand = new Random();
                        for (int i = 0; i < ACCESS_COUNT; ++i) {
                        	
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
	                        
                            // If there are more threads running than are supposed to be, throw an error.
                            if (running > PERMIT_COUNT)
                                throw new RuntimeException();
	                        
                            // Wait for an indeterminate amount of time.
                            try {
                                Thread.sleep(rand.nextInt(140) + 10);
                            } catch (InterruptedException e) {}
	                        
                            // Decrement the number of threads that have a permit.
                            runningThreads.decrementAndGet();
	                        
                            // Release the permit
                            semaphore.release();
                        }
                    }
                });
    		
            // If any of the threads throw an exception, then we failed.
            t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				
                    @Override
                        public void uncaughtException(Thread t, Throwable e) {
                        mFailed = true;
                    }
                });
    		
            threads[i] = t;
    		
            t.start();
    	}
    	
    	for (int i = 0; i < THREAD_COUNT; ++i)
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                fail("The main thread was interrupted for some reason.");
            }
    	
    	assertFalse("One of the threads threw an exception.", mFailed);
    	assertFalse("One of the threads was interrupted while calling acquire(). This shouldn't "
    			+ "happen (even if your Semaphore is wrong).", mInterrupted);
    }
   
}
