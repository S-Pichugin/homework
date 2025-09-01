import my.CustomHashMap;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomHashMapTest {

    static Stream<Map> mapProvider() {
        return Stream.of(
                new HashMap<>(),
                new CustomHashMap<>()
        );
    }


    @ParameterizedTest
    @MethodSource("mapProvider")
    void testPutAndGet(Map<String, Integer> map) {
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
    }

    @ParameterizedTest
    @MethodSource("mapProvider")
    void testOverwriteValue(Map<String, Integer> map) {
        map.put("a", 1);
        map.put("a", 2);
        assertEquals(2, map.get("a"));
    }

    @ParameterizedTest
    @MethodSource("mapProvider")
    void testRemove(Map<String, Integer> map) {
        map.put("a", 1);
        map.put("b", 2);
        map.remove("a");
        assertNull(map.get("a"));
        assertEquals(2, map.get("b"));
    }

    @ParameterizedTest
    @MethodSource("mapProvider")
    void testSizeAndIsEmpty(Map<String, Integer> map) {
        assertTrue(map.isEmpty());
        map.put("a", 1);
        assertEquals(1, map.size());
        map.put("b", 2);
        assertEquals(2, map.size());
        map.remove("a");
        assertEquals(1, map.size());
        map.remove("b");
        assertTrue(map.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("mapProvider")
    void testKeySetAndValues(Map<String, Integer> map) {
        map.put("a", 1);
        map.put("b", 2);
        Set<String> keys = map.keySet();
        assertTrue(keys.contains("a"));
        assertTrue(keys.contains("b"));
        Collection<Integer> values = map.values();
        assertTrue(values.contains(1));
        assertTrue(values.contains(2));
    }

    @ParameterizedTest
    @MethodSource("mapProvider")
    void testEntrySet(Map<String, Integer> map) {
        map.put("a", 1);
        map.put("b", 2);
        Set<Map.Entry<String, Integer>> entries = map.entrySet();
        assertEquals(2, entries.size());
        for (Map.Entry<String, Integer> entry : entries) {
            assertTrue(entry.getKey().equals("a") || entry.getKey().equals("b"));
        }
    }

    @ParameterizedTest
    @MethodSource("mapProvider")
    void testClear(Map<String, Integer> map) {
        map.put("a", 1);
        map.put("b", 2);
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }



    @ParameterizedTest
    @MethodSource("mapProvider")
    void testGetNonExistentKeyThrows(Map<String, Integer> map) {
        assertNull(map.get("notfound"));
    }
}