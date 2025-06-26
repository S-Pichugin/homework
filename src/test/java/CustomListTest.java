import my.CustomList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CustomListTest {

    static Stream<List<String>> listProvider() {
        return Stream.of(
                new CustomList<>(),
                new ArrayList<>()
        );
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testAddAndGet(List<String> list) {
        list.add("Object1");
        list.add("Object2");

        assertEquals("Object1", list.get(0));
        assertEquals("Object2", list.get(1));
        assertEquals(2, list.size());
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testGetInvalidIndex(List<Integer> list) {
        list.add(10);

        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testSet(List<String> list) {
        list.add("A");
        list.add("B");

        var oldValue = list.set(1, "C");
        assertEquals("B", oldValue);
        assertEquals("C", list.get(1));
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testSetInvalidIndex(List<String> list) {
        assertThrows(IndexOutOfBoundsException.class, () -> list.set(11, "X"));
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testRemove(List<Integer> list) {
        list.add(10);
        list.add(20);
        list.add(30);
        var removed = list.remove(0);

        assertEquals(10, removed);
        assertEquals(2, list.size());
        assertEquals(30, list.get(1));
        assertEquals(20, list.get(0));

    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testRemoveAllElements(List<String> list) {
        list.add("A");
        list.add("B");
        list.remove(0);
        list.remove(0);
        assertTrue(list.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testRemoveFromEmptyList(List<String> list) {

        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertTrue(list.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testIsEmpty(List<String> list) {
        assertTrue(list.isEmpty());

        list.add("Test");
        assertFalse(list.isEmpty());

        list.clear();
        assertTrue(list.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testCapacityGrowth(List<Integer> list) {
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        assertEquals(20, list.size());
        assertEquals(15, list.get(15));
    }

    @ParameterizedTest
    @MethodSource("listProvider")
    public void testNullSupport(List<String> list) {
        list.add(null);
        list.add("NotNull");

        assertNull(list.get(0));
        assertEquals("NotNull", list.get(1));
    }
}