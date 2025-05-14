package com.gaayong.service;

import com.gaayong.repository.AccountRepository;
import com.gaayong.repository.ExpenseRepository;
import com.gaayong.repository.FixedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class FixedServiceImp implements FixedService{
    @Autowired
    private FixedRepository repository;

    @Autowired
    private ExpenseService expenseService;


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
    public boolean add(Map<String, String> map) {
        int id = repository.save(map);
        if(map.get("isPaid") != null && map.get("isPaid").equals("on")) {
            map.put("fixedId", String.valueOf(id));
            map.put("dd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            expenseService.add(map);
        }
        return true;

    }

    @Transactional
    @Override
    public boolean del(Map<String, String> map) {
        return repository.del(map);
    }

    @Transactional
    @Override
    public boolean mod(Map<String, String> map) {
        System.out.println(map);
        Map<String, Object> pre = repository.findById(map.get("id"));
        repository.mod(map);

        boolean isPaid = map.get("isPaid") != null && map.get("isPaid").equals("on");
        System.out.println("isPaid : " + isPaid);
        boolean preIsPaid = (boolean) pre.get("IS_PAID");
        map.put("dd", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // 미결제 -> 결제완료
        if(!preIsPaid && isPaid) expenseService.add(map);
        // 결제완료 -> 미결제
        else if (preIsPaid && !isPaid) expenseService.del(map);
       return true;
    }
}
