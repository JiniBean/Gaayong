package com.gaayong.api;

import com.gaayong.api.util.ApiBodyHelper;
import com.gaayong.entity.User;
import com.gaayong.service.AccountService;
import com.gaayong.service.CardService;
import com.gaayong.service.CategoryService;
import com.gaayong.service.FixedService;
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
@RestController("apiFixedController")
@RequestMapping("/api/fixed")
public class FixedController {

    private final FixedService fixedService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final CardService cardService;

    public FixedController(FixedService fixedService,
                           CategoryService categoryService,
                           AccountService accountService,
                           CardService cardService) {
        this.fixedService = fixedService;
        this.categoryService = categoryService;
        this.accountService = accountService;
        this.cardService = cardService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(name = "f", required = false) String paidFilter,
            @AuthenticationPrincipal User user) {
        String userId = user.getId();
        Boolean flag = parsePaidFilter(paidFilter);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("total", fixedService.getTotal(userId));
        body.put("unpaid", fixedService.getUnpaid(userId));
        body.put("list", toCamelCaseMaps(fixedService.getList(userId, flag)));
        body.put("categoryList", toCamelCaseMaps(categoryService.getList(userId, "E")));
        body.put("accountList", toCamelCaseMaps(accountService.getList(userId)));
        body.put("cardList", toCamelCaseMaps(cardService.getList(userId)));
        body.put("filter", paidFilter);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = ApiBodyHelper.allBodyFields(body, user.getId(), null);
        if (!map.containsKey("type")) {
            map.put("type", "FIX");
        }
        normalizeCheckbox(map, "isPaid");
        normalizeCheckbox(map, "isAutoPay");
        return mutate(map, "add", "저장");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable String id,
                                                      @RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = ApiBodyHelper.allBodyFields(body, user.getId(), id);
        normalizeCheckbox(map, "isPaid");
        normalizeCheckbox(map, "isAutoPay");
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

    private Boolean parsePaidFilter(String paidFilter) {
        if (paidFilter == null || paidFilter.isEmpty()) {
            return null;
        }
        return Boolean.parseBoolean(paidFilter);
    }

    private void normalizeCheckbox(Map<String, String> map, String key) {
        if (!map.containsKey(key)) {
            return;
        }
        String value = map.get(key);
        if ("true".equals(value) || "on".equals(value)) {
            map.put(key, "on");
        } else {
            map.remove(key);
        }
    }

    private ResponseEntity<Map<String, String>> mutate(Map<String, String> map, String method, String label) {
        try {
            boolean ok = switch (method) {
                case "add" -> fixedService.add(map);
                case "mod" -> fixedService.mod(map);
                case "del" -> fixedService.del(map);
                default -> false;
            };
            if (ok) {
                if ("add".equals(method)) {
                    return ResponseEntity.status(201).body(Map.of("message", "고정지출이 " + label + "되었습니다."));
                }
                return ResponseEntity.ok(Map.of("message", "고정지출이 " + label + "되었습니다."));
            }
            return ResponseEntity.status(500).body(Map.of("error", "고정지출 " + label + "에 실패했습니다."));
        } catch (Exception e) {
            log.error("Fixed {} failed: {}", method, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
