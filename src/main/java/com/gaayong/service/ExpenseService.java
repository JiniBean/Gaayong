package com.gaayong.service;

import java.util.List;
import java.util.Map;

public interface ExpenseService {
    Integer getTotal(String id, String year, String month);

    Integer getVarTotal(String id, String year, String month);

    Integer getFixedTotal(String id, String year, String month);

    Integer getCategoryTotal(String id, String category, String year, String month);

    List<Map<String, Object>> getList(String id, String category, String year, String month);

    boolean add(Map<String, String> map);

    boolean del(Map<String, String> map);

    boolean mod(Map<String, String> map);

    List<Map<String, Object>> getCardPmt(String id);
}
