package com.gaayong.service;

import com.gaayong.repository.PayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class PayServiceImp implements PayService {
    @Autowired
    private PayRepository repository;

    @Override
    public Integer getTotal(String id, String year, String month) {
        return repository.findTotal(id, year, month);
    }

    @Override
    public Integer getUnpaid(String id, String year, String month) {
        return repository.findUnpaid(id, year, month);
    }

    @Override
    public Integer getCategoryTotal(String id, String category, String year, String month) {
        return repository.findCategoryTotal(id, category, year, month);
    }

    @Override
    public List<Map<String, Object>> getList(String id, String category, String year, String month) {
        return repository.findList(id, category, year, month);
    }

    @Transactional
    @Override
    public boolean add(Map<String, String> map) {
        return repository.save(map);
    }

    @Transactional
    @Override
    public boolean mod(Map<String, String> map) {
        return repository.mod(map);
    }

    @Transactional
    @Override
    public boolean del(Map<String, String> map) {
        return repository.del(map);
    }
}
