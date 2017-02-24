package edu.vandy.PalantiriManager;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * This class checks whether the FairSemaphore implementation is
 * indeed "fair", i.e., implements strict FIFO ordering.
 */
public class FairnessChecker {
    /**
     * Debugging tag used by the Android logger.
     */
    protected final static String TAG = 
        FairnessChecker.class.getSimpleName();
    
    /**
     * List of the waiting threads, which are stored in FIFO order to
     * see if the FairSemaphore implementation is "fair".
     */
    private final List<Long> mFairnessQueue;

    /**
     * Initialize the FairnessChecker.
     */
    public FairnessChecker(final int totalEntries) {
        mFairnessQueue = new ArrayList<>(totalEntries);
    }

    /**
     * Add the Id of the current thread that's about to wait to
     * acquire a semaphore.
     */
    public void addCurrentThread() {
        long tid = Thread.currentThread().getId();
        synchronized (this) {
            /*
              Log.d(TAG,
              "Adding thread " 
              + Thread.currentThread().getId()
              + " to the fairness queue: ");
              print();
            */
            System.out.println("Adding thread " 
                               + tid
                               + " to the end of the following fairness queue: ");
            print();

            // Add the current thread id to the fairness queue in a
            // thread-safe manner.
            mFairnessQueue.add(tid);
        }
    }

    /**
     * Add the Id of a given thread that's about to wait to acquire a
     * semaphore.
     */
    public void addNewThread(long threadId) {
        long tid = threadId;
        synchronized (this) {
            /*
              Log.d(TAG,
              "Adding thread " 
              + Thread.currentThread().getId()
              + " to the fairness queue: ");
              print();
            */
            System.out.println("Adding thread " 
                               + tid
                               + " to the end of the following fairness queue: ");
            print();

            // Add the current thread id to the fairness queue in a
            // thread-safe manner.
            mFairnessQueue.add(tid);
        }
    }

    /**
     * Remove the id of a thread, e.g., if an InterruptedException
     * occurs.
     */
    public void removeCurrentThread() {
        // Remove the current thread id from the fairness queue in a
        // thread-safe manner.
        long tid = Thread.currentThread().getId();
        synchronized (this) {
            mFairnessQueue.remove(tid);
            /*
              Log.d(TAG,
              "Removing thread " 
              + Thread.currentThread().getId()
              + " from the fairness queue: ");
              print();
            */
            System.out.println("Removing thread " 
                               + Thread.currentThread().getId()
                               + " from the updated fairness queue: ");
            print();
        }
    }

    /**
     * Returns true if the current thread's id is the same as the
     * first thread id in the list, else false.
     */
    public boolean isFifoOrder() {
        // Check to see if the thread id removed from the front of the
        // queue matches the current thread id in a thread-safe
        // manner.
        long tid = Thread.currentThread().getId();
        synchronized (this) {
            long firstThreadId = mFairnessQueue.remove(0);
            boolean result = tid == firstThreadId;
            /*
              if (!result) {
              Log.d(TAG,
              "Expected Thread id " 
              + Thread.currentThread().getId()
              + " but got Thread id "
              + firstThreadId
              + " with remaining fairness queue ");
              print();
              }
            */
            if (!result) {
                System.out.println("Expected Thread id " 
                                   + Thread.currentThread().getId()
                                   + " but got Thread id "
                                   + firstThreadId
                                   + " with remaining fairness queue ");
                print();
            }
            return result;
        }
    }

    /**
     * Display the contents of mFairnessQueue.
     */
    private void print() {
        String contents = "";

        // Append the contents of the fairness queue.
        for (Long i : mFairnessQueue)
            contents += i + ", ";

        // Uncomment one of the following, depending on whether you're
        // debugging in the Android or Java contexts.

        if (!contents.isEmpty())
            System.out.println(contents);
        /*
        Log.d(TAG, contents);
        */
    }

    /**
     * Reset the entries in the FairnessChecker.
     */
    public void reset() {
        mFairnessQueue.clear();
    }
}

