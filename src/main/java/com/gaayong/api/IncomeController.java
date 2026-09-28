package com.gaayong.api;

import com.gaayong.api.util.ApiBodyHelper;
import com.gaayong.api.util.ApiDateHelper;
import com.gaayong.entity.User;
import com.gaayong.service.AccountService;
import com.gaayong.service.CategoryService;
import com.gaayong.service.IncomeService;
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
@RestController("apiIncomeController")
@RequestMapping("/api/income")
public class IncomeController {

    private final IncomeService incomeService;
    private final CategoryService categoryService;
    private final AccountService accountService;

    public IncomeController(IncomeService incomeService,
                            CategoryService categoryService,
                            AccountService accountService) {
        this.incomeService = incomeService;
        this.categoryService = categoryService;
        this.accountService = accountService;
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
        body.put("total", incomeService.getTotal(userId, y, m));
        body.put("budgetTotal", incomeService.getBudgetTotal(userId, y, m));
        body.put("extraTotal", incomeService.getExtraTotal(userId, y, m));
        body.put("categoryTotal", category != null
                ? incomeService.getCategoryTotal(userId, category, y, m) : 0);
        body.put("list", toCamelCaseMaps(incomeService.getList(userId, category, y, m)));
        body.put("categoryList", toCamelCaseMaps(categoryService.getList(userId, "I")));
        body.put("accountList", toCamelCaseMaps(accountService.getList(userId)));
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = ApiBodyHelper.allBodyFields(body, user.getId(), null);
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
                case "add" -> incomeService.add(map);
                case "mod" -> incomeService.mod(map);
                case "del" -> incomeService.del(map);
                default -> false;
            };
            if (ok) {
                if ("add".equals(method)) {
                    return ResponseEntity.status(201).body(Map.of("message", "수입이 " + label + "되었습니다."));
                }
                return ResponseEntity.ok(Map.of("message", "수입이 " + label + "되었습니다."));
            }
            return ResponseEntity.status(500).body(Map.of("error", "수입 " + label + "에 실패했습니다."));
        } catch (Exception e) {
            log.error("Income {} failed: {}", method, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
