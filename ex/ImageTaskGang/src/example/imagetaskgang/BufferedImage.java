package example.imagetaskgang;

/**
 * @class BufferedImage
 *
 * @brief Encapsulates the Android Buffered class via a
 *        platform-independent interface.
 */
class BufferedImage implements Image {
    /**
     * A Java BufferedImage object.
     */
    // @@ Need to fix 
	//public java.awt.image.BufferedImage mBufferedImage;

    /**
     * Constructor that converts @a imageData into a Java @a BufferedImage.
     * @@ Nolan, we may need to change the type of the @imageData parameter..
     */
    public BufferedImage(byte[] imageData) {
        // mBufferedImage = null; // @@ Nolan, please fill in here.
    }

    /**
     * Constructor that stores the @a bufferedInmage parameter into
     * the data member.
     */
    public BufferedImage (Object bufferedImage) {
    	// mBufferedImage = (java.awt.image.BufferedImage) bufferedImage;
    }
}
