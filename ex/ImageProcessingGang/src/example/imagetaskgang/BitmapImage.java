package example.imagetaskgang;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @class BitmapImage
 *
 * @brief Encapsulates the Android Bitmap class via a
 *        platform-independent interface.
 */
class BitmapImage implements Image {
    /**
     * An Android Bitmap object.
     */
    public Bitmap mBitmap;

    /**
     * Constructor that converts @a imageData into an Android Bitmap.
     */
    public BitmapImage(byte[] imageData) {
        mBitmap = BitmapFactory.decodeByteArray(imageData,
                                                0,
                                                imageData.length);
    }

    /**
     * Constructor that stores the @a bitmap parameter into the data
     * member.
     */
    public BitmapImage (Bitmap bitmap) {
    	mBitmap = bitmap;
    }
}
