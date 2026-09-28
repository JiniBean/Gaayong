package com.gaayong.api.util;

import java.util.HashMap;
import java.util.Map;

public final class ApiBodyHelper {

    private ApiBodyHelper() {
    }

    public static Map<String, String> toServiceMap(Map<String, String> body, String userId, String id, String... keys) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        if (id != null) {
            map.put("id", id);
        }
        if (body != null) {
            for (String key : keys) {
                if (body.get(key) != null) {
                    map.put(key, body.get(key));
                }
            }
        }
        return map;
    }

    public static Map<String, String> allBodyFields(Map<String, String> body, String userId, String id) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        if (id != null) {
            map.put("id", id);
        }
        if (body != null) {
            map.putAll(body);
        }
        return map;
    }
}
