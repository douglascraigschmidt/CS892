package example.imagetaskgang;

import java.net.URL;

/**
 * @class InputEntity
 *
 * @brief An abstract super class that defines the base interface for
 *        decoding raw bytes of data obtained from some input source
 *        into some result that can be processed by the rest of the
 *        application. This class also stores meta-data, such as the
 *        URL where the image was downloaded, the name of the filter
 *        applied to process the entity, and whether operations on
 *        this entity succeeded or not.
 */
public abstract class InputEntity {
    /**
     * The source URL from which the entity was downloaded.
     */
    protected URL mSourceUrl;

    /**
     * The name of the filter that was applied to process this entity.
     */
    protected String mFilterName;
    
    /**
     * Keeps track of whether operations on this entity succeeded or
     * not.
     */
    protected boolean mSucceeded;

    /**
     * Constructs a null entity.
     */
    public InputEntity() {
        mSourceUrl = null;
        mFilterName = null;
        mSucceeded = true;
    }

    /**
     * Constructs an entity with a @a sourceURL.
     */
    public InputEntity(URL sourceURL) {
        mSourceUrl = sourceURL;
        mFilterName = null;
        mSucceeded = true;
    }

    /**
     * Returns the source URL this entity was constructed from.
     */
    public URL getSourceURL() {
        return mSourceUrl;
    }

    /**
     * Modifies the source URL of this entity. Necessary for when the
     * entity is constructed before it is associated with data.
     */
    public void setSourceURL(URL url) {
        mSourceUrl = url;
    }

    /**
     * Sets the name of the filter applied to this entity.
     */
    public void setFilterName(Filter filter) {
        mFilterName = filter.getName();
    }

    /**
     * Returns the name of the filter applied to this entity.
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
