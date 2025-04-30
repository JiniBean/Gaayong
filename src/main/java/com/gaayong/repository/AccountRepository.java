package com.gaayong.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountRepository {
    List<Map<String, String>> findList(String id);
    Integer findTotal(String id);
    boolean save(Map<String, String> map);
    
    boolean mod(Map<String, String> map);
    
    boolean del(Map<String, String> map);
} 