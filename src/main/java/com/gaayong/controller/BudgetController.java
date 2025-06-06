package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.BudgetService;
import com.gaayong.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("budget")
public class BudgetController {
    @Autowired
    private BudgetService service;

    @Autowired
    private CategoryService categoryService;

    @GetMapping()
    public String page(@AuthenticationPrincipal User user, Model model){
        Integer total = service.getTotal(user.getId());
        List<Map<String, Object>> list = service.getList(user.getId());
        List<Map<String, Object>> categoryList = categoryService.getList(user.getId(), "I");

        model.addAttribute("total", total);
        model.addAttribute("list", list);
        model.addAttribute("categoryList", categoryList);
        return "budget";
    }

    @PostMapping()
    public String page(@RequestParam Map<String, String> map,
                       @RequestParam(name = "m") String method,
                       @AuthenticationPrincipal User user){

        map.put("userId", user.getId());
        try {
            boolean isValid = service.edit(map, method);
            if(isValid) return "redirect:budget";
            else return "redirect:budget?error=" + "예산 변경에 실패했습니다.";
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage(), e);
            return "redirect:budget?error=" + e.getMessage();
        }
    }
}
