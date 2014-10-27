package edu.vuum.mocca;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @class PingPongPaddle
 * 
 * @brief This class uses Java Condition and ReentrantLock to
 *        implement a blocking pingpong paddle abstraction.
 */
public class PingPongPaddle {
    /** Condition used to wait for the Pingpong ball. */
    // TODO - You fill in here.

    /** Lock used along with the Condition. */
    // TODO - You fill in here.

    /** Do we have the ball or not. */
    // TODO - You fill in here.

    /**
     * Constructor initializes data members.
     */
    public PingPongPaddle(boolean haveBall) {
        // TODO - You fill in here.
    }

    /**
     * Returns the ball to the other PingPongPaddle.
     */
    public void returnBall() {
        // TODO - You fill in here.
    }

    /**
     * Waits until the other PingPongPaddle hits the ball to us.
     */
    public void awaitBall() {
        // TODO - You fill in here.
    }
}

