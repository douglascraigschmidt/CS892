package edu.vandy.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import edu.vandy.common.LifecycleLoggingService;
import edu.vandy.model.aidl.LeaseCallback;
import edu.vandy.model.aidl.Palantir;
import edu.vandy.model.aidl.PalantiriManager;

/**
 * This Bound Service provides clients with an AIDL interface to a
 * PalantiriLeasePool that controls access to a fixed number of
 * available Palantiri.  AIDL is an example of the Broker Pattern, in
 * which all interprocess communication details are hidden behind the
 * AIDL interfaces.
 */
public class PalantiriService 
       extends LifecycleLoggingService {
    /**
     * LeasePool that controls the access of multiple Middle-Earth
     * Beings to a fixed number of available Palantiri.
     */
    private PalantiriLeasePool mPalantiriLeasePool;

    /**
     * Factory method that makes an explicit Intent used to start the
     * PalantiriService when passed to bindService().
     * 
     * @param context
     *            The context of the calling component.
     */
    public static Intent makeIntent(Context context) {
        return new Intent(context,
                          PalantiriService.class);
    }

    /**
     * Called when a client (e.g., PalantiriModel) calls bindService()
     * with the proper Intent.  Returns the implementation of
     * PalantiriLeasePool, which is implicitly cast as an IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mPalantiriLeasePoolImpl;
    }

    /**
     * The concrete implementation of the AIDL Interface
     * PalantiriManager, which extends the Stub class that implements
     * PalantiriLeasePool, thereby allowing Android to handle calls
     * across process boundaries.  This method runs in a separate
     * Thread as part of the Android Binder framework.
     * 
     * This implementation plays the role of Invoker in the Broker
     * Pattern.
     */
    private final PalantiriManager.Stub mPalantiriLeasePoolImpl =
        // TODO -- you fill in here, replacing "null" with the
        // appropriate call.
        null {
            /**
             * Create a resource manager that contains the designated
             * number of Palantir with random gaze times between 1 and
             * 5 milliseconds "Fair" semantics should be used to
             * instantiate the Semaphore.
             *
             * @param palantiriCount
             *            The number of Palantiri to add to the LeasePool.
             */
            @Override
            public void makePalantiri(int palantiriCount) {
                // Create a list to hold the generated Palantiri.
                final List<Palantir> palantiri =
                    new ArrayList<Palantir>();		

                // Create a new Random number generator.
                final Random random = new Random();

                // Create and add each new Palantir into the list.
                // The id of each Palantir is its position in the
                // list.
                for (int i = 0; i < palantiriCount; ++i) 
                    palantiri.add(new Palantir(i,
                                               random));

                // Create a LeasePool that is used to control
                // concurrent access to the Palantiri.
                mPalantiriLeasePool =
                    new PalantiriLeasePool(palantiri);
            }

            /**
             * Get the next available Palantir from the resource pool,
             * blocking until one is available.
             *
             * @param leaseDurationInMillis
             *            The amount of time the lease can be held, in milliseconds.
             * @param leaseCallback
             *            The object to callback if the lease expires.
             */
            @Override
            public Palantir acquire(long leaseDurationInMillis,
                                    LeaseCallback leaseCallback) {
                // TODO - You fill in here.
            }

            /**
             * Releases the designated @a palantir so it's available
             * for other Beings to use.  If @a palantir is null it is
             * ignored.
             */
            @Override
            public void release(Palantir palantir) {
                // TODO - You fill in here.
            }

            /**
             * Returns the amount of time (in milliseconds) remaining on the
             * lease held on the @a resource.
             */
            @Override
            public long remainingTime(Palantir palantir) {
                // TODO - You fill in here.
            }
        };
}
