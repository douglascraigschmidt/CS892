package edu.vandy.view;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.SystemClock;
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

    @Rule
    public ActivityTestRule<PalantiriActivity> activityTestRule =
            new ActivityTestRule<>(PalantiriActivity.class);

    @Test
    public void palantiriActivityTest() {
        // Create and install a mock Toaster implementation.
        MockToaster mockToaster = new MockToaster();
        Utils.setMockToaster(mockToaster);

        setOrientationPortrait(1000);

        ViewInteraction startButton = onView(
                allOf(withId(R.id.button_simulation),
                      withText("Start Simulation"), isDisplayed()));

        ViewInteraction stopButton = onView(
                allOf(withId(R.id.button_simulation),
                      withText("Stop Simulation"), isDisplayed()));

        List<Pair<Integer, Integer>> pairs =
                Stream.of(
                        Pair.create(R.id.edittext_number_of_palantiri, 4),
                        Pair.create(R.id.edittext_number_of_beings, 6),
                        Pair.create(R.id.edittext_gazing_iterations, 5)
                ).collect(Collectors.toList());

        pairs.stream().forEach(
                p -> onView(withId(p.first)).perform(
                        typeText(p.second.toString())));

        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));

        setOrientationLandscape(2000);

        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));

        startButton.perform(click());

        setOrientationLandscape(2000);

        SystemClock.sleep(4000);

        stopButton.perform(click());

        Assert.assertTrue(mockToaster.hasMessageStartingWith(
                "Exception was thrown or stop button was pressed"));
        mockToaster.clear();

        startButton.perform(click());

        setOrientationPortrait(2000);

        SystemClock.sleep(4000);

        stopButton.perform(click());

        Assert.assertTrue(mockToaster.hasMessageStartingWith(
                "Exception was thrown or stop button was pressed"));
        mockToaster.clear();

        setOrientationLandscape(2000);

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
            java.util.Collection<Activity> activites =
                    ActivityLifecycleMonitorRegistry
                            .getInstance().getActivitiesInStage(Stage.RESUMED);
            activity[0] = Iterables.getOnlyElement(activites);
        });
        return activity[0];
    }

    private class MockToaster implements Toaster {
        ArrayList<String> mMessages = new ArrayList<>();

        @Override
        public void showToast(
                Context context, String message, int duration) {
            mMessages.add(message);
            Toast.makeText(context, message, duration).show();
        }

        boolean hasMessageStartingWith(String message) {
            for (String msg : mMessages) {
                if (msg.startsWith(message)) {
                    return true;
                }
            }
            return false;
        }

        boolean hasMessage(String message) {
            return mMessages.contains(message);
        }

        void clear() {
            mMessages.clear();
        }
    }
}
