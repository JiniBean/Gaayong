package com.gaayong.service;

import com.gaayong.repository.AccountRepository;
import com.gaayong.repository.IncomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class IncomeServiceImp implements IncomeService{

    @Autowired
    private IncomeRepository repository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public List<Map<String, Object>> getList(String id, String category, String year, String month) {
        return repository.findList(id, category, year, month);
    }

    @Override
    public Integer getTotal(String id, String year, String month) {
        return repository.findTotal(id, year, month);
    }

    @Override
    public Integer getBudgetTotal(String id, String year, String month) {
        return repository.findBudgetTotal(id, year, month);
    }

    @Override
    public Integer getExtraTotal(String id, String year, String month) {
        return repository.findExtraTotal(id, year, month);
    }

    @Override
    public Integer getCategoryTotal(String id, String category, String year, String month) {
        return repository.findCategoryTotal(id, category, year, month);
    }
    
    @Transactional
    public boolean add(Map<String, String> map) {
        // 계좌 연결된 수입인 경우 통장 잔고 증가
        String acctId = map.get("acctId");
        if (acctId != null && !acctId.isEmpty()) {
            int amount = Integer.parseInt(map.get("amt"));
            accountRepository.updateAmount(acctId, amount);
        }
        return repository.save(map);
    }
    
    @Transactional
    public boolean mod(Map<String, String> map) {
        // 기존 수입 정보 조회
        Map<String, Object> old = repository.findById(map.get("id"));
        String oldAcctId = old.get("ACCT_ID") != null ? old.get("ACCT_ID").toString() : null;
        String acctId = map.get("acctId");

        // 기존 금액, 현재 금액
        int oldAmt = Integer.parseInt(old.get("AMT").toString());
        int newAmt = Integer.parseInt(map.get("amt"));
        int dif = newAmt - oldAmt;

        //1. 이전 수입과 같은 계좌인 경우
        if(oldAcctId != null && !oldAcctId.isEmpty() && old.get("ACCT_ID").equals(acctId))
            // 금액 변경이 있는 경우만 계좌 잔액 수정
            if (dif != 0) accountRepository.updateAmount(acctId, dif);

        //2. 이전 수입과 다른 계좌인 경우
        else if(oldAcctId != null && !oldAcctId.isEmpty() && !old.get("ACCT_ID").equals(acctId)) {
            //이전 계좌 잔고 차감
            accountRepository.updateAmount(oldAcctId, -oldAmt);
            //현재 계좌 잔고 증가
            accountRepository.updateAmount(acctId, newAmt);
        }

        return repository.mod(map);
    }
    
    @Transactional
    public boolean del(Map<String, String> map) {
        // 기존 수입 정보 조회
        Map<String, Object> old = repository.findById(map.get("id"));
        
        // 계좌 연결된 수입인 경우 통장 잔고 차감
        String acctId = old.get("ACCT_ID") != null ? old.get("ACCT_ID").toString() : null;
        if (acctId != null && !acctId.isEmpty()) {
            int amount = Integer.parseInt(old.get("AMT").toString());
            accountRepository.updateAmount(acctId, -amount);
        }
        
        return repository.del(map);
    }
}
