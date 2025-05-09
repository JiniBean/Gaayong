package com.gaayong.service;

import com.gaayong.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ExpenseServiceImp implements ExpenseService{
    @Autowired
    private ExpenseRepository repository;

    @Override
    public Integer getTotal(String id, String year, String month) {
        return repository.findTotal(id, year, month);
    }

    @Override
    public Integer getVarTotal(String id, String year, String month) {
        return repository.findVarTotal(id, year, month);
    }

    @Override
    public Integer getFixedTotal(String id, String year, String month) {
        return repository.findFixedTotal(id, year, month);
    }

    @Override
    public Integer getCategoryTotal(String id, String category, String year, String month) {
        return repository.findCategoryTotal(id, category, year, month);
    }

    @Override
    public List<Map<String, Object>> getList(String id, String category, String year, String month) {
        return repository.findList(id, category, year, month);
    }
}
