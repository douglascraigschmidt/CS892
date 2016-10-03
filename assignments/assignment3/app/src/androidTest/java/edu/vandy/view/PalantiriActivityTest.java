package edu.vandy.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.vandy.R;
import edu.vandy.common.Toaster;
import edu.vandy.common.Utils;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class PalantiriActivityTest {
    /**
     * Logging tag.
     */
    private static final String TAG = "PalantiriActivityTest";

    /**
     * Wait time constants.
     */
    private final int CONFIG_TIMEOUT = 4000;
    private final int SHUTDOWN_TIMEOUT = 6000;

    /**
     * Input values.
     */
    private final int PALANTIRI = 6;
    private final int BEINGS = 4;
    private final int ITERATIONS = 5;

    @Rule
    public ActivityTestRule<PalantiriActivity> activityTestRule =
            new ActivityTestRule<>(PalantiriActivity.class);

    @Test
    public void palantiriActivityTest() {
        // Create and install a mock Toaster implementation.
        MockToaster mockToaster = new MockToaster();
        Utils.setMockToaster(mockToaster);

        setOrientationPortrait(CONFIG_TIMEOUT);

        ViewInteraction startButton = onView(
                allOf(withId(R.id.button_simulation),
                      withText("Start Simulation"), isDisplayed()));

        ViewInteraction stopButton = onView(
                allOf(withId(R.id.button_simulation),
                      withText("Stop Simulation"), isDisplayed()));

        List<Pair<Integer, Integer>> pairs =
                Stream.of(
                        Pair.create(R.id.edittext_number_of_palantiri,
                                    PALANTIRI),
                        Pair.create(R.id.edittext_number_of_beings, BEINGS),
                        Pair.create(R.id.edittext_gazing_iterations, ITERATIONS)
                ).collect(Collectors.toList());

        pairs.stream().forEach(
                p -> onView(withId(p.first)).perform(
                        typeText(p.second.toString())));

        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));

        setOrientationLandscape(CONFIG_TIMEOUT);

        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));

        startButton.perform(click());

        setOrientationLandscape(0);

        Assert.assertTrue(mockToaster.hasAnyMessageStartingWith(
                R.string.toast_simulation_resume, CONFIG_TIMEOUT));

        stopButton.perform(click());

        Assert.assertTrue(
                mockToaster.hasAnyMessage(
                        String.format(activityTestRule.getActivity().getString(
                                R.string.toast_simulation_stopped, BEINGS),
                                      SHUTDOWN_TIMEOUT)));

        // It can take a few seconds for the Being runnables to complete the
        // shutdown sequence so allow 5 seconds to receive the completion
        // message.
        Assert.assertTrue(mockToaster.hasAnyMessageStartingWith(
                R.string.toast_simulation_complete, SHUTDOWN_TIMEOUT));

        mockToaster.clear();

        startButton.perform(click());

        setOrientationPortrait(0);

        // should be resuming the simulation from before the config change.
        Assert.assertTrue(mockToaster.hasAnyMessageStartingWith(
                R.string.toast_simulation_resume, CONFIG_TIMEOUT));

        stopButton.perform(click());

        Assert.assertTrue(
                mockToaster.hasAnyMessage(
                        String.format(activityTestRule.getActivity().getString(
                                R.string.toast_simulation_stopped, BEINGS),
                                      SHUTDOWN_TIMEOUT)));

        mockToaster.clear();

        setOrientationLandscape(CONFIG_TIMEOUT);

        pressBack();

        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));
    }

    public void setOrientationLandscape(int wait) {
        Log.d(TAG, "palantiriActivityTest: setting orientation to LANDSCAPE");
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, wait);
    }

    public void setOrientationPortrait(int wait) {
        Log.d(TAG, "palantiriActivityTest: setting orientation to PORTRAIT");
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, wait);
    }

    public void setOrientation(int orientation, int wait) {
        try {
            getCurrentActivity().setRequestedOrientation(orientation);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // Give the system app to settle.
        SystemClock.sleep(wait);
    }

    private Activity getCurrentActivity() throws Throwable {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        getInstrumentation().runOnMainSync(() -> {
            java.util.Collection<Activity> activities =
                    ActivityLifecycleMonitorRegistry
                            .getInstance().getActivitiesInStage(Stage.RESUMED);
            activity[0] = Iterables.getOnlyElement(activities);
        });
        return activity[0];
    }

    private class MockToaster implements Toaster {
        /**
         * Default sleep interval used while repeatedly checking for a toast
         * message.
         */
        private static final int WAIT_INTERVAL = 100;

        /**
         * List of toast messages received from the application since the the
         * last clear() operation.
         */
        final ArrayList<String> mMessages = new ArrayList<>();

        /**
         * Mock implementation simply adds passed toast message to an array.
         */
        @Override
        public void showToast(
                Context context, String message, int duration) {
            synchronized (mMessages) {
                mMessages.add(message);
            }
            Toast.makeText(context, message, duration).show();
        }

        /**
         * Returns true if the first and only received toast messages matches
         * the passed message string within the specified time frame.
         */
        boolean hasJustMessage(@StringRes int id, int waitTime) {
            return hasJustMessage(
                    activityTestRule.getActivity().getString(id), waitTime);
        }

        /**
         * Returns true if the first and only received toast messages matches
         * the passed message string within the specified time frame.
         */
        boolean hasJustMessage(String message, int waitTime) {
            do {
                synchronized (mMessages) {
                    if (mMessages.size() > 1) {
                        return false;
                    } else if (mMessages.size() == 1) {
                        return mMessages.contains(message);
                    }
                }
                int sleepTime = Math.min(WAIT_INTERVAL, waitTime);
                SystemClock.sleep(sleepTime);
                waitTime -= sleepTime;
            } while (waitTime >= 0);

            return false;
        }

        /**
         * Returns true if the specified string exactly matches any posted toast
         * messages. Non-matching toast messages that may also be received
         * before or after the expected message.
         */
        boolean hasAnyMessage(@StringRes int id, int waitTime) {
            return hasAnyMessage(
                    activityTestRule.getActivity().getString(id),
                    waitTime);
        }

        /**
         * Returns true if the specified string exactly matches any posted toast
         * messages within the specified wait time. Ignores any additional
         * non-matching toast messages that may also be received before or after
         * the expected message.
         */
        boolean hasAnyMessage(String message, int waitTime) {
            while (waitTime >= 0) {
                synchronized (mMessages) {
                    if (hasAnyMessage(message)) {
                        return true;
                    }
                }
                int sleepTime = Math.min(WAIT_INTERVAL, waitTime);
                SystemClock.sleep(sleepTime);
                waitTime -= sleepTime;
            }
            return false;
        }

        /**
         * Returns true if the specified string has been displayed as a toast
         * message. Ignores any additional non-matching toast messages that may
         * also be received before.or after the expected message.
         */
        boolean hasAnyMessage(String message) {
            synchronized (mMessages) {
                for (String msg : mMessages) {
                    if (msg.equals(message)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Returns true if the specified string resource matches an posted toast
         * message withing the specified wait time. Ignores any additional
         * non-matching toast messages that may also be received before or after
         * the expected message.
         */
        boolean hasAnyMessageStartingWith(@StringRes int id, int waitTime) {
            return hasAnyMessageStartingWith(
                    activityTestRule.getActivity().getString(id),
                    waitTime);
        }

        /**
         * Returns true if the specified string matches an posted toast message
         * withing the specified wait time. Ignores any additional non-matching
         * toast messages that may also be received before or after the expected
         * message.
         */
        boolean hasAnyMessageStartingWith(String message, int waitTime) {
            while (waitTime >= 0) {
                synchronized (mMessages) {
                    if (hasAnyMessageStartingWith(message)) {
                        return true;
                    }
                }
                int sleepTime = Math.min(WAIT_INTERVAL, waitTime);
                SystemClock.sleep(sleepTime);
                waitTime -= sleepTime;
            }
            return false;
        }

        /**
         * Returns true if the specified string has already been posted. Ignores
         * any additional non-matching toast messages that may also be received
         * before or after the expected message.
         */
        boolean hasAnyMessageStartingWith(String message) {
            synchronized (mMessages) {
                for (String msg : mMessages) {
                    if (msg.startsWith(message)) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Clears any messages accumulated in the message array.
         */
        void clear() {
            synchronized (mMessages) {
                mMessages.clear();
            }
        }
    }
}
