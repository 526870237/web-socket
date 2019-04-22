package test;

import java.util.HashMap;

public class Test {
	public static void main(String[] args) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("name", "12");
        stringStringHashMap.put("name12", "12");
        stringStringHashMap.remove("id");
        stringStringHashMap.remove("name");
        System.out.println(stringStringHashMap);
    }
}