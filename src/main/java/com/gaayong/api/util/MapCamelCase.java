package com.gaayong.api.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MapCamelCase {

    private MapCamelCase() {
    }

    public static String toCamelCaseKey(String key) {
        if (key == null || key.isEmpty()) {
            return key;
        }
        String[] parts = key.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    sb.append(parts[i].substring(1));
                }
            }
        }
        return sb.toString();
    }

    public static Map<String, Object> toCamelCaseMap(Map<String, ?> map) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            result.put(toCamelCaseKey(entry.getKey()), entry.getValue());
        }
        return result;
    }

    public static List<Map<String, Object>> toCamelCaseMaps(List<? extends Map<String, ?>> maps) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, ?> map : maps) {
            result.add(toCamelCaseMap(map));
        }
        return result;
    }
}
