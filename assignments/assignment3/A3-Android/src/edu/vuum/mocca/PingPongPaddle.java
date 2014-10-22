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
    Condition mBall;

    /** Lock used along with the Condition. */
    // TODO - You fill in here.
    Lock mLock;

    /** Do we have the ball or not. */
    // TODO - You fill in here.
    boolean mHaveBall;

    /**
     * Constructor initializes data members.
     */
    public PingPongPaddle(boolean haveBall) {
        // TODO - You fill in here.
        mLock = new ReentrantLock();
        mBall = mLock.newCondition();
        mHaveBall = haveBall;
    }

    /**
     * Returns the ball to the other PingPongPaddle.
     */
    public void returnBall() {
        // TODO - You fill in here.
        mLock.lock();
        try {
            mHaveBall = true;
            mBall.signal();
        } finally {
            mLock.unlock();
        }
    }

    /**
     * Waits until the other PingPongPaddle hits the ball to us.
     */
    public void awaitBall() {
        // TODO - You fill in here.
        mLock.lock();
        try {
            // Wait until we've been hit the ball.
            while (mHaveBall == false)
                mBall.awaitUninterruptibly();
            mHaveBall = false;
        } finally {
            mLock.unlock();
        }
    }
}

