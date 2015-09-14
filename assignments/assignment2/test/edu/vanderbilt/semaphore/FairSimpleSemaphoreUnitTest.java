package edu.vanderbilt.semaphore;

import java.util.ArrayList;
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
public class FairSimpleSemaphoreUnitTest {

   /** 
     * Keep track of if a runtime exception occurs
     */
    volatile boolean mFailed = false;
    
    /**
     * Keep track of if a thread is interrupted.
     */
    volatile boolean mInterrupted = false;
    
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
    
    @Test
    public void testNegativePermits() throws InterruptedException {
    	SimpleSemaphore simpleSemaphore =
    			new SimpleSemaphore(-1, true);
    	assertEquals(simpleSemaphore.availablePermits(), -1);
    	simpleSemaphore.release();
    	assertEquals(simpleSemaphore.availablePermits(), 0);
    	simpleSemaphore.release();
    	assertEquals(simpleSemaphore.availablePermits(), 1);
    	simpleSemaphore.acquire();
    	assertEquals(simpleSemaphore.availablePermits(), 0);
    	
    	final SimpleSemaphore simpleSemaphore2 = new SimpleSemaphore(-1, true);
    	
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
    	final ISemaphore semaphore = new SimpleSemaphore(PERMIT_COUNT, true);
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
    	
    	assertFalse(mFailed);
    	assertFalse("One of the threads was interrupted while calling acquire(). This shouldn't "
    			+ "happen (even if your Semaphore is wrong).", mInterrupted);
    }
    
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
                            
                    		try {    
                    			// Attempt to get a permit.
                    			simpleSemaphore.acquire();    				
                    		}
                    		catch (InterruptedException e) {
                    			mInterrupted = true;
                    			return;
                    		}
                    		
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
    	
    	// Check if anyone was interrupted.
    	assertFalse("One of the threads was interrupted while calling acquire(). This shouldn't "
    			+ "happen (even if your Semaphore is wrong).", mInterrupted);
    }
}
