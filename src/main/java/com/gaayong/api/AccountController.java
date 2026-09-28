package com.gaayong.api;

import com.gaayong.api.util.ApiBodyHelper;
import com.gaayong.entity.User;
import com.gaayong.service.AccountService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.gaayong.api.util.MapCamelCase.toCamelCaseMaps;

@Slf4j
@RestController("apiAccountController")
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(@AuthenticationPrincipal User user) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("total", accountService.getTotal(user.getId()));
        body.put("list", toCamelCaseMaps(accountService.getList(user.getId())));
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = ApiBodyHelper.toServiceMap(body, user.getId(), null,
                "bank", "name", "accNum", "amt");
        return edit(map, "add", "저장");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable String id,
                                                      @RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = ApiBodyHelper.toServiceMap(body, user.getId(), id,
                "bank", "name", "accNum", "amt");
        return edit(map, "mod", "수정");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("id", id);
        return edit(map, "del", "삭제");
    }

    private ResponseEntity<Map<String, String>> edit(Map<String, String> map, String method, String label) {
        try {
            if (accountService.edit(map, method)) {
                if ("add".equals(method)) {
                    return ResponseEntity.status(201).body(Map.of("message", "통장이 " + label + "되었습니다."));
                }
                return ResponseEntity.ok(Map.of("message", "통장이 " + label + "되었습니다."));
            }
            return ResponseEntity.status(500).body(Map.of("error", "통장 " + label + "에 실패했습니다."));
        } catch (Exception e) {
            log.error("Account {} failed: {}", method, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
