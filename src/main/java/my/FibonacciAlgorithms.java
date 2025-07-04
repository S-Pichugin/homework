package my;

import java.util.Arrays;

public class FibonacciAlgorithms {

    /**
     * Recursive implementation of Fibonacci sequence
     * Time Complexity: O(2^n)
     * Space Complexity:  O(n)
     * <p>
     * Explanation: Each call branches into two recursive calls, creating
     * a binary tree of calls with height n. The same subproblems are
     * solved multiple times, leading to exponential time complexity.
     */
    public static long fibonacciRecursive(int n) {
        if (n <= 1) {
            return 0;
        } else if (n == 2) {
            return 1;
        } else {
            return fibonacciRecursive(n - 1) + fibonacciRecursive(n - 2);
        }
    }

    /**
     * Memoized implementation of Fibonacci sequence
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * <p>
     * Explanation: By caching intermediate results, we avoid
     * redundant calculations. Each number from 0 to n is
     * calculated exactly once.
     */

    public static long fibonacciMemoized(int n) {
        var memo = new long[n + 1];
        Arrays.fill(memo, -1);
        return fibMemoHelper(n, memo);
    }

    private static long fibMemoHelper(int n, long[] memo) {
        if (n <= 1) {
            return 0;
        } else if (n == 2) {
            return 1;
        } else if (memo[n] != -1) {
            return memo[n];
        } else {

            long result = fibMemoHelper(n - 1, memo) + fibMemoHelper(n - 2, memo);
            memo[n] = result;
            return result;
        }
    }

    /**
     * Iterative implementation of Fibonacci sequence
     * Time Complexity: O(n) - single loop from 0 to n
     * Space Complexity: O(1) - constant space usage
     * <p>
     * Explanation: Uses bottom-up approach with only two variables
     * to track previous values, eliminating recursion overhead.
     */
    public static long fibonacciIterative(int n) {
        if (n <= 1) {
            return 0;
        } else if (n == 2) {
            return 1;
        }
        int prevPrevNum = 0;
        int prevNum = 1;
        int curNum = 0;

        for (int i = 3; i <= n; i++) {
            curNum = prevPrevNum + prevNum;
            prevPrevNum = prevNum;
            prevNum = curNum;
        }
        return curNum;
    }
}

