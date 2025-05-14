package com.gaayong.service;

import java.util.List;
import java.util.Map;

public interface IncomeService {
    List<Map<String, Object>> getList(String id, String category, String year, String month);

    Integer getTotal(String id, String year, String month);

    Integer getBudgetTotal(String id, String year, String month);

    Integer getExtraTotal(String id, String year, String month);

    Integer getCategoryTotal(String id, String category, String year, String month);
    
    boolean add(Map<String, String> map);
    
    boolean mod(Map<String, String> map);
    
    boolean del(Map<String, String> map);
}
