package edu.vandy.model.aidl;

import java.util.List;
import edu.vandy.model.aidl.Palantir;

/**
 * Interface defining the method the PalantiriModel implements so
 * the PalantirService can inform it when a Lease expires.
 */
interface LeaseCallback {
    /**
     * Inform the PalantiriModel that the lease associated with @a
     * palantir has expired.  It's essential to define this method as
     * a oneway since otherwise it will block the service.
     *
     * @param palantir
     *            The Palantir that has expired.
     */
     // TODO -- you fill in here.
}
