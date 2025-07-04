import my.ArrayOperations;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;


public class ArrayOperationsPerformanceTest {

    private static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(1_000, 1),
                Arguments.of(1_000, 10),
                Arguments.of(1_000, 100),
                Arguments.of(1_000, 1000),
                Arguments.of(10_000, 1),
                Arguments.of(10_000, 10),
                Arguments.of(10_000, 100),
                Arguments.of(10_000, 1000),
                Arguments.of(100_000, 1),
                Arguments.of(100_000, 10),
                Arguments.of(100_000, 100),
                Arguments.of(100_000, 1000),
                Arguments.of(1_000_000, 1),
                Arguments.of(1_000_000, 10),
                Arguments.of(1_000_000, 100),
                Arguments.of(1_000_000, 1000),
                Arguments.of(1_000_000, 10000)
        );
    }

    private int[] generateTestArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testShiftLeftSystemCopyPerformance(int arraySize, int shiftPositions) {
        int[] array = generateTestArray(arraySize);

        long startTime = System.nanoTime();
        ArrayOperations.shiftLeftSystemCopy(array, shiftPositions);
        long endTime = System.nanoTime();

        System.out.printf("System.arraycopy: size=%,d, positions=%,d, time=%,d ns%n",
                arraySize, shiftPositions, endTime - startTime);
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testShiftLeftManualLoopPerformance(int arraySize, int shiftPositions) {
        int[] array = generateTestArray(arraySize);

        long startTime = System.nanoTime();
        ArrayOperations.shiftLeftManualLoop(array, shiftPositions);
        long endTime = System.nanoTime();

        System.out.printf("Manual loop: size=%,d, positions=%,d, time=%,d ns%n",
                arraySize, shiftPositions, endTime - startTime);
    }
}
