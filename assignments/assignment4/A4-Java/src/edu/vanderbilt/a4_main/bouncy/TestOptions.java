package edu.vanderbilt.a4_main.bouncy;

/**
 * @class TestOptions
 *
 * @brief Sets various constants used in the test.
 */
public class TestOptions {
    /**
     * The number of balloons to test.
     */
    public static final int NUM_THREADS = 50;
	
    /**
     * Scales the size of the arena. A bigger number means the time
     * between bounces will increase. A value of 1 means that at least
     * 1 balloon will be bouncing most of the time.
     */
    public static final int SCALE_FACTOR = 50;
}
