package com.gaayong.service;

import com.gaayong.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BudgetServiceImp implements BudgetService{
    @Autowired
    private BudgetRepository repository;

    @Override
    public List<Map<String, Object>> getList(String id) {
        return repository.findList(id);
    }

    @Override
    public Integer getTotal(String id) {
        return repository.findTotal(id);
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
}
