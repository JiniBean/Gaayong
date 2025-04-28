package com.gaayong.service;

import com.gaayong.entity.User;
import com.gaayong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userNm) throws UsernameNotFoundException {
        User user = userRepository.findByUserNm(userNm);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + userNm);
        }
        
        return user;
    }

    public int addUser(Map<String, String> user) {
        // 비밀번호 암호화
        user.put("pwd", passwordEncoder.encode(user.get("pwd")));
        System.out.println(user);
        // 사용자 이름 중복 확인
        User existingUser = userRepository.findByUserNm(user.get("userNm"));
        if (existingUser != null) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
        
        // 회원 가입 처리
        return userRepository.saveUser(user);
    }
} 