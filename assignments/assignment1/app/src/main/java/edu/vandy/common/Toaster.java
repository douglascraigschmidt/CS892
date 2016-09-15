package edu.vandy.common;

import android.content.Context;

/**
 * An interface used to support mocking toast calls for Espresso testing.
 */
public interface Toaster {
    void showToast(Context context, String message, int duration);
}
