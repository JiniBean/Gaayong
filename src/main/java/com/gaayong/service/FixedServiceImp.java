package com.gaayong.service;

import com.gaayong.repository.AccountRepository;
import com.gaayong.repository.ExpenseRepository;
import com.gaayong.repository.FixedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class FixedServiceImp implements FixedService{
    @Autowired
    private FixedRepository repository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private AccountRepository accountRepository;

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

    @Transactional
    @Override
    public boolean edit(Map<String, String> map, String method) {
        if(method.equals("add")){
            if(map.get("isPaid") != null){

            }
            return repository.save(map);
        }else if(method.equals("mod")){
            return repository.mod(map);
        }else if(method.equals("del")){
            return repository.del(map);
        }
        return false;
    }

    @Override
    public boolean add(Map<String, String> map) {
        if(map.get("isPaid") != null){

        }
        return repository.save(map);

    }

    @Override
    public boolean del(Map<String, String> map) {
        return repository.del(map);
    }

    @Override
    public boolean mod(Map<String, String> map) {
       return repository.mod(map);
    }
}
