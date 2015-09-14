package edu.vanderbilt.semaphore;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({SimpleAtomicLongUnitTest.class,
               FairSimpleSemaphoreUnitTest.class,
               UnfairSimpleSemaphoreUnitTest.class})
public class AllTests {
}
