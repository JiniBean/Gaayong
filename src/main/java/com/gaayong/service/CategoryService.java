package com.gaayong.service;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Map<String, Object>> getList(String id, String code);

    boolean edit(Map<String, String> map, String method);

    boolean addDefaultCategory(String id);
}
