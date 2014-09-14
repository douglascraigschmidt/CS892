import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @class Main
 * 
 * @brief Test program for the BuggyQueue.
 */
public class Main
{ 
    /**
     * Maximum number of iterations.
     */
    private static int mMaxIterations = 100;

    /**
     * Main entry point that tests the BuggyQueue class.
     */
    public static void main(String argv[]) {
        final BuggyQueue buggyQueue = new BuggyQueue();

        // Create a producer thread.
        Thread producer = 
            new Thread(new Runnable(){ 
                    public void run(){ 
                        for(int i = 0; i < mMaxIterations; i++)
                            buggyQueue.put(Integer.toString(i)); 
                    }});
        
        // Create a consumer thread.
        Thread consumer =
            new Thread(new Runnable(){
                    public void run(){ 
                        for(int i = 0; i < mMaxIterations; i++)
                            System.out.println(buggyQueue.take());
                    }});

        // Run both threads concurrently.
        consumer.start();
        producer.start();
    }
}
