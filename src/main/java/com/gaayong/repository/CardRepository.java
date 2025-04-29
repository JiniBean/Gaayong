package com.gaayong.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CardRepository {
    List<Map<String, String>> findList(String id);
    
    boolean save(Map<String, String> map);
    
    boolean mod(Map<String, String> map);
    
    boolean del(Map<String, String> map);
} 