package com.gaayong.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExpenseRepository {
    Integer findTotal(String id, String year, String month);

    Integer findVarTotal(String id, String year, String month);

    Integer findFixedTotal(String id, String year, String month);

    Integer findCategoryTotal(String id, String category, String year, String month);

    List<Map<String, Object>> findList(String id, String category, String year, String month);
}
