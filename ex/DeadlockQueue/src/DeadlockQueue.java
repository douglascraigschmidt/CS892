import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/*
 * @class DeadlockQueue
 *
 * @brief Defines a buggy version of the BlockingQueue interface that
 *        illustrates how deadlock can occur due to "circular
 *        waiting".  Moreover, deadlocks occur sporatically, which
 *        makes it even harder to identify and diagnose the problem!
 */
class DeadlockQueue {
    /**
     * The queue consists of a List of Strings.
     */
    private List<String> mQ = new ArrayList<String>();

    /**
     * True if the queue is empty.
     */
    public Boolean isEmpty() {
        return mQ.size() == 0;
    }

    /**
     * Add a new String to the end of the queue.
     */
    public void put(String msg){
        mQ.add(msg);
    } 

    /**
     * Remove the String at the front of the queue.
     */
    public String take(){
        return mQ.remove(0);
    } 

    /**
     * Transfer the contents of src to dest.
     */
    public static void transfer(DeadlockQueue src,
                                DeadlockQueue dest){
        // Acquire the locks for src and dest.
        synchronized(src) {
            synchronized(dest) {
                // Remove each element from src and put it into dest.
                while(!src.isEmpty()) {
                    dest.put(src.take());
                }
            }
        }
    }
}






