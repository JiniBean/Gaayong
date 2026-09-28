package com.gaayong.api;

import com.gaayong.entity.User;
import com.gaayong.service.AccountService;
import com.gaayong.service.BudgetService;
import com.gaayong.service.ExpenseService;
import com.gaayong.service.FixedService;
import com.gaayong.service.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gaayong.api.util.MapCamelCase.toCamelCaseMaps;

@RestController("apiHomeController")
@RequestMapping("/api/home")
public class HomeController {

    private final AccountService accountService;
    private final FixedService fixedService;
    private final ExpenseService expenseService;
    private final BudgetService budgetService;
    private final IncomeService incomeService;

    public HomeController(AccountService accountService,
                          FixedService fixedService,
                          ExpenseService expenseService,
                          BudgetService budgetService,
                          IncomeService incomeService) {
        this.accountService = accountService;
        this.fixedService = fixedService;
        this.expenseService = expenseService;
        this.budgetService = budgetService;
        this.incomeService = incomeService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> dashboard(@AuthenticationPrincipal User user) {
        String userId = user.getId();
        int cash = nullToZero(accountService.getTotal(userId));
        int unpaidFixed = nullToZero(fixedService.getUnpaid(userId));
        List<Map<String, Object>> cardPmtList = expenseService.getCardPmt(userId);
        int cardPmt = cardPmtList.stream()
                .mapToInt(row -> {
                    Object total = row.get("total");
                    if (total == null) total = row.get("TOTAL");
                    return total != null ? Integer.parseInt(total.toString()) : 0;
                })
                .sum();
        int expenseTotal = nullToZero(expenseService.getTotal(userId, null, null));
        int fixedTotal = nullToZero(fixedService.getTotal(userId));
        int budgetTotal = nullToZero(budgetService.getTotal(userId));
        int incomeTotal = nullToZero(incomeService.getTotal(userId, null, null));
        int availAmt = cash - unpaidFixed - cardPmt;
        int expAvailAmt = budgetTotal - fixedTotal;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("cash", cash);
        body.put("unpaidFixed", unpaidFixed);
        body.put("cardPmt", cardPmt);
        body.put("cardPmtList", toCamelCaseMaps(cardPmtList));
        body.put("expenseTotal", expenseTotal);
        body.put("fixedTotal", fixedTotal);
        body.put("budgetTotal", budgetTotal);
        body.put("incomeTotal", incomeTotal);
        body.put("availAmt", availAmt);
        body.put("expAvailAmt", expAvailAmt);
        return ResponseEntity.ok(body);
    }

    private int nullToZero(Integer value) {
        return value != null ? value : 0;
    }
}
