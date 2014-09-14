import java.util.ArrayList;
import java.util.List;

/**
 * @class BuggyQueue
 *
 * @brief This class (intentially) doesn't work properly when accessed
 *        via multiple threads since it's not synchronized properly.
 */
public class BuggyQueue {
    // Resizable-array implementation.
    private List<String> mQ = new ArrayList<String>();

    // Insert msg at the tail of the queue.
    public void put(String msg){ mQ.add(msg); }

    // Remove msg from the head of the queue.
    public String take(){ return mQ.remove(0); }
}

