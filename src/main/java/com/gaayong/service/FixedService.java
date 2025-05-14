package com.gaayong.service;

import java.util.List;
import java.util.Map;

public interface FixedService {
    Integer getTotal(String id);

    Integer getUnpaid(String id);

    List<Map<String, Object>> getList(String id, Boolean flag);

    boolean add(Map<String, String> map);

    boolean del(Map<String, String> map);

    boolean mod(Map<String, String> map);
}
