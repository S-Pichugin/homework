import my.FibonacciAlgorithms;

import java.util.function.Function;

public class FibonacciAlgorithmsPerfomanceTest {

    public static void main(String[] args) {
        performanceTestsForNum(10);
        performanceTestsForNum(20);
        performanceTestsForNum(30);
        performanceTestsForNum(35);
        performanceTestsForNum(50);

    }

    private static void performanceTestsForNum(int n) {
        testPerformanceBenchmark(FibonacciAlgorithms::fibonacciRecursive, "Recursive", n);
        testPerformanceBenchmark(FibonacciAlgorithms::fibonacciMemoized, "Memoized", n);
        testPerformanceBenchmark(FibonacciAlgorithms::fibonacciIterative, "Iterative", n);
    }

    private static void testPerformanceBenchmark(Function<Integer, Long> fibFunction, String algorithmName, int n) {
        System.gc();
        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        fibFunction.apply(n);

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.println("[" + algorithmName + " algorithm for num = " + n + "]");
        System.out.println("Time (ms): " + (endTime - startTime));
        System.out.printf("Memory (MB): %.3f%n%n", (endMemory - startMemory) / (1024.0 * 1024.0));
        System.out.println();
    }
}
