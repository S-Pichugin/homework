package my.hw6;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class WebServerDemo {
    public static void main(String[] args) {
        System.out.println("=== HW6 Demo ===");
        testConcurrentExecution();
        testShutdownBehavior();
        testPerformanceComparison();
        startServersBriefly();
    }

    private static void testConcurrentExecution() {
        System.out.println("-- Concurrent Execution Test");
        AtomicInteger counter = new AtomicInteger(0);
        CustomExecutorService exec = new CustomExecutorService(100, true);
        int tasks = 1000;
        List<Callable<Void>> list = new ArrayList<>();
        for (int i = 0; i < tasks; i++) {
            list.add(() -> {
                counter.incrementAndGet();
                return null;
            });
        }
        try {
            for (Callable<Void> c : list) exec.submit(c);
        } finally {
            exec.shutdown();
            try { exec.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        }
        System.out.println("Counter = " + counter.get());
    }

    private static void testShutdownBehavior() {
        System.out.println("-- Shutdown Behavior Test");
        CustomExecutorService exec = new CustomExecutorService(10, false);
        for (int i = 0; i < 50; i++) {
            exec.execute(() -> {
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
            });
        }
        exec.shutdown();
        try {
            boolean ok = exec.awaitTermination(3, TimeUnit.SECONDS);
            System.out.println("Terminated: " + ok);
        } catch (InterruptedException ignored) {}
    }

    private static void testPerformanceComparison() {
        System.out.println("-- Performance Comparison (sleepy tasks)");
        int[] poolSizes = {10, 50, 100, 500};
        int tasks = 10_000;
        for (int size : poolSizes) {
            runPerf("virtual", size, true, tasks);
            runPerf("platform", size, false, tasks);
        }
    }

    private static void runPerf(String label, int poolSize, boolean vthreads, int tasks) {
        CustomExecutorService exec = new CustomExecutorService(poolSize, vthreads);
        long start = System.nanoTime();
        for (int i = 0; i < tasks; i++) {
            exec.execute(() -> {
                try { Thread.sleep(10); } catch (InterruptedException ignored) {}
            });
        }
        exec.shutdown();
        try { exec.awaitTermination(60, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
        long ms = Duration.ofNanos(System.nanoTime() - start).toMillis();
        System.out.println(label + " size=" + poolSize + " -> " + ms + " ms");
    }

    private static void startServersBriefly() {
        System.out.println("-- Starting servers (10s)");
        CustomWebServer v = new CustomWebServer(8080, 100, true);
        CustomWebServer p = new CustomWebServer(8081, 50, false);
        try {
            v.start();
            p.start();
            System.out.println("Virtual: http://localhost:8080");
            System.out.println("Platform: http://localhost:8081");
            Thread.sleep(10_000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            v.stop();
            p.stop();
        }
    }
}


