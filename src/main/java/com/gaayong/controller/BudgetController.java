package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.BudgetService;
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

@Controller
@RequestMapping("budget")
public class BudgetController {
    @Autowired
    private BudgetService service;

    @GetMapping()
    public String page(@AuthenticationPrincipal User user, Model model){
        Integer total = service.getTotal(user.getId());
        List<Map<String, Object>> list = service.getList(user.getId());

        model.addAttribute("total", total);
        model.addAttribute("list", list);
        return "budget";
    }

    @PostMapping()
    public String page(@RequestParam Map<String, String> map,
                       @RequestParam(name = "m") String method,
                       @AuthenticationPrincipal User user){

        map.put("userId", user.getId());
        boolean isValid = service.edit(map, method);

        return "redirect:budget";
    }
}
