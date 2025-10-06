package my.hw5;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadWeightComparison {
    private static final int THREAD_COUNT = 8000;
    private static final int SLEEP_TIME_MS = 200;
    
    public static void main(String[] args) {
        System.out.println("=== Thread Weight Comparison ===");
        System.out.println("Testing with " + THREAD_COUNT + " threads, each sleeping for " + SLEEP_TIME_MS + "ms\n");

        testVirtualThreads();

        testPlatformThreads();
    }
    
    private static void testVirtualThreads() {
        System.out.println("--- Virtual Threads Test ---");
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long initialMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long startTime = System.currentTimeMillis();
        
        try {
            Thread[] virtualThreads = new Thread[THREAD_COUNT];
            for (int i = 0; i < THREAD_COUNT; i++) {
                virtualThreads[i] = Thread.startVirtualThread(() -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        long endTime = System.currentTimeMillis();
        long finalMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        System.out.printf("Execution time: %d ms%n", endTime - startTime);
        System.out.printf("Memory used: %d bytes (%.2f MB)%n", 
                         finalMemory - initialMemory, 
                         (finalMemory - initialMemory) / (1024.0 * 1024.0));
        System.out.println();
    }
    
    private static void testPlatformThreads() {
        System.out.println("--- Platform Threads Test ---");
        
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long initialMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long startTime = System.currentTimeMillis();
        
        try (ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT)) {
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        Thread.sleep(SLEEP_TIME_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Platform threads test interrupted");
        }
        
        long endTime = System.currentTimeMillis();
        long finalMemory = memoryBean.getHeapMemoryUsage().getUsed();
        
        System.out.printf("Execution time: %d ms%n", endTime - startTime);
        System.out.printf("Memory used: %d bytes (%.2f MB)%n", 
                         finalMemory - initialMemory, 
                         (finalMemory - initialMemory) / (1024.0 * 1024.0));
        System.out.println();
    }
}
