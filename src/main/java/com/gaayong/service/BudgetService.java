package com.gaayong.service;

import java.util.List;
import java.util.Map;

public interface BudgetService {
    List<Map<String, Object>> getList(String id);
    Integer getTotal(String id);
    boolean edit(Map<String, String> map, String method);
}
