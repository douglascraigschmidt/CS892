package edu.vandy.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

/**
 * @class Utils
 * @brief Helper methods shared by various Activities.
 */
public class UiUtils {
    /**
     * Debugging tag.
     */
    private static final String TAG =
            UiUtils.class.getCanonicalName();

    /**
     * A Singleton implementation of the Toaster interface (to support mocking
     * toast calls).
     */
    public static Toaster sToaster;

    /**
     * Ensure this class is only used as a utility.
     */
    private UiUtils() {
        throw new AssertionError();
    }

    /**
     * FAB animator that displays the FAB.
     * @param fab The FAB to be displayed
     */
    public static void showFab(FloatingActionButton fab) {
        fab.show();
        fab.animate()
           .translationY(0)
           .setInterpolator(new DecelerateInterpolator(2))
           .start();
    }

    /**
     * FAB animator that hides the FAB.
     * @param fab The FAB to be hidden
     */
    public static void hideFab (FloatingActionButton fab) {
        fab.hide();
        fab.animate()
           .translationY(fab.getHeight() + 100)
           .setInterpolator(new AccelerateInterpolator(2))
           .start();
    }

    /**
     * Reveals the EditText.
     * @param text EditText to be revealed
     */
    public static void revealEditText (EditText text) {
        // Get x and y positions of the view with a slight offset
        // to give the illusion of reveal happening from FAB.
        int cx = text.getRight() - 30;
        int cy = text.getBottom() - 60;

        // Radius gives the reveal the circular outline.
        int finalRadius = Math.max(text.getWidth(),
                text.getHeight());

        // This creates a circular reveal that is used starting from
        // cx and cy with a radius of 0 and then expanding to finalRadius.
        Animator anim =
                ViewAnimationUtils.createCircularReveal(text,
                        cx,
                        cy,
                        0,
                        finalRadius);
        text.setVisibility(View.VISIBLE);
        anim.start();
    }

    /**
     * Hides the EditText
     * @param text EditText to be hidden.
     * @param clear Whether to clear the contents of the @a text or not.
     */
    public static void hideEditText(final EditText text,
                                    boolean clear) {
        // Get x and y positions of the view with a slight offset
        // to give the illusion of reveal happening from FAB.
        int cx = text.getRight() - 30;
        int cy = text.getBottom() - 60;

        // Gets the initial radius for the circular reveal.
        int initialRadius = text.getWidth();

        // This creates a circular motion that appears to be going back into the
        // FAB from cx and cy with the initial radius as the width and final radius
        // as 0 since it is animating back into the FAB.
        Animator anim =
                ViewAnimationUtils.createCircularReveal(text,
                        cx,
                        cy,
                        initialRadius,
                        0);

        // Create a listener so that we can make the EditText
        // invisible once the circular animation is over.
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                text.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();

        // Clear the text from the EditText when the user touches the X FAB.
        if (clear)
            text.getText().clear();
    }

    /**
     * Pause the current thread for the given number of milliseconds.
     */
    public static void pauseThread(long millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
        }
    }

    /**
     * Show a toast message using a string format
     * and possible arguments.
     */
    public static void showToast(Context context,
                                 String message,
                                 Object... args) {
        if (sToaster == null) {
            sToaster = new ToasterImpl();
        }
        sToaster.showToast(context,
                           String.format(message, args),
                           Toast.LENGTH_SHORT);
    }

    /**
     * Show a toast message using a resource string format
     * and possible arguments.
     */
    public static void showToast(Context context,
                                 @StringRes int id,
                                 Object... args) {
        if (sToaster == null) {
            sToaster = new ToasterImpl();
        }
        sToaster.showToast(context,
                           context.getString(id, args),
                           Toast.LENGTH_SHORT);
    }

    /**
     * Sets the application toast implementation to use a mock. This method
     * should only be called from test classes.
     *
     * @param mockToaster A mock Toaster implementation.
     */
    public static void setMockToaster(Toaster mockToaster) {
        sToaster = mockToaster;
    }

    /**
     * An implementation of the Toaster interface that transparenty forwards
     * all toast messages to a Toast object.
     */
    public static class ToasterImpl implements Toaster {
        @Override
        public void showToast(Context context, String message, int duration) {
            Toast.makeText(context, message, duration).show();
        }
    }
}
