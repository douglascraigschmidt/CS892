package edu.vanderbilt.a4_android.bouncy;

/**
 * @class BouncyBalloon
 *
 * @brief A model of a bouncy balloon, which bounces around the
 *        screen. A bouncy balloon has a location and a movement
 *        offset that is added to the location every frame.
 * 
 *        Once a balloon bounces a certain number of times, it will
 *        explode, removing the last barrier it bounced off of.
 *
 */
public class BouncyBalloon {
    /**
     * The current location of this bouncy balloon
     */
    private Point center;
	
    /**
     * The direction this balloon is moving.
     */
    private int movementX, movementY;
	
    /**
     * The number of bounces left until this balloon explodes.
     */
    private int bouncesLeft;
	
    /**
     * The number of frames this balloon has been exploding.
     */
    private int explosionCount;

    /**
     * Constructor initializes the data members.
     */
    public BouncyBalloon(Point center,
                         int movementX,
                         int movementY,
                         int bounces) {
        this.center = center;
        this.movementX = movementX;
        this.movementY = movementY;
        this.bouncesLeft = bounces;
    }
	
    // Getters and Setters
    public Point getCenter() {
        return center;
    }

    public void setLocation(Point center) {
        this.center = center;
    }

    public int getMovementX() {
        return movementX;
    }

    public void setMovementX(int movementX) {
        this.movementX = movementX;
    }

    public int getMovementY() {
        return movementY;
    }

    public void setMovementY(int movementY) {
        this.movementY = movementY;
    }

    public int getBouncesLeft() {
        return bouncesLeft;
    }

    public void setBouncesLeft(int bouncesLeft) {
        this.bouncesLeft = bouncesLeft;
    }
	
    public int getExplosionCount() {
        return explosionCount;
    }

    public void incrementExplosionCount() {
        ++explosionCount;
    }
	
    /**
     * Moves this bouncy balloon according to its defined motion.
     */
    public void move() {
        center.x += movementX;
        center.y += movementY;
    }
	
    /**
     * Changes this balloon's motion as if it bounced off a vertical
     * wall.  Decrements bouncesLeft.
     */
    public void bounceX() {
        movementX = -movementX;
        --bouncesLeft;
    }
	
    /**
     * Changes this balloon's motion as if it bounced off a horizontal
     * wall.
     */
    public void bounceY() {
        movementY = -movementY;
        --bouncesLeft;
    }
}
