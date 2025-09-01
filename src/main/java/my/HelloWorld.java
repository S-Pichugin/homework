package my;

import java.util.HashMap;

public class HelloWorld {
    public static void main(String[] args) {
        CustomHashMap myMap = new CustomHashMap<>();
        HashMap<Integer, String> myHashMap = new HashMap<>();
        myHashMap.put(1, "1");
        myHashMap.put(2, "2");
        myHashMap.remove(2);
        System.out.println(myHashMap.get(2));

        System.out.println(myMap.keySet());
//        myMap.put("5", "world5");
//        myMap.put("6", "world6");
//        myMap.put("7", "world7");
//        myMap.put("8", "world8");
//        myMap.put("9", "world9");
//        myMap.put("10", "world10");
//        myMap.put("11", "world11");
//        myMap.put("12", "world12");
        System.out.println(myMap);
    }
}
