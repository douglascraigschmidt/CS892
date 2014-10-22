package edu.vuum.mocca;

import android.app.Activity;

/**
 * @name OutputTextViewInterface
 * 
 * @brief Interface that defines the print() and getSelf()
 *        methods.
 */
public interface PingPongOutputInterface {
    /**
     * Prints the output string to the text log on screen. If the
     * string contains "ping" (case-insensitive) then a large Ping!
     * will be shown on screen with a certain color. The same goes for
     * strings containing "pong".
     */
    void print(String output);

    /**
     * Accessor method that returns this Activity.
     */
    Activity getSelf();
}
