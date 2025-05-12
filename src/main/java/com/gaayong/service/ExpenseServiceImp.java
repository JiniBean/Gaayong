package com.gaayong.service;

import com.gaayong.repository.AccountRepository;
import com.gaayong.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        // 1. 계좌 연결된 지출인 경우 통장 잔고 처리
        System.out.println("==============================");
        System.out.println();
        System.out.println(map);
        System.out.println();
        String acctId = map.get("acctId");
        System.out.println("acctId : " + acctId);
        if (acctId != null && !acctId.isEmpty()) {
            int amount = Integer.parseInt(map.get("amt"));
            // 지출이므로 계좌 금액을 감소시킴 (음수로 전달)
            accountRepository.updateAmount(acctId, -amount);
        }
        return repository.save(map);
    }

    @Transactional
    @Override
    public boolean del(Map<String, String> map) {
        // 삭제 전 해당 지출 정보 조회
        Map<String, String> expense = repository.findById(map.get("id"));
        
        // 계좌 연결된 지출인 경우 통장 잔고 복원
        String acctId = expense.get("ACCT_ID");
        if (acctId != null && !acctId.isEmpty()) {
            int amount = Integer.parseInt(expense.get("AMT"));
            // 지출 삭제이므로 계좌 금액을 증가시킴 (양수로 전달)
            accountRepository.updateAmount(acctId, amount);
        }
        
        return repository.del(map);
    }

    @Transactional
    @Override
    public boolean mod(Map<String, String> map) {
        // 기존 지출 정보 조회
        Map<String, String> prevExpense = repository.findById(map.get("id"));
        
        // 1. 계좌 연결된 지출인 경우 통장 잔고 처리
        String acctId = map.get("acctId");
        if (acctId != null && !acctId.isEmpty()) {
            int newAmount = Integer.parseInt(map.get("amt"));
            int oldAmount = Integer.parseInt(prevExpense.get("AMT"));
            int amountDiff = oldAmount - newAmount;
            
            // 금액 변경이 있는 경우만 계좌 잔액 수정
            if (amountDiff != 0) {
                accountRepository.updateAmount(acctId, amountDiff);
            }
        }
        
        // 2. 현금 결제 내역인 경우 처리 (CARD_ID와 ACCT_ID가 모두 null이면 현금 결제로 간주)
        String cardId = map.get("cardId");
        if ((acctId == null || acctId.isEmpty()) && (cardId == null || cardId.isEmpty())) {
            // 현금 결제 내역에 대한 특별 처리 로직
            // 현재는 별도 처리 없음, 필요시 구현
        }
        
        return repository.mod(map);
    }
}
