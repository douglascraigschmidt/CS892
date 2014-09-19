import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/*
 * @class SimpleBlockingQueue
 *
 * @brief Defines an implementation of the BlockingQueue interface
 *        that works properly when accessed via multiple threads since
 *        it's synchronized properly.
 */
class SimpleBlockingQueue<E> implements BlockingQueue<E> {
    /**
     * The queue consists of a List of E's.
     */
    private List<E> mList = new ArrayList<E>();

    /**
     * True if the queue is empty.
     */
    public synchronized boolean isEmpty() {
        return mList.size() == 0;
    }

    /**
     * Add a new E to the end of the queue, blocking until there's room.
     */
    public synchronized void put(E msg) throws InterruptedException {
        mList.add(msg);
        notifyAll();
    } 

    /**
     * Remove the E at the front of the queue, blocking until there's
     * something in the queue.
     */
    public synchronized E take() throws InterruptedException {
        while (mList.isEmpty())
            wait();

        return mList.remove(0);
    } 

    /**
     * Returns the number of elements in this queue.
     */
    public synchronized int size() {
        return mList.size();
    }

    /**
     * All these methods are inherited from the BlockingQueue
     * interface. They are defined as no-ops and their implementations
     * are left as an exercise to the reader.
     */
    public int drainTo(Collection<? super E> c) {
        return 0;
    }
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }
    public boolean contains(Object o) {
        return false;
    }
    public boolean remove(Object o) {
        return false;
    }
    public int remainingCapacity() {
        return 0;
    }
    public E element() {
        return null;
    }
    public E remove() {
        return null;
    }
    public void clear() {
    }
    public boolean retainAll(Collection<?> collection) {
        return false;
    }
    public boolean removeAll(Collection<?> collection) {
        return false;
    }
    public boolean addAll(Collection<? extends E> collection) {
        return false;
    }
    public boolean containsAll(Collection<?> collection) {
        return false;
    }
    public Object[] toArray() {
        return null;
    }
    public <T> T[] toArray(T[] array) {
        return null;
    }
    public Iterator<E> iterator() {
        return null;
    }
}






