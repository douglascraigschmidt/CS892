package edu.vanderbilt.a4_android.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.vanderbilt.a4_android.R;
import edu.vanderbilt.a4_android.bouncy.BarrierManager;
import edu.vanderbilt.a4_android.bouncy.BarrierManagerStrategy;
import edu.vanderbilt.a4_android.bouncy.BarrierManagerStrategy.Strategy;
import edu.vanderbilt.a4_android.bouncy.BouncyBalloonRunnable;
import edu.vanderbilt.a4_android.bouncy.BouncyBarrier;
import edu.vanderbilt.a4_android.bouncy.BouncyBalloon;
import edu.vanderbilt.a4_android.bouncy.Point;

/**
 * @class MainActivity
 * 
 * @brief The main activity of the application. Displays the bouncy balloon
 *        board to the user, along with buttons to add bouncy balloons and clear
 *        the board. The user can also set the durability of barriers that are
 *        created when the user touches the bouncy area.
 */
public class MainActivity extends Activity {
    /**
     * The root layout of our activity.
     */
    LinearLayout mLayout;

    /**
     * Where the user inputs how many times a balloon should bounce before
     * exploding.
     */
    EditText mBouncesEditText;

    /**
     * The list of bouncy balloons currently on screen.
     */
    List<BouncyBalloon> mBalloons;

    /**
     * The executor service that will handle updating our balloons.
     */
    ExecutorService mExecutor;

    /**
     * A barrier manager to keep track of the barriers.
     */
    BarrierManager mManager;

    /**
     * The view that shows the balloons and the barriers.
     */
    BouncyBalloonArea mBalloonArea;

    /**
     * Used to generate random balloon directions.
     */
    Random mRand;

    /**
     * Hook method called when the Activity is launched.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the views from the framework
        mLayout = (LinearLayout) findViewById(R.id.layout);
        mBouncesEditText = (EditText) findViewById(R.id.bounces_edit_text);

        // Create the arraylist that will hold our bouncy balloons.
        mBalloons = new ArrayList<BouncyBalloon>();

        // Instantiate the manager
        mManager = new BarrierManagerStrategy(Options.MANAGER_STRATEGY);

        // Create the bouncy balloon area view.
        mBalloonArea = new BouncyBalloonArea(this, mBalloons);
        mManager.setObserver(mBalloonArea);

        // Set the layout params appropriately.
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                                                         LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);

        // Add the area to the linear layout.
        mLayout.addView(mBalloonArea, 0, params);

        // Add the click listener to the balloon area. Create a
        // barrier wherever they touch.
        mBalloonArea.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    // Cast to int because we don't need that level of precision.
                    int x = (int) event.getX(), y = (int) event.getY();

                    BouncyBarrier barrier = new BouncyBarrier(new Point(x, y),
                                                              false);

                    mManager.addBarrier(barrier);

                    return false;
                }
            });

        // Seed the random generator.
        mRand = new Random();
    }

    /**
     * Called when the user clicks the "Add Balloon" button.
     */
    public void addBalloon(View v) {
        synchronized (mBalloons) {
            // Get the number of bounces that the new balloon will
            // take to explode
            int bounces = Integer.parseInt(mBouncesEditText.getText()
                                           .toString());

            if (bounces <= 0) {
                Toast.makeText(this, "Please enter a number greater than 0",
                               Toast.LENGTH_SHORT).show();
                return;
            }

            // If this is the first balloon.
            if (mBalloons.isEmpty()) {
                // Instantiate a new CachedThreadPool ExecutorService to manage
                // our BouncyBalloons for this round.

                // TODO - You fill in here
                mExecutor = null; 

                // Add two invincible barriers to keep balloons
                // on-screen.
                BouncyBarrier topLeft = new BouncyBarrier(new Point(0, 0), true);
                BouncyBarrier bottomRight = new BouncyBarrier(new Point(
                                                                        mBalloonArea.getWidth(), mBalloonArea.getHeight()),
                                                              true);

                mManager.addBarrier(topLeft);
                mManager.addBarrier(bottomRight);

                // Note: we put this block here because the width and
                // height of mBalloonArea is not ready if we try to to
                // do it in onCreate()

            }

            // Generate a random x and y motion between -10 and 10
            int moveX = mRand.nextInt(21) - 10;
            int moveY = mRand.nextInt(21) - 10;

            // Create a point in the middle of the area
            Point p = new Point(mBalloonArea.getWidth() / 2,
                                mBalloonArea.getHeight() / 2);

            // Create a balloon and add it to the list of balloons.
            BouncyBalloon balloon = new BouncyBalloon(p, moveX, moveY, bounces);

            mBalloons.add(balloon);

            // Create a runnable to move and manage our balloon
            // TODO - You fill in here
            BouncyBalloonRunnable runnable = null;

            // Post the runnable to the executor.
            // TODO - You fill in here
        }
    }

    /**
     * Called when the user clicks the "Clear" button.
     */
    public void clear(View v) {

        // Interrupt all the bouncy balloon threads.
        // TODO - You fill in here
		
        // Clear all the lists.
        synchronized (mBalloons) {
            mBalloons.clear();
        }

        mManager.clear();
    }

    /**
     * Called when the user presses the "Bounces Left" button.
     */
    public void bouncesLeft(View v) {
        new BouncesLeftTask().execute();
    }

    /**
     * @class BouncesLeftTask
     *
     * @brief An asynchronous task that counts how many bounces are left until
     *        all balloons explode in the background. It then posts the results
     *        as a Toast.
     */
    // TODO - You fill in here to fix this class definition appropriately.
    private class BouncesLeftTask extends AsyncTask<Void, Void, Void> {
        // TODO - You fill in here.

        // Note: When iterating over mBalloons, you should use its
        // built in monitor object to synchronize on it, because other
        // parts of the application modify it concurrently (like
        // BouncyBalloonArena). Other than that, you don't need to do any
        // special synchronization.

    }

    /**
     * If the user closes our app, we definitely don't want to run in the
     * background.
     */
    @Override
    protected void onPause() {
        super.onPause();
        // Stop the balloons/barriers
        clear(mBalloonArea);

        // Stop redrawing the balloon area
        mBalloonArea.stopDrawing();
    }

    /**
     * When we come into view, start drawing the balloon area again.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mBalloonArea.startDrawing();
    }
}
