package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.CategoryService;
import com.gaayong.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("pay")
public class PayController {
    @Autowired
    private PayService service;

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public String page(@RequestParam(name = "error", required = false) String error,
                       @RequestParam(name = "c", required = false) String category,
                       @RequestParam(name = "y", required = false) String year,
                       @RequestParam(name = "m", required = false) String month,
                       @AuthenticationPrincipal User user, Model model) {
        if (!StringUtils.hasText(year)) year = String.valueOf(LocalDate.now().getYear());
        if (!StringUtils.hasText(month)) month = String.valueOf(LocalDate.now().getMonthValue());

        Integer total = service.getTotal(user.getId(), year, month);
        Integer unpaid = service.getUnpaid(user.getId(), year, month);
        Integer categoryTotal = service.getCategoryTotal(user.getId(), category, year, month);
        List<Map<String, Object>> list = service.getList(user.getId(), category, year, month);
        List<Map<String, Object>> categoryList = categoryService.getList(user.getId(), "E");

        model.addAttribute("error", error);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("total", total);
        model.addAttribute("unpaid", unpaid);
        model.addAttribute("categoryTotal", categoryTotal);
        model.addAttribute("list", list);
        model.addAttribute("categoryList", categoryList);
        return "pay";
    }

    @PostMapping()
    public String page(@RequestParam Map<String, String> map,
                       @RequestParam(name = "m") String method,
                       @AuthenticationPrincipal User user,
                       HttpServletRequest request) {
        map.put("userId", user.getId());

        try {
            boolean isValid = false;

            if (method.equals("add")) {
                isValid = service.add(map);
                method = "저장";
            } else if (method.equals("del")) {
                isValid = service.del(map);
                method = "삭제";
            } else if (method.equals("mod")) {
                isValid = service.mod(map);
                method = "수정";
            }

            if (isValid) return buildRedirect(map);
            return buildRedirect(map) + "?error=" + "지출 " + method + "에 실패했습니다.";
        } catch (Exception e) {
            log.error("Request failed uri={} method={} params={} msg={}",
                    request.getRequestURI(), request.getMethod(), map, e.getMessage(), e);
            return buildRedirect(map) + "?error=" + e.getMessage();
        }
    }

    private String buildRedirect(Map<String, String> map) {
        StringBuilder url = new StringBuilder("redirect:pay");
        String y = map.get("y");
        String month = map.get("month");
        String c = map.get("c");
        boolean hasParam = false;

        if (y != null && !y.isEmpty()) {
            url.append(hasParam ? "&" : "?").append("y=").append(y);
            hasParam = true;
        }
        if (month != null && !month.isEmpty()) {
            url.append(hasParam ? "&" : "?").append("m=").append(month);
            hasParam = true;
        }
        if (c != null && !c.isEmpty()) {
            url.append(hasParam ? "&" : "?").append("c=").append(c);
        }
        return url.toString();
    }
}
