import my.FibonacciAlgorithms;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FibonacciAlgorithmsTest {

    static IntStream numProvider() {
        return IntStream.rangeClosed(0, 35);
    }


    @ParameterizedTest
    @MethodSource("numProvider")
    public void testVerifyAllImplementationsProduceIdenticalResults(int n) {
        var fibRecValue = FibonacciAlgorithms.fibonacciRecursive(n);
        var fibMemValue = FibonacciAlgorithms.fibonacciMemoized(n);
        var fibIterValue = FibonacciAlgorithms.fibonacciIterative(n);

        assertEquals(fibRecValue, fibMemValue);
        assertEquals(fibMemValue, fibIterValue);
    }
}
