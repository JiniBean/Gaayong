package com.gaayong.api;

import com.gaayong.api.util.ApiBodyHelper;
import com.gaayong.api.util.ApiDateHelper;
import com.gaayong.entity.User;
import com.gaayong.service.AccountService;
import com.gaayong.service.CardService;
import com.gaayong.service.CategoryService;
import com.gaayong.service.ExpenseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.gaayong.api.util.MapCamelCase.toCamelCaseMaps;

@Slf4j
@RestController("apiExpenseController")
@RequestMapping("/api/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final CardService cardService;

    public ExpenseController(ExpenseService expenseService,
                             CategoryService categoryService,
                             AccountService accountService,
                             CardService cardService) {
        this.expenseService = expenseService;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(name = "y", required = false) String year,
            @RequestParam(name = "month", required = false) String month,
            @RequestParam(name = "c", required = false) String category,
            @AuthenticationPrincipal User user) {
        String y = ApiDateHelper.defaultYear(year);
        String m = ApiDateHelper.defaultMonth(month);
        String userId = user.getId();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("year", Integer.parseInt(y));
        body.put("month", Integer.parseInt(m));
        body.put("total", expenseService.getTotal(userId, y, m));
        body.put("varTotal", expenseService.getVarTotal(userId, y, m));
        body.put("fixedTotal", expenseService.getFixedTotal(userId, y, m));
        body.put("categoryTotal", category != null
                ? expenseService.getCategoryTotal(userId, category, y, m) : 0);
        body.put("list", toCamelCaseMaps(expenseService.getList(userId, category, y, m)));
        body.put("categoryList", toCamelCaseMaps(categoryService.getList(userId, "E")));
        body.put("accountList", toCamelCaseMaps(accountService.getList(userId)));
        body.put("cardList", toCamelCaseMaps(cardService.getList(userId)));
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = ApiBodyHelper.allBodyFields(body, user.getId(), null);
        if (!map.containsKey("type")) {
            map.put("type", "VAR");
        }
        return mutate(map, "add", "저장");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable String id,
                                                      @RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = ApiBodyHelper.allBodyFields(body, user.getId(), id);
        return mutate(map, "mod", "수정");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id,
                                                       @AuthenticationPrincipal User user) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("id", id);
        return mutate(map, "del", "삭제");
    }

    private ResponseEntity<Map<String, String>> mutate(Map<String, String> map, String method, String label) {
        try {
            boolean ok = switch (method) {
                case "add" -> expenseService.add(map);
                case "mod" -> expenseService.mod(map);
                case "del" -> expenseService.del(map);
                default -> false;
            };
            if (ok) {
                if ("add".equals(method)) {
                    return ResponseEntity.status(201).body(Map.of("message", "지출이 " + label + "되었습니다."));
                }
                return ResponseEntity.ok(Map.of("message", "지출이 " + label + "되었습니다."));
            }
            return ResponseEntity.status(500).body(Map.of("error", "지출 " + label + "에 실패했습니다."));
        } catch (Exception e) {
            log.error("Expense {} failed: {}", method, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
