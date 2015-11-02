package edu.vandy.model.aidl;

import java.util.List;
import edu.vandy.model.aidl.Palantir;
import edu.vandy.model.aidl.LeaseCallback;

/**
 * Interface defining the methods the PalantiriService implements to
 * provide synchronous access to the PalantiriManager.
 */
interface PalantiriManager {
    /**
     * Create a resource manager that contains the designated number
     * of Palantir with random gaze times between 1 and 5 milliseconds
     * "Fair" semantics should be used to instantiate the Semaphore.
     *
     * @param palantiriCount
     *            The number of Palantiri to add to the PalantiriManager.
     */
    // TODO -- you fill in here.

    /**
     * Get the next available Palantir from the resource pool,
     * blocking until one is available.
     *
     * @param leaseDurationInMillis
     *            The amount of time the lease can be held, in milliseconds.
     * @param leaseCallback
     *            The object to callback if the lease expires.
     */
    // TODO -- you fill in here.

    /**
     * Releases the designated @code palantir so it's available
     * for other Beings to use.
     */
    // TODO -- you fill in here.

    /**
     * Returns the amount of time (in milliseconds) remaining on
     * the lease held on the @a resource.
     */
    // TODO -- you fill in here.
}

