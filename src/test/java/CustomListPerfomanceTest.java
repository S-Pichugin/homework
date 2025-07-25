import my.CustomLinkedList;
import my.CustomList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomListPerfomanceTest {
    private static final int ELEMENTS_COUNT = 1_000_000;

    public static void main(String[] args) {
        testMassiveInsertion(new CustomList<>(), "CustomList");
        testMassiveInsertion(new ArrayList<>(), "ArrayList");
        testMassiveInsertion(new LinkedList<>(), "LinkedList");
        testMassiveInsertion(new CustomLinkedList<>(), "CustomLinkedList");
        testAddRemoveFirst(new CustomList<>(), "CustomList");
        testAddRemoveFirst(new ArrayList<>(), "ArrayList");
        testAddRemoveFirst(new LinkedList<>(), "LinkedList");
        testAddRemoveFirst(new CustomLinkedList<>(), "CustomLinkedList");
    }

    private static void testMassiveInsertion(List<Integer> list, String listType) {
        System.gc();
        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (int i = 0; i < ELEMENTS_COUNT; i++) {
            list.add(i);
        }

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.println("[" + listType + "]");
        System.out.println("Time (ms): " + (endTime - startTime));
        System.out.println("Memory (MB): " + (endMemory - startMemory) / (1024 * 1024));
        System.out.println();
    }

    private static void testAddRemoveFirst(List<Integer> list, String listType) {
        System.gc();
        long startTime = System.currentTimeMillis();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        for (int i = 0; i < 10_000; i++) {
            list.add(i);
        }


        for (int i = 0; i < 10_000; i++) {
            list.remove(0);
        }

        long endTime = System.currentTimeMillis();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("[" + listType + "] (Add/Remove First)");
        System.out.println("Time (ms): " + (endTime - startTime));
        System.out.printf("Memory (MB): %.2f%n%n", (endMemory - startMemory) / (1024.0 * 1024.0));
        System.out.println();
    }
}
