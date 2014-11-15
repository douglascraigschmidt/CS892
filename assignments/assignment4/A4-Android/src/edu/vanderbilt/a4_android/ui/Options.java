package edu.vanderbilt.a4_android.ui;

import edu.vanderbilt.a4_android.bouncy.BarrierManagerStrategy;
import edu.vanderbilt.a4_android.bouncy.BarrierManagerStrategy.Strategy;

/**
 * @class Options
 * 
 * Application wide settings for the BouncyBalloon application.
 */
public class Options {
    /**
     * Which strategy should we use for the BarrierManager?
     */
    public static final BarrierManagerStrategy.Strategy MANAGER_STRATEGY =
        Strategy.Synchronized;
	
    /**
     * How many milliseconds we should wait in between refreshing the
     * screen.
     */
    public static final long REFRESH_RATE = 10;
	
    /**
     * How many milliseconds we should wait in between moving each
     * balloon.
     */
    public static final long BOMB_MOVEMENT_SPEED = 33;
}
