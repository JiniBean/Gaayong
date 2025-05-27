package com.gaayong.service;

import com.gaayong.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImp implements CategoryService{
    @Autowired
    private CategoryRepository repository;

    @Override
    public List<Map<String, Object>> getList(String id, String code) {
        return repository.findList(id, code);
    }

    @Override
    public boolean edit(Map<String, String> map, String method) {
        if(method.equals("add")){
            return repository.save(map);
        }else if(method.equals("del")){
            return repository.del(map);
        }else if(method.equals("mod")){
            return repository.mod(map);
        }
        return false;
    }

    @Override
    public boolean addDefaultCategory(String userId) {
        return repository.saveDefaultCategory(userId);
    }
}
