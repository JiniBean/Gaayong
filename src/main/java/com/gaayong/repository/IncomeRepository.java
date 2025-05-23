package com.gaayong.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IncomeRepository {
    Integer findTotal(String id, String year, String month);

    Integer findBudgetTotal(String id, String year, String month);

    Integer findExtraTotal(String id, String year, String month);

    Integer findCategoryTotal(String id, String category, String year, String month);

    List<Map<String, Object>> findList(String id, String category, String year, String month);

    boolean del(Map<String, String> map);

    boolean save(Map<String, String> map);

    Map<String, Object> findById(String id);

    boolean mod(Map<String, String> map);
} 