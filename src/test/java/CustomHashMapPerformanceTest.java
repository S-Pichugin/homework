import my.CustomHashMap;

import java.util.*;

public class CustomHashMapPerformanceTest {
    public static void main(String[] args) {
        int n = 1_000_000;
        List<Integer> keys = new ArrayList<>(n);
        for (int i = 0; i < n; i++) keys.add(i);
        Collections.shuffle(keys);

        CustomHashMap<Integer, Integer> customMap = new CustomHashMap<>();
        long start = System.currentTimeMillis();
        for (Integer k : keys) customMap.put(k, k);
        long putCustom = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (Integer k : keys) customMap.get(k);
        long getCustom = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (Integer k : keys) customMap.remove(k);
        long removeCustom = System.currentTimeMillis() - start;


        HashMap<Integer, Integer> jdkMap = new HashMap<>();
        start = System.currentTimeMillis();
        for (Integer k : keys) jdkMap.put(k, k);
        long putJdk = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (Integer k : keys) jdkMap.get(k);
        long getJdk = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        for (Integer k : keys) jdkMap.remove(k);
        long removeJdk = System.currentTimeMillis() - start;

        System.out.println("CustomHashMap: put=" + putCustom + "ms, get=" + getCustom + "ms, remove=" + removeCustom + "ms");
        System.out.println("JDK HashMap:   put=" + putJdk + "ms, get=" + getJdk + "ms, remove=" + removeJdk + "ms");
    }
} 