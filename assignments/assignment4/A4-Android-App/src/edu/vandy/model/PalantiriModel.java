package edu.vandy.model;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import edu.vandy.MVP;
import edu.vandy.common.GenericServiceConnection;
import edu.vandy.model.aidl.LeaseCallback;
import edu.vandy.model.aidl.Palantir;
import edu.vandy.model.aidl.PalantiriManager;
import edu.vandy.model.service.PalantiriService;
import edu.vandy.presenter.BeingThread;

/**
 * This class is a proxy that binds to the PalantiriService, which
 * manages access of multiple concurrent BeingThreads to a limited
 * number of Palantiri.
 *
 * This class plays the "Model" role in the Model-View-Presenter (MVP)
 * pattern by acting upon requests from the Presenter, i.e., it
 * implements the methods in MVP.ProvidedModelOps that acquire and
 * release Palantiri from the PalantiriManager running in the
 * PalantiriService and returns the Palantiri to the Presenter.
 */
public class PalantiriModel
       implements MVP.ProvidedModelOps {
    /**
     * Debugging tag used by the Android logger.
     */
    private final static String TAG = 
        PalantiriModel.class.getSimpleName();

    /**
     * WeakReference to the Presenter layer.
     */
    private WeakReference<MVP.RequiredPresenterOps> mPresenter;

    /**
     * This GenericServiceConnection is used to receive results after
     * binding to the PalantiriService via bindService().
     */
    // TODO - grad students must replace the use of the GenericService
    // connection with the conventional Android ServiceConnection and
    // update all parts of the code below accordingly.
    private GenericServiceConnection<PalantiriManager> mServiceConnection;

    /**
     * This class keeps track of the Thread that invokes an
     * acquirePalantir() method and the generation count for the
     * Palantir it acquires.  This class is used to ensure that
     * asynchronously invoked timeouts don't accidentally interrupt
     * the wrong BeingTasks.
     */
     private static class PalantirRecord {
        /**
         * The Thread that invokes an acquirePalantir() method.
         */
         public BeingThread mThread;
        
        /**
         * The generation count of the Palantir that's returned from
         * the PalantiriManager.
         */
        public final AtomicInteger mGenerationCount;
        
        /**
         * Constructor initializes the fields.
         */
        PalantirRecord(int generationCount) {
            mGenerationCount = new AtomicInteger(generationCount);
            mThread = (BeingThread) Thread.currentThread();
        }

        /**
         * A setter that initializes the fields.
         */
        void set(int generationCount) {
            mGenerationCount.set(generationCount);
            mThread = (BeingThread) Thread.currentThread();
        }
    }

    /**
     * Maps a Palantir Id to the PalantirRecord that's using it.
     */
    private ConcurrentHashMap<Integer, PalantirRecord> mPalantirRecordMap =
        new ConcurrentHashMap<>();

    /**
     * This callback is invoked by the PalantiriManager running in the
     * PalantiriService when a lease expires.
     */
    private LeaseCallback.Stub mLeaseCallback = 
        new LeaseCallback.Stub() {
            /**
             * Inform the PalantiriModel that the lease associated
             * with @a palantirId has expired.
             *
             * @param palantiri
             *            The Palantir whose lease has expired.
             */
            @Override
            public void leaseExpired(Palantir palantir) {
                // Get the Thread that's associated with this
                // palantirId.
                final PalantirRecord pr =
                    mPalantirRecordMap.get(palantir.getId());

                // Store the generation count in a local variable.
                final int generationCount = 
                    pr.mGenerationCount.get();

                // Check to see if the generation counts match.
                if (generationCount == palantir.getGenerationCount()) {
                    // Inform the BeingThread the lease has expired.
                    pr.mThread.leaseExpired();
                    /*
                    Log.d(TAG,
                          "the lease has expired for Palantir "
                          + palantir.getId() 
                          + " running in Thread " 
                          + pr.mThread.getId());
                    */
                } else
                    Log.d(TAG,
                          "generation count "
                          + generationCount
                          + " doesn't match "
                          + palantir.getGenerationCount()
                          + " for Palantir "
                          + palantir.getId());
            }
        };

    /**
     * Hook method called when a new instance of PalantiriModel is
     * created.  One-time initialization code goes here, e.g., storing
     * a WeakReference to the Presenter layer and binding to the
     * PalantiriService.
     * 
     * @param presenter
     *            A reference to the Presenter layer.
     */
    @Override
    public void onCreate(final MVP.RequiredPresenterOps presenter) {
        // Initialize the WeakReference.
        mPresenter = new WeakReference<>(presenter);

        // Initialize the GenericServiceConnection object.
        mServiceConnection = 
            new GenericServiceConnection<PalantiriManager>
                (PalantiriManager.class,
                 // Create a Runnable whose run() hook method starts
                 // the Presenter layer after the ServiceConnection is
                 // established.
                 new Runnable() {
                    @Override
                    public void run() {
                        // Start the Presenter layer.
                        presenter.start();
                    }
                });

        // Bind to the PalantiriService using the Intent returned from
        // its makeIntent() factory method.
        mPresenter.get()
                  .getApplicationContext()
                  .bindService(PalantiriService.makeIntent(presenter.getActivityContext()),
                               mServiceConnection,
                               Context.BIND_AUTO_CREATE);
    }

    /**
     * Hook method called to shutdown the Model layer.
     *
     * @param isChangeConfigurations
     *        True if a runtime configuration triggered the onDestroy() call.
     */
    @Override
    public void onDestroy(boolean isChangingConfigurations) {
        // Unbind from the PalantiriService.
        if (isChangingConfigurations == false)
            mPresenter.get()
                      .getApplicationContext()
                      .unbindService(mServiceConnection);
    }

    /**
     * Initialize the PalantiriManager in the PalantiriService, which
     * contains the designated number of Palantir with random gaze
     * times between 1 and 5 milliseconds "Fair" semantics should be
     * used to instantiate the Semaphore.
     *
     * @param palantiriCount
     *            The number of Palantiri to add to the
     *            PalantiriManager.  
     */
    @Override
    public void makePalantiri(int palantiriCount) {
        try {
            final PalantiriManager palantiriManager = 
                mServiceConnection.getInterface();

            if (palantiriManager != null)
                // Invoke a two-way AIDL call to initialize the
                // PalalantiriManager, which blocks the caller.
                // TODO - You fill in here.
                ;
            else 
                Log.d(TAG, "palantiriManager was null.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the next available Palantir from the resource pool,
     * blocking until one is available.
     */
    @Override
    public Palantir acquirePalantir(long leaseDurationInMillis) {
        try {
            final PalantiriManager palantiriManager = 
                mServiceConnection.getInterface();

            if (palantiriManager != null) {
                // Invoke a two-way AIDL call to acquire a Palantir,
                // which blocks the caller until it completes.
                // TODO - You fill in here, replacing "null" with the
                // proper code.
                final Palantir palantir = null;

                // Create or update a PalantirRecord with the
                // appropriate id of the acquired Palantir and the
                // generation count.  Then put record into the
                // mPalantirRecordMap using the palantir as the key.
                // TODO - You fill in here.

                /*
                Log.d(TAG, "Palantir "
                      + palantir.getId()
                      + " put in map for Thread "
                      + pr.mThread.getId()
                      + " with generation count "
                      + pr.mGenerationCount.get());
                */
                return palantir;
            } else 
                Log.d(TAG, "palantiriManager was null.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Releases the designated @code palantir so it's available for
     * other Beings to use.  If @palantir is null it is ignored.
     */
    @Override
    public void releasePalantir(final Palantir palantir) {
        try {
            final PalantiriManager palantiriManager = 
                mServiceConnection.getInterface();

            if (palantiriManager != null
                && palantir != null) {
                // Get the mapping between the Palantir that's being
                // released and its PalantirRecord and negate the
                // generation count to avoid future false lease
                // expirations.
                // TODO - You fill in here.

                // Invoke a two-way AIDL call to release the Palantir,
                // which blocks the caller.
                // TODO -- you fill in here.
            } else 
                Log.d(TAG,
                      "palantiriManager was null.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the amount of time (in milliseconds) remaining on the
     * lease held on the @a resource.
     */
    @Override
    public long remainingTime(Palantir palantir) {
        try {
            final PalantiriManager palantiriManager = 
                mServiceConnection.getInterface();

            if (palantiriManager != null) 
                // Invoke a two-way AIDL call to return the time
                // remaining on the Palantir lease, which blocks the
                // caller.
                // TODO - You fill in here, replacing return 0 with
                // the proper call.
                return 0;
            else 
                Log.d(TAG, "palantiriManager was null.");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

