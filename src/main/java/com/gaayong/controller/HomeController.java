package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private FixedService fixedService;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private IncomeService incomeService;

    @RequestMapping("/")
    public String home(@AuthenticationPrincipal User user, Model model){

        if(user == null) {
            List<Map<String, Object>> cardPmtList = List.of(Map.of("total", 0, "NAME", "카드명"));
            model.addAttribute("user", 0);
            model.addAttribute("cash", 0);
            model.addAttribute("unpaidFixed", 0);
            model.addAttribute("cardPmtList", cardPmtList);
            model.addAttribute("cardPmt", 0);
            model.addAttribute("expenseTotal", 0);
            model.addAttribute("budgetTotal", 0);
            model.addAttribute("incomeTotal", 0);
            model.addAttribute("availAmt", 0);
            model.addAttribute("expAvailAmt", 0);
            return "home";
        }
        int cash = accountService.getTotal(user.getId());
        int unpaidFixed = fixedService.getUnpaid(user.getId());
        List<Map<String, Object>> cardPmtList = expenseService.getCardPmt(user.getId());
        int cardPmt = 0;
        for (Map<String, Object> map : cardPmtList) cardPmt += Integer.parseInt(map.get("total").toString());

        int expenseTotal = expenseService.getTotal(user.getId(), null, null);
        int fixedTotal = fixedService.getTotal(user.getId());
        int budgetTotal = budgetService.getTotal(user.getId());
        int incomeTotal = incomeService.getTotal(user.getId(), null, null);

        int availAmt = cash - unpaidFixed - cardPmt;
        int expAvailAmt = budgetTotal - fixedTotal;

        model.addAttribute("cash", cash);
        model.addAttribute("unpaidFixed", unpaidFixed);
        model.addAttribute("cardPmtList", cardPmtList);
        model.addAttribute("cardPmt", cardPmt);
        model.addAttribute("expenseTotal", expenseTotal);
        model.addAttribute("budgetTotal", budgetTotal);
        model.addAttribute("incomeTotal", incomeTotal);
        model.addAttribute("availAmt", availAmt);
        model.addAttribute("expAvailAmt", expAvailAmt);
        return "home";
    }
}
