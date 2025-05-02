package com.gaayong.service;

import com.gaayong.repository.BudgetRepository;
import com.gaayong.repository.FixedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FixedServiceImp implements FixedService{
    @Autowired
    private FixedRepository repository;

    @Override
    public List<Map<String, Object>> getList(String id, Boolean flag) {
        return repository.findList(id, flag);
    }

    @Override
    public Integer getTotal(String id) {
        return repository.findTotal(id);
    }

    @Override
    public Integer getUnpaid(String id) {
        return repository.findUnpaid(id);
    }

    @Override
    public boolean edit(Map<String, String> map, String method) {
        map.putIfAbsent("isPaid", "0");
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
