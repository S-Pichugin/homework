package my.hw5;

import my.CustomList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadSafeCustomListTest {
    
    private CustomList<Integer> baseList;
    
    @BeforeEach
    void setUp() {
        baseList = new CustomList<>();
    }
    
    @Test
    void testSynchronizedDecorator() throws InterruptedException {
        List<Integer> synchronizedList = new ThreadSafeCustomList.SynchronizedCustomList<>(baseList);
        testConcurrentAdditions(synchronizedList, "Synchronized");
    }
    
    @Test
    void testReadWriteLockDecorator() throws InterruptedException {
        List<Integer> readWriteLockList = new ThreadSafeCustomList.ReadWriteLockCustomList<>(baseList);
        testConcurrentAdditions(readWriteLockList, "ReadWriteLock");
    }
    
    @Test
    void testCopyOnWriteDecorator() throws InterruptedException {
        List<Integer> copyOnWriteList = new ThreadSafeCustomList.CopyOnWriteCustomList<>(baseList);
        testConcurrentAdditions(copyOnWriteList, "CopyOnWrite");
    }
    
    @Test
    void testBaseListWithoutDecorator() throws InterruptedException {
        // Этот тест может падать из-за отсутствия синхронизации
        try {
            testConcurrentAdditions(baseList, "Base (No Decorator)");
        } catch (AssertionError e) {
            System.out.println("Expected failure for base list without decorator: " + e.getMessage());
        }
    }
    
    private void testConcurrentAdditions(List<Integer> list, String listType) throws InterruptedException {
        final int THREAD_COUNT = 2;
        final int ELEMENTS_PER_THREAD = 1_000_000;
        final int EXPECTED_TOTAL = THREAD_COUNT * ELEMENTS_PER_THREAD;
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                        list.add(threadId * ELEMENTS_PER_THREAD + j);
                    }
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("Thread " + threadId + " failed: " + e.getMessage());
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        int actualSize = list.size();
        int successfulThreads = successCount.get();
        
        System.out.printf("%s List:%n", listType);
        System.out.printf("  Expected size: %d%n", EXPECTED_TOTAL);
        System.out.printf("  Actual size: %d%n", actualSize);
        System.out.printf("  Successful threads: %d/%d%n", successfulThreads, THREAD_COUNT);
        System.out.printf("  Execution time: %d ms%n", executionTime);
        System.out.printf("  Elements per second: %.0f%n", (double) actualSize / executionTime * 1000);
        System.out.println();

        assertEquals(THREAD_COUNT, successfulThreads, "All threads should complete successfully");

        if (listType.contains("Synchronized") || listType.contains("ReadWriteLock") || listType.contains("CopyOnWrite")) {
            assertEquals(EXPECTED_TOTAL, actualSize, "List should contain all elements from all threads");
        }
    }
    
    @Test
    void testCorrectnessMultipleRuns() throws InterruptedException {
        final int RUNS = 100;
        final int THREAD_COUNT = 2;
        final int ELEMENTS_PER_THREAD = 10_000;
        
        List<Integer> synchronizedList = new ThreadSafeCustomList.SynchronizedCustomList<>(new CustomList<>());
        List<Integer> readWriteLockList = new ThreadSafeCustomList.ReadWriteLockCustomList<>(new CustomList<>());
        List<Integer> copyOnWriteList = new ThreadSafeCustomList.CopyOnWriteCustomList<>(new CustomList<>());
        
        int synchronizedFailures = 0;
        int readWriteLockFailures = 0;
        int copyOnWriteFailures = 0;
        
        for (int run = 0; run < RUNS; run++) {
            try {
                testCorrectnessRun(synchronizedList, THREAD_COUNT, ELEMENTS_PER_THREAD);
            } catch (AssertionError e) {
                synchronizedFailures++;
            }

            try {
                testCorrectnessRun(readWriteLockList, THREAD_COUNT, ELEMENTS_PER_THREAD);
            } catch (AssertionError e) {
                readWriteLockFailures++;
            }

            try {
                testCorrectnessRun(copyOnWriteList, THREAD_COUNT, ELEMENTS_PER_THREAD);
            } catch (AssertionError e) {
                copyOnWriteFailures++;
            }
            

            synchronizedList.clear();
            readWriteLockList.clear();
            copyOnWriteList.clear();
        }
        
        System.out.println("Correctness Test Results (" + RUNS + " runs):");
        System.out.println("  Synchronized failures: " + synchronizedFailures + "/" + RUNS);
        System.out.println("  ReadWriteLock failures: " + readWriteLockFailures + "/" + RUNS);
        System.out.println("  CopyOnWrite failures: " + copyOnWriteFailures + "/" + RUNS);
        

        assertEquals(0, synchronizedFailures, "Synchronized version should never fail");
        assertEquals(0, readWriteLockFailures, "ReadWriteLock version should never fail");
        assertEquals(0, copyOnWriteFailures, "CopyOnWrite version should never fail");
    }
    
    private void testCorrectnessRun(List<Integer> list, int threadCount, int elementsPerThread) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < elementsPerThread; j++) {
                    list.add(threadId * elementsPerThread + j);
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        int expectedSize = threadCount * elementsPerThread;
        assertEquals(expectedSize, list.size(), "List should contain exactly " + expectedSize + " elements");
        

        for (int i = 0; i < expectedSize; i++) {
            assertTrue(list.contains(i), "List should contain element " + i);
        }
    }
}
