import my.ArrayOperations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArrayOperationsEqualityTest {

    private static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of(10, 1),
                Arguments.of(10, 3),
                Arguments.of(100, 10),
                Arguments.of(100, 50),
                Arguments.of(100, 99),
                Arguments.of(100, 100),
                Arguments.of(100, 101)
        );
    }

    @ParameterizedTest
    @MethodSource("testCases")
    public void testBothMethodsProduceSameResult(int arraySize, int shiftPositions) {
        int[] originalArray = generateTestArray(arraySize);

        int [] arrayForSystemCopy = ArrayOperations.shiftLeftSystemCopy(originalArray, shiftPositions);
        int [] arrayForManualLoop =  ArrayOperations.shiftLeftManualLoop(originalArray, shiftPositions);

        assertArrayEquals(arrayForSystemCopy, arrayForManualLoop,
                String.format("Arrays differ for size=%d, shift=%d", arraySize, shiftPositions));
    }

    @Test
    public void testEmptyArray() {
        int[] emptyArray = new int[0];

        int [] arrayForSystemCopy = ArrayOperations.shiftLeftSystemCopy(emptyArray, 5);
        int [] arrayForManualLoop = ArrayOperations.shiftLeftManualLoop(emptyArray, 5);

        assertArrayEquals(arrayForSystemCopy, arrayForManualLoop, "Empty array should remain unchanged");
    }

    @Test
    public void testSingleElementArray() {
        int[] singleElement = {42};

        int [] arrayForSystemCopy = ArrayOperations.shiftLeftSystemCopy(singleElement, 1);
        int [] arrayForManualLoop = ArrayOperations.shiftLeftManualLoop(singleElement, 1);

        assertArrayEquals(arrayForSystemCopy, arrayForManualLoop, "Single element array should remain unchanged");
    }

    private int[] generateTestArray(int size) {
        int[] array = new int[size];
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(100);
        }
        return array;
    }
}
