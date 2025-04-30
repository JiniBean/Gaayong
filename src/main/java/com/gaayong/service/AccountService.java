package com.gaayong.service;

import java.util.List;
import java.util.Map;

public interface AccountService {
    List<Map<String, String>> getList(String id);
    Integer getTotal(String id);
    boolean edit(Map<String, String> map, String method);
} 