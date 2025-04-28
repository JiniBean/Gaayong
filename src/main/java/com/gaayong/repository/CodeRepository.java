package com.gaayong.repository;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface CodeRepository {

    List<Map<String, String>> findList(String code);

}
