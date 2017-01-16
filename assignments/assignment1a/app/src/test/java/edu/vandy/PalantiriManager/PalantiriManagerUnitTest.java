package edu.vandy.PalantiriManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import edu.vandy.model.Palantir;
import edu.vandy.model.PalantiriManager;

/**
 * Unit test for the PalantiriManager.
 */
public class PalantiriManagerUnitTest {
    /** 
     * Keep track of if a runtime exception occurs
     */
    volatile boolean mFailed = false;
    
    /**
     * Keep track of whether a thread is interrupted.
     */
    volatile boolean mInterrupted = false;

    /**
     * Keep track of whether an exception occurs.
     */
    protected volatile boolean exc = false;
    
    @Test
    public void testPalantiriManager() {
        PalantiriManager palantiriManager = makePalantiri(2);
        assertNotNull(palantiriManager);
    }

    public PalantiriManager makePalantiri(int palantiriCount) {
    	// Create a list to hold the generated Palantiri.
        final List<Palantir> palantiri =
            new ArrayList<Palantir>();		

        // Create a new Random number generator.
        final Random random = new Random();

        // Create and add each new Palantir into the list.  The id of
        // each Palantir is its position in the list.
        for (int i = 0; i < palantiriCount; ++i) 
            palantiri.add(new Palantir(i,
                                       random));

        // Create a PalantiriManager that is used to mediate
        // concurrent access to the List of Palantiri.
        return new PalantiriManager(palantiri);
    }

    @Test
    public void testAcquire() throws InterruptedException {
        Thread t =
            new Thread(() -> {
                    try {
                        PalantiriManager palantiriManager =
                            makePalantiri(2);
                        assertEquals(palantiriManager.availablePermits(), 2);
                        palantiriManager.acquire();
                        assertEquals(palantiriManager.availablePermits(), 1);
                        palantiriManager.acquire();
                        assertEquals(palantiriManager.availablePermits(), 0);
                    } catch(AssertionError e) {
                        exc = true;
                        System.out.println(e);
                    }
            });
        t.start();
        t.join();
        assertEquals(exc, false);
        exc = false;
    }

    @Test
    public void testRelease() throws InterruptedException {
    	Thread t =
            new Thread(() -> {
                try {
                    PalantiriManager palantiriManager =
                            makePalantiri(2);
                    assertEquals(palantiriManager.availablePermits(), 2);
                    Palantir palantir1 = palantiriManager.acquire();
                    assertEquals(palantiriManager.availablePermits(), 1);
                    Palantir palantir2 = palantiriManager.acquire();
                    assertEquals(palantiriManager.availablePermits(), 0);
                    palantiriManager.release(palantir1);
                    assertEquals(palantiriManager.availablePermits(), 1);
                    palantiriManager.release(palantir2);
                    assertEquals(palantiriManager.availablePermits(), 2);
                    palantiriManager.release(null);
                    assertEquals(palantiriManager.availablePermits(), 2);
                } catch (AssertionError e) {
                    exc = true;
                }
            });
    	t.start();
    	t.join();
    	assertEquals(exc,false);
    	exc = true;
    }
	
    @Test
    public void testavailablePalantiri() throws InterruptedException{
        Thread t =
            new Thread(() -> {
                    try {
                        PalantiriManager palantiriManager =
                            makePalantiri(2);
                        assertEquals(palantiriManager.availablePermits(), 2);
                        palantiriManager.acquire();
                        assertEquals(palantiriManager.availablePermits(), 1);
                    }
                    catch(AssertionError e) {
                        exc = true;
                    }
            });
        t.start();
        t.join();
        assertEquals(exc, false);
        exc = true;
    }
}
