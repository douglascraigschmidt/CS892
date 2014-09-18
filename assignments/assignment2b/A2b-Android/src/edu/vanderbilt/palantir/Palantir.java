package edu.vanderbilt.palantir;

/**
 * @class Palantir
 *
 * @brief Provides an interface for gazing into a Palantir.
 *        Essentially plays the role of a "command" in the Command
 *        pattern.
 */
public interface Palantir {
    /**
     * Gaze into the Palantir (and go into a trance ;-)).
     * @throws InterruptedException 
     */
    public void gaze() throws InterruptedException;

    /**
     * Return the id of the Palantir.
     */
    public int getId();
}

