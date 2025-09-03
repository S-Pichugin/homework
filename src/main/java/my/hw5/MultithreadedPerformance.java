package my.hw5;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.*;

public class MultithreadedPerformance {
    private static final int ARRAY_SIZE = 100_000_000;
    private static final short[] array = new short[ARRAY_SIZE];
    
    static {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = (short) (Math.random() * 1000);
        }
    }

    public static long sumWithParallelStream(int threadsCount) {
        ForkJoinPool customThreadPool = new ForkJoinPool(threadsCount);
        try {
            return customThreadPool.submit(() ->
                    java.util.stream.IntStream.range(0, array.length)
                            .parallel()
                            .mapToLong(i -> array[i])
                            .sum()
            ).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            customThreadPool.shutdown();
        }
    }
    
    public static long sumWithParallelThreads(int threadsCount) throws InterruptedException {
        if (threadsCount <= 0) {
            throw new IllegalArgumentException("Thread count must be positive");
        }
        
        int chunkSize = ARRAY_SIZE / threadsCount;
        long[] partialSums = new long[threadsCount];
        Thread[] threads = new Thread[threadsCount];
        
        for (int i = 0; i < threadsCount; i++) {
            final int threadIndex = i;
            final int startIndex = i * chunkSize;
            final int endIndex = (i == threadsCount - 1) ? ARRAY_SIZE : (i + 1) * chunkSize;
            
            threads[i] = new Thread(() -> {
                long sum = 0;
                for (int j = startIndex; j < endIndex; j++) {
                    sum += array[j];
                }
                partialSums[threadIndex] = sum;
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long totalSum = 0;
        for (long partialSum : partialSums) {
            totalSum += partialSum;
        }
        
        return totalSum;
    }
    
    public static void main(String[] args) {
        int[] threadCounts = {1, 10, 100, 1000};
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("multithreading_performance_results.txt"))) {
            writer.println("Thread Count | Method | Time (ms)");
            writer.println("-------------|--------|-----------");
            
            for (int threadCount : threadCounts) {
                System.out.println("Testing with " + threadCount + " threads...");

                long startTime = System.nanoTime();
                long streamResult = sumWithParallelStream(threadCount);
                long endTime = System.nanoTime();
                long streamTime = (endTime - startTime) / 1_000_000;
                
                writer.printf("%-12d | Stream | %-8d%n", threadCount, streamTime);
                System.out.printf("Parallel Stream: %d ms, Result: %d%n", streamTime, streamResult);

                try {
                    startTime = System.nanoTime();
                    long threadResult = sumWithParallelThreads(threadCount);
                    endTime = System.nanoTime();
                    long threadTime = (endTime - startTime) / 1_000_000;
                    
                    writer.printf("%-12d | Thread| %-8d%n", threadCount, threadTime);
                    System.out.printf("Parallel Threads: %d ms, Result: %d%n", threadTime, threadResult);

                    if (streamResult != threadResult) {
                        System.err.println("WARNING: Results differ! Stream: " + streamResult + ", Threads: " + threadResult);
                    }
                    
                } catch (Exception e) {
                    writer.printf("%-12d | Thread| ERROR%n", threadCount);
                    System.err.println("Error with " + threadCount + " threads: " + e.getMessage());
                }
                
                writer.println();
            }
            
            System.out.println("Results written to multithreading_performance_results.txt");
            
        } catch (IOException e) {
            System.err.println("Error writing results: " + e.getMessage());
        }
    }
}
