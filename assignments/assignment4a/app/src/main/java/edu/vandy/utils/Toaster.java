package edu.vandy.utils;

import android.content.Context;

/**
 * An interface used to support mocking toast calls for Espresso
 * testing.
 */
public interface Toaster {
    /**
     * Show a toast with the given @a message for the given @a duration.
     */
    void showToast(Context context, String message, int duration);
}
