package my;

import org.w3c.dom.ls.LSOutput;

import java.util.Arrays;

public class ArrayOperations {
    /** Shift array elements using System.arraycopy
     * Time Complexity: worst-case scenario is O(N). However,
     * the processor can copy contiguous blocks of memory one block at a time (memcpy() in C),
     * so actual results can be better. */
    public static int [] shiftLeftSystemCopy(int[] array, int positions) {
        int [] newArray = new int[array.length + positions];
        System.arraycopy(array, 0, newArray, positions, array.length);
        return newArray;
    }

    /** Shift array elements using manual for loop
     * Time Complexity: O(n) - single loop from 0 to n */

    public static int [] shiftLeftManualLoop(int[] array, int positions) {
        int [] newArray = new int[array.length + positions];
        for (int i = 0; i < array.length; i++) {
            newArray[i + positions] = array[i];
        }
        return newArray;
    }
}
