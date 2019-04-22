package com.ribeen.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 下划线与驼峰相互转换
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/12 21:58
 */
public class HumpUtil {
    private static final char UNDERLINE = '_';

    /**
     * 数组由下划线转换为驼峰
     *
     * @param arr 带有下划线的数组
     * @return java.util.Map<java.lang.String,V>
     */
    public static String[] arrUnderlineToHump(String[] arr) {
        if (arr == null) {
            return null;
        }
        String[] newArr = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = toHump(arr[i]);
        }
        return newArr;
    }

    /**
     * 数组由驼峰转换为下划线
     *
     * @param arr 带有下划线的数组
     * @return java.util.Map<java.lang.String,V>
     */
    public static String[] arrHumpToUnderline(String[] arr) {
        if (arr == null) {
            return null;
        }
        String[] newArr = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            newArr[i] = toUnderline(arr[i]);
        }
        return newArr;
    }

    /**
     * 将Map中的key由下划线转换为驼峰
     *
     * @param map 带有下划线的map
     * @return java.util.Map<java.lang.String,V>
     */
    public static <V> Map<String, V> mapKeyUnderlineToHump(Map<String, V> map) {
        if (map == null) {
            return null;
        }
        Map<String, V> newMap = new HashMap<>(map.size());
        for (Map.Entry<String, V> entry : map.entrySet()) {
            String key = entry.getKey();
            String newKey = toHump(key);
            newMap.put(newKey, entry.getValue());
        }
        return newMap;
    }

    /**
     * 将Map中的key由驼峰转换为下划线
     *
     * @param map 带有驼峰的map
     * @return java.util.Map<java.lang.String,V>
     */
    public static <V> Map<String, V> mapKeyHumpToUnderline(Map<String, V> map) {
        if (map == null) {
            return null;
        }
        Map<String, V> newMap = new HashMap<>(map.size());
        for (Map.Entry<String, V> entry : map.entrySet()) {
            String key = entry.getKey();
            String newKey = toUnderline(key);
            newMap.put(newKey, entry.getValue());
        }
        return newMap;
    }

    /**
     * 下划线转驼峰
     *
     * @param colName 字符串
     * @return java.lang.String
     */
    public static String toHump(String colName) {
        if (colName == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] str = colName.toLowerCase().split(String.valueOf(UNDERLINE));
        for (String s : str) {
            if (s.length() == 1) {
                sb.append(s.toUpperCase());
                continue;
            }
            if (s.length() > 1) {
                sb.append(s.substring(0, 1).toUpperCase());
                sb.append(s.substring(1));
            }
        }
        String result = sb.toString();
        return result.substring(0, 1).toLowerCase() + result.substring(1);
    }

    /**
     * 驼峰转下划线
     *
     * @param colName 字符串
     * @return java.lang.String
     */
    public static String toUnderline(String colName) {
        if (colName == null) {
            return null;
        }
        String result = colName.replaceAll("[A-Z]", String.valueOf(UNDERLINE) + "$0");
        return result.toLowerCase();
    }
}
