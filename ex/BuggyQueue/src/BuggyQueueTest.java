import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @class BuggyQueueTest
 * 
 * @brief Test program for the SimpleQueue that induces race
 *        conditions due to lack of synchronization.
 */
public class BuggyQueueTest
{ 
    /**
     * Maximum number of iterations.
     */
    private static int mMaxIterations = 100;

    /**
     * Main entry point that tests the SimpleQueue class.
     */
    public static void main(String argv[]) {
        final SimpleQueue<String> simpleQueue =
            new SimpleQueue<String>();

        try {
            // Create a producer thread.
            Thread producer = 
                new Thread(new Runnable(){ 
                        public void run(){ 
                            try {
                                for(int i = 0; i < mMaxIterations; i++)
                                    simpleQueue.put(Integer.toString(i)); 
                            } catch (InterruptedException e) {
                                System.out.println("InterruptedException caught");
                            }
                        }});
        
            // Create a consumer thread.
            Thread consumer =
                new Thread(new Runnable(){
                        public void run(){ 
                            try {
                                for(int i = 0; i < mMaxIterations; i++)
                                    System.out.println(simpleQueue.take());
                            } catch (InterruptedException e) {
                                System.out.println("InterruptedException caught");
                            }
                        }});

            // Run both threads concurrently.
            consumer.start();
            producer.start();

            // Wait for both threads to stop.
            consumer.join();
            producer.join();
        } catch (Exception e) {
            System.out.println("caught exception");
        }
    }
}
