package com.gaayong.service;

import com.gaayong.repository.AccountRepository;
import com.gaayong.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ExpenseServiceImp implements ExpenseService{
    @Autowired
    private ExpenseRepository repository;

    @Autowired
    private AccountRepository accountRepository;

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

    @Transactional
    @Override
    public boolean add(Map<String, String> map) {

        // 계좌 연결된 지출인 경우 통장 잔고 차감
        String acctId = map.get("acctId");
        if (acctId != null && !acctId.isEmpty()) {
            int amount = Integer.parseInt(map.get("amt"));
            accountRepository.updateAmount(acctId, -amount);
        }
        return repository.save(map);
    }

    @Transactional
    @Override
    public boolean del(Map<String, String> map) {
        // 삭제 전 해당 지출 정보 조회
        Map<String, Object> expense;
        if(map.get("fixedId") != null && !map.get("fixedId").isEmpty())
            expense = repository.findByFixedId(map.get("fixedId"));
        else
            expense = repository.findById(map.get("id"));

        map.put("id", expense.get("ID").toString());

        // 계좌 연결된 지출인 경우 통장 잔고 복원
        String acctId = expense.get("ACCT_ID") != null ? expense.get("ACCT_ID").toString() : null;
        if (acctId != null && !acctId.isEmpty()) {
            int amount = Integer.parseInt(expense.get("AMT").toString());
            accountRepository.updateAmount(acctId, amount);
        }
        
        return repository.del(map);
    }

    @Transactional
    @Override
    public boolean mod(Map<String, String> map) {
        // 기존 지출 정보 조회
        Map<String, Object> old = repository.findById(map.get("id"));
        String oldAcctId = old.get("ACCT_ID") != null ? old.get("ACCT_ID").toString() : null;
        String acctId = map.get("acctId");

        // 기존 금액, 현재 금액
        int oldAmt = Integer.parseInt(old.get("AMT").toString());
        int newAmt = Integer.parseInt(map.get("amt"));
        int dif = oldAmt - newAmt;

        // 계좌 연결된 지출인 경우 통장 잔고 처리
        if (acctId != null && !acctId.isEmpty()) {
            // 1. 이전 지출이 카드인 경우
            if(oldAcctId == null || oldAcctId.isEmpty())
                //현재 계좌 잔고 차감
                accountRepository.updateAmount(acctId, -newAmt);
            // 2. 이전 지출과 같은 계좌인 경우
            else if(old.get("ACCT_ID").equals(acctId))
                // 금액 변경이 있는 경우만 계좌 잔액 수정
                if (dif != 0) accountRepository.updateAmount(acctId, dif);
            // 3. 이전 지출과 다른 계좌인 경우
            else {
                //이전 계좌 잔고 복원
                accountRepository.updateAmount(oldAcctId, oldAmt);
                //현재 계좌 잔고 차감
                accountRepository.updateAmount(acctId, -newAmt);
            }
        }
        // 4. 이전 지출은 통장, 현재 지출은 카드인 경우
        else if(oldAcctId != null && !oldAcctId.isEmpty() && (acctId == null || acctId.isEmpty())) 
            //이전 계좌 잔고 복원
            accountRepository.updateAmount(oldAcctId, oldAmt);

        return repository.mod(map);
    }

    @Override
    public List<Map<String, Object>> getCardPmt(String id) {
        return repository.findCardPmt(id);
    }
}
