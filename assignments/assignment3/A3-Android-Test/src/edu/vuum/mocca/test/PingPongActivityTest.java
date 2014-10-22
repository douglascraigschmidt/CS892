package edu.vuum.mocca.test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;
import android.view.WindowManager;

import com.robotium.solo.Solo;

import edu.vuum.mocca.PingPongActivity;

/**
 * @class PingPongActivityTest
 * 
 * @brief Implements a Robotium test for the PingPongActivity.
 */
public class PingPongActivityTest 
    extends ActivityInstrumentationTestCase2<PingPongActivity> {
    /**
     * Constructor initializes the superclass.
     */
    public PingPongActivityTest() {
        super(PingPongActivity.class);
    }

    /**
     * This is the handle for Robotium, which allows us to interact with the UI.
     */
    Solo mSolo;

    /**
     * The context of this project, not the target project.
     */
    Context mContext;

    Button playButton_;
    TextView outputTextView_;

    /**
     * Called before each test is run to perform the initialization.
     */
    public void setUp() throws Exception {
        super.setUp();

        // Setup Robotium and get the EditText View.
        mSolo = new Solo(getInstrumentation(), getActivity());

        mContext = getInstrumentation().getContext();

        playButton_ = (Button) mSolo.getView(edu.vuum.mocca.R.id.play_button);

        outputTextView_ = (TextView) mSolo
            .getView(edu.vuum.mocca.R.id.pingpong_text_output);

        // Prevent lockscreen from preventing test.
        getInstrumentation().runOnMainSync(new Runnable() {
                @Override
                    public void run() {
                    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                }
            });

        getInstrumentation().callActivityOnStart(getActivity());
        getInstrumentation().callActivityOnResume(getActivity());
    }

    /**
     * No need for @Test because of use of ActivityInstrumentationTestCase2
     * which makes each method in this class a @Test method TODO find better
     * explanation, 100% proper.
     */
    public void testPlayPingPongButtonPress() throws Exception {
        Thread.sleep(TestOptions.ACCEPTABLE_STARTUP_LENGTH);

        // this is added in API lvl 19
        // Asserttrue(outputTextView_.isAttachedToWindow() == true);
        assertEquals(outputTextView_.getText().length(), 0);

        // Click on the 'play' button
        mSolo.clickOnView(mSolo.getView(edu.vuum.mocca.R.id.play_button));

        // wait for the threads to execute
        Thread.sleep(TestOptions.ACCEPTABLE_RUNTIME_LENGTH);

        assertEquals(outputTextView_.getText().toString(),
                     TestOptions.ANDROID_TEXTVIEW);
    }
}
