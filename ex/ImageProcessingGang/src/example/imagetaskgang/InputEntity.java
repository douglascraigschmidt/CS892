package example.imagetaskgang;

import java.net.URL;

/**
 * @class InputEntity
 *
 * @brief An abstract super class that defines the base API for
 *        decoding raw bytes of data obtained from some input source
 *        into some result that can be processed by the rest of the
 *        application.
 *
 * @@ Nolan, please make sure this explanation is correct!
 */
public abstract class InputEntity {
    /**
     * The source URL from which the result was downloaded.
     */
    protected URL mSourceUrl;

    /**
     * The name of the filter that was applied to this result.
     */
    protected String mFilterName;
    
    /**
     * Keeps track of whether operations on this InputEntity succeed.
     * @@ Nolan, we need to improve this and use it consistently to
     * keep track of whether downloads and processing succeed.
     */
    protected boolean mSucceeded;

    /**
     * Constructs a null result.
     */
    public InputEntity() {
        mSourceUrl = null;
        mFilterName = null;
    }

    /**
     * Constructs a result with a @a sourceURL.
     */
    public InputEntity(URL sourceURL) {
        mSourceUrl = sourceURL;
        mFilterName = null;
    }

    /**
     * A factory method that subclasses override to convert raw data
     * obtained from some input source into the appropriate type of
     * result.
     */
    protected abstract InputEntity decodeBytesToResult(byte[] data);

    /**
     * Returns the source URL this result was constructed from.
     */
    public URL getSourceURL() {
        return mSourceUrl;
    }

    /**
     * Modifies the source URL of this result. Necessary for when the
     * result is constructed before it is associated with data.
     */
    public void setSourceURL(URL url) {
        mSourceUrl = url;
    }

    /**
     * Sets the name of the filter applied to this result.
     */
    public void setFilterName(Filter filter) {
        mFilterName = filter.getName();
    }

    /**
     * Returns the name of the filter applied to this result.
     */
    public String getFilterName() {
        return mFilterName;
    }

    /**
     * Returns true if operations on the InputEntity succeeded, else
     * false.
     */
    public boolean succeeded() {
        return mSucceeded;
    }
}
