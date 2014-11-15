package edu.vanderbilt.a4_android.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;
import edu.vanderbilt.a4_android.R;
import edu.vanderbilt.a4_android.bouncy.BarrierObserver;
import edu.vanderbilt.a4_android.bouncy.BouncyBarrier;
import edu.vanderbilt.a4_android.bouncy.BouncyBalloon;
import edu.vanderbilt.a4_android.bouncy.Point;

/**
 * @class BouncyBalloonArea
 *
 * @brief This is a custom view that paints a visual representation of
 *        the Bouncy Balloon grid.
 */
public class BouncyBalloonArea extends View implements BarrierObserver {
    /**
     * The lists of bouncy balloons and barriers we'll draw.
     */
    private List<BouncyBarrier> mBarriers;
    private List<BouncyBalloon> mBalloons;

    /**
     * The paint settings we'll use to draw them.
     */
    private Paint mBlackPaint, mWhitePaint;

    /**
     * The bitmap we'll use to draw explosions.
     */
    Bitmap mExplosion;

    /**
     * Used to invalidate ourselves every 33 ms.
     */
    private Thread mThread = null;

    /*
     * Constructs a BouncyBalloonArea. Note that the list of barriers
     * here should not be the same list that the BarrierManager uses
     * because this is not synchronized.
     */
    public BouncyBalloonArea(Context context,
                             List<BouncyBalloon> balloons) {
        super(context);

        mBalloons = balloons;
        mBarriers = new ArrayList<BouncyBarrier>();
        // Create the paint settings
        mBlackPaint = new Paint();
        mBlackPaint.setColor(Color.BLACK);
        mWhitePaint = new Paint();
        mWhitePaint.setColor(Color.WHITE);
        mWhitePaint.setTextSize(30);
        mWhitePaint.setTextAlign(Align.CENTER);

        // Get the bitmap from the drawable
        BitmapFactory.Options ops = new BitmapFactory.Options();
        mExplosion = BitmapFactory.decodeResource(context.getResources(),
                                                  R.drawable.explosion,
                                                  ops);
        mExplosion.setDensity(Bitmap.DENSITY_NONE);
    }

    /**
     * Invalidates this view as often as specified by the Options parameter. 
     */
    public void startDrawing() {
        mThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        BouncyBalloonArea.this.postInvalidate();
                        try {
                            Thread.sleep(Options.REFRESH_RATE);
                        } catch (InterruptedException e) {
                            // If we get interrupted, just quit the thread
                            return;
                        }
                    }
                }
            });
        mThread.start();
    }

    /**
     * Stops refreshing this view 
     */
    public void stopDrawing() {
        if (mThread != null)
            mThread.interrupt();

        mThread = null;
    }

    /**
     * Called each time the view is invalidated.
     */
    protected void onDraw(Canvas canvas) {
        int height = canvas.getHeight();
        int width = canvas.getWidth();

        synchronized (mBarriers) {
            for (BouncyBarrier b : mBarriers) {
                Point center = b.getCenter();
                // Draw the horizontal line
                canvas.drawLine(0, center.y, width, center.y, mBlackPaint);
                // Draw the vertical line.
                canvas.drawLine(center.x, 0, center.x, height, mBlackPaint);
            }
        }

        synchronized (mBalloons) {
            // Draw each balloon
            for (Iterator<BouncyBalloon> i = mBalloons.iterator(); i.hasNext();) {
                BouncyBalloon b = i.next();
                Point center = b.getCenter();
                int bouncesLeft = b.getBouncesLeft();
                // If it has bounces left, draw a balloon
                if (bouncesLeft > 0) {
                    canvas.drawCircle(center.x, center.y, 30, mBlackPaint);
                    canvas.drawText(String.valueOf(b.getBouncesLeft()),
                                    center.x, center.y, mWhitePaint);
                }
                // If it exploded, draw an explosion for 30 frames
                else if (b.getExplosionCount() < 30) {
                    canvas.drawBitmap(mExplosion, center.x - 30, center.y - 30,
                                      mBlackPaint);
                    b.incrementExplosionCount();
                }
                // If the explosion is over, remove it.
                else {
                    i.remove();
                }
            }
        }
    }

    /**
     * Called by a BarrierManager each time a barrier is added.
     */
    @Override
    public void onBarrierAdded(BouncyBarrier b) {
        synchronized (mBarriers) {
            mBarriers.add(b);
        }
    }

    /**
     * Called by a BarrierManager each time a barrier is removed.
     */
    @Override
    public void onBarrierRemoved(BouncyBarrier b) {
        synchronized (mBarriers) {
            mBarriers.remove(b);
        }
    }

}
