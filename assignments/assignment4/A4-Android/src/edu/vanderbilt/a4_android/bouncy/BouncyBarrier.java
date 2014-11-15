package edu.vanderbilt.a4_android.bouncy;

/**
 * @class BouncyBarrier
 *
 * @brief A model of a barrier on a Cartesian plane. These simple
 *        barriers extend from a center point in both the horizontal
 *        and vertical directions infinitely, dividing the plane into
 *        four quadrants. A barrier can tell if a given BouncyBalloon
 *        would cross it the next time it moves.
 * 
 *        Barriers can also be invincible. This allows us to place two
 *        invincible barriers that cover the edges of the screen,
 *        keeping balloons from ever bouncing off screen.
 */
public class BouncyBarrier {
    /**
     * The center of this barrier.
     */
    private Point center;

    /**
     * Is this barrier invincible?
     */
    private boolean invincible;

    /**
     * A constructor
     */
    public BouncyBarrier(Point center, boolean invincible) {
        this.center = center;
        this.invincible = invincible;
    }

    /**
     * Returns the center of this barrier.
     */
    public Point getCenter() {
        return center;
    }

    /**
     * Returns whether or not this barrier is invincible.
     */
    public boolean isInvincible() {
        return invincible;
    }

    /**
     * Returns true if the given motion would cross the barrier
     * horizontally.
     */
    public boolean crossesHorizontally(BouncyBalloon b) {
        Point start = b.getCenter();
        int movementX = b.getMovementX();
        return (start.x < center.x && start.x + movementX >= center.x)
            || (start.x >= center.x && start.x + movementX < center.x);
    }

    /**
     * Returns true if the given BouncyBalloon would cross the barrier
     * vertically. 
     */
    public boolean crossesVertically(BouncyBalloon b) {
        Point start = b.getCenter();
        int movementY = b.getMovementY();
        return (start.y < center.y && start.y + movementY >= center.y)
            || (start.y >= center.y && start.y + movementY < center.y);
    }
}
