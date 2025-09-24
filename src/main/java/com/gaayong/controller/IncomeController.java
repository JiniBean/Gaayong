package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("income")
public class IncomeController {
    @Autowired
    private IncomeService service;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AccountService accountService;

    @GetMapping()
    public String page(@RequestParam(name = "error", required = false) String error,
                       @RequestParam(name = "c", required = false) String category,
                       @RequestParam(name = "y", required = false) String year,
                       @RequestParam(name = "m", required = false) String month,
                       @AuthenticationPrincipal User user, Model model){

        Integer total = service.getTotal(user.getId(), year, month);
        Integer budgetTotal = service.getBudgetTotal(user.getId(), year, month);
        Integer extraTotal = service.getExtraTotal(user.getId(), year, month);
        Integer categoryTotal = service.getCategoryTotal(user.getId(), category, year, month);

        List<Map<String, Object>> list = service.getList(user.getId(), category, year, month);
        List<Map<String, Object>> categoryList = categoryService.getList(user.getId(), "I");
        List<Map<String, String>> accountList = accountService.getList(user.getId());

        model.addAttribute("error", error);
        model.addAttribute("total", total);
        model.addAttribute("budgetTotal", budgetTotal);
        model.addAttribute("extraTotal", extraTotal);
        model.addAttribute("categoryTotal", categoryTotal);
        model.addAttribute("list", list);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("accountList", accountList);
        return "income";
    }
    
    @PostMapping
    public String process(@RequestParam(name = "m", required = false) String method,
                          @RequestParam Map<String, String> map,
                          @AuthenticationPrincipal User user,
                          HttpServletRequest request) {

        map.put("userId", user.getId());

        try {
            boolean isValid = false;

            if(method.equals("add")) {
                isValid = service.add(map);
                method = "저장";
            }
            else if(method.equals("del")) {
                isValid = service.del(map);
                method = "삭제";
            }
            else if(method.equals("mod")) {
                isValid = service.mod(map);
                method = "수정";
            }

            if(isValid) return "redirect:income";
            else return "redirect:income?error=" + "수입 "+ method + "에 실패했습니다.";
        } catch (Exception e) {
            log.error("Request failed uri={} method={} params={} msg={}",
                    request.getRequestURI(), request.getMethod(), map, e.getMessage(), e);
            return "redirect:income?error=" + e.getMessage();
        }
    }
}
