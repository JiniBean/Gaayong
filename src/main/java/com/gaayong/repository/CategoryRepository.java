package com.gaayong.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryRepository {
    List<Map<String, Object>> findList(String id, String code);

    boolean save(Map<String, String> map);

    boolean del(Map<String, String> map);

    boolean mod(Map<String, String> map);

    boolean saveDefaultCategory(String userId);
}
