package com.gaayong.service;

import com.gaayong.repository.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CodeServiceImp implements CodeService{

    @Autowired
    private CodeRepository repository;

    @Override
    public List<Map<String, String>> getList(String code) {
        return repository.findList(code);
    }
}
