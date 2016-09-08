package edu.vandy.view;

import android.content.pm.ActivityInfo;
import android.os.SystemClock;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.vandy.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class PalantiriActivityTest {

    @Rule
    public ActivityTestRule<PalantiriActivity> activityTestRule =
            new ActivityTestRule<>(PalantiriActivity.class);

    @Test
    public void palantiriActivityTest() {
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

        stopButton.perform(click());

        onView(withText(
                startsWith("Exception was thrown or stop button was pressed")))
                .inRoot(withDecorView(not(activityTestRule.getActivity()
                                                  .getWindow()
                                                  .getDecorView())))
                .check(matches(isDisplayed()));

        SystemClock.sleep(2000);
        startButton.perform(click());

        setOrientationPortrait(2000);
        // setOrientationPortrait(4000);

        stopButton.perform(click());

        onView(withText(
                startsWith("Exception was thrown or stop button was pressed")))
                .inRoot(withDecorView(not(activityTestRule.getActivity()
                                                  .getWindow()
                                                  .getDecorView())))
                .check(matches(isDisplayed()));

        pressBack();

        pairs.stream().forEach(
                p -> onView(withId(p.first)).check(
                        matches(withText(
                                p.second.toString()))));
    }

    public void setOrientationLandscape(int wait) {
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, wait);
    }

    public void setOrientationPortrait(int wait) {
        setOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, wait);
    }

    public void setOrientation(int orientation, int wait) {
        activityTestRule.getActivity().setRequestedOrientation(orientation);

        // Give the system app to settle.
        SystemClock.sleep(wait);
    }
}
