package com.gaayong.repository;

import com.gaayong.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface UserRepository {
    
    /**
     * 사용자 이름으로 사용자 정보 조회
     * @param userNm 사용자 이름
     * @return 사용자 정보
     */
    User findByUserNm(String userNm);
    
    /**
     * 새 사용자 등록
     * @param Map<String, String> 사용자 정보
     * @return 영향받은 행 수
     */
    int saveUser(Map<String, String> user);
} 