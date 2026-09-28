package com.gaayong.api;

import com.gaayong.entity.User;
import com.gaayong.service.CategoryService;
import com.gaayong.service.CodeService;
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

import com.gaayong.api.util.ApiBodyHelper;
import com.gaayong.api.util.MapCamelCase;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.gaayong.api.util.MapCamelCase.toCamelCaseMaps;

@Slf4j
@RestController("apiCategoryController")
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final CodeService codeService;

    public CategoryController(CategoryService categoryService, CodeService codeService) {
        this.categoryService = categoryService;
        this.codeService = codeService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(name = "c", required = false) String filter,
            @AuthenticationPrincipal User user) {
        List<Map<String, Object>> list = categoryService.getList(user.getId(), filter);
        List<Map<String, String>> codeList = codeService.getList("CTG_CD");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("codeList", toCamelCaseMaps(codeList));
        body.put("list", toCamelCaseMaps(list));
        body.put("filter", filter);
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> create(@RequestBody Map<String, String> body,
                                                      @AuthenticationPrincipal User user) {
        Map<String, String> map = toServiceMap(body, user.getId(), null);
        return edit(map, "add", "저장");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@PathVariable String id,
                                                     @RequestBody Map<String, String> body,
                                                     @AuthenticationPrincipal User user) {
        Map<String, String> map = toServiceMap(body, user.getId(), id);
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
            if (categoryService.edit(map, method)) {
                if ("add".equals(method)) {
                    return ResponseEntity.status(201).body(Map.of("message", "카테고리가 " + label + "되었습니다."));
                }
                return ResponseEntity.ok(Map.of("message", "카테고리가 " + label + "되었습니다."));
            }
            return ResponseEntity.status(500).body(Map.of("error", "카테고리 " + label + "에 실패했습니다."));
        } catch (Exception e) {
            log.error("Category {} failed: {}", method, e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, String> toServiceMap(Map<String, String> body, String userId, String id) {
        return ApiBodyHelper.toServiceMap(body, userId, id, "name", "des", "ctgCd");
    }
}
