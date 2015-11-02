package edu.vandy.model.aidl;

import java.util.Random;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Provides an interface for gazing into a Palantir.  Plays the role
 * of a "command" in the Command pattern.
 */
public class Palantir 
       implements Parcelable {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        Palantir.class.getSimpleName();

    /**
     * The value used to identify the Palantir.
     */
    private final int mId;

    /**
     * The generation count of the Palantir (i.e., how many times it's
     * been acquired).
     */
    private int mGenerationCount;

    /**
     * Create a new Random number generators.
     */
    private final Random mRandom;

    /**
     * Constructor initializes the fields.
     */
    public Palantir(int id,
                    Random random) {
        mId = id;
        mGenerationCount = 0;
        mRandom = random;
    }

    /**
     * Create a random gaze time between 1 and 5 milliseconds.
     *
     * @return true if gaze() returns normally, false if it is
     * interrupted or some other exception occurs.
     */
    public boolean gaze() {
        int sleepTime = 0;
        try {
            sleepTime = mRandom.nextInt(4000) + 1000;
            Thread.sleep(sleepTime);
            /*
            Log.d(TAG,
                  "gazing for "
                  + sleepTime 
                  + " msecs finished normally for Palantir "
                  + getId()
                  + " in Thread " 
                  + Thread.currentThread().getId());
            */
            return true;
        } catch (InterruptedException e) {
            /*
            Log.d(TAG,
                  "gazing for "
                  + sleepTime
                  + " msecs was interrupted for Palantir "
                  + getId()
                  + " in Thread " 
                  + Thread.currentThread().getId());
            */
            return false;
        } catch (Exception e) {
            Log.d(TAG,
                  "Some Exception caught in gaze() for Palantir "
                  + getId()
                  + " in Thread " 
                  + Thread.currentThread().getId());
            return false;
        }
    }

    /**
     * Return the id of the Palantir.
     */
    public int getId() {
        return mId;
    }

    /**
     * Return the generation count of the Palantir.
     */
    public int getGenerationCount() {
        return mGenerationCount;
    }

    /**
     * Increment the generation count of the Palantir by one.
     */
    public void incrementGenerationCount() {
        ++mGenerationCount;
    }

    /**
     * Returns true if @a this is equal to @a other.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Palantir))
            return false;
        final Palantir that = (Palantir) other;
        return this.mId == that.mId; 
    }

    /**
     * Returns the hashcode for the Palantir, which is simply its id.
     */
    @Override
    public int hashCode() {
        return mId;
    }

    /*
     * BELOW THIS is related to Parcelable Interface.
     */

    /**
     * A bitmask indicating the set of special object types marshaled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write this instance out to byte contiguous memory.
     */
    @Override
    public void writeToParcel(Parcel dest,
                              int flags) {
        dest.writeInt(mId);
        dest.writeInt(mGenerationCount);
    }

    /**
     * Private constructor provided for the CREATOR interface, which
     * is used to de-marshal an Palantir from the Parcel of data.
     * <p>
     * The order of reading in variables HAS TO MATCH the order in
     * writeToParcel(Parcel, int)
     *
     * @param in
     */
    private Palantir(Parcel in) {
        mId = in.readInt();
        mGenerationCount = in.readInt();
        mRandom = new Random();
    }

    /**
     * public Parcelable.Creator for Palantir, which is an
     * interface that must be implemented and provided as a public
     * CREATOR field that generates instances of your Parcelable class
     * from a Parcel.
     */
    public static final Parcelable.Creator<Palantir> CREATOR =
        new Parcelable.Creator<Palantir>() {
        public Palantir createFromParcel(Parcel in) {
            return new Palantir(in);
        }

        public Palantir[] newArray(int size) {
            return new Palantir[size];
        }
    };
}
