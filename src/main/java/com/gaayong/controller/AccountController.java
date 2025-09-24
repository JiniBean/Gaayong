package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.AccountService;
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
@RequestMapping("account")
public class AccountController {
    @Autowired
    private AccountService service;

    @GetMapping
    public String page(@AuthenticationPrincipal User user, Model model){
        Integer total = service.getTotal(user.getId());
        List<Map<String, String>> list = service.getList(user.getId());

        model.addAttribute("total", total);
        model.addAttribute("list", list);
        return "account";
    }

    @PostMapping
    public String page(@RequestParam Map<String, String> map,
                       @RequestParam(name = "m") String method,
                       @AuthenticationPrincipal User user,
                       HttpServletRequest request){
        
        map.put("userId", user.getId());
        try {
            boolean isValid = service.edit(map, method);
            if(isValid) return "redirect:account";
            else return "redirect:account?error=처리 중 오류가 발생했습니다.";
        } catch (Exception e) {
            log.error("Request failed uri={} method={} params={} msg={}",
                    request.getRequestURI(), request.getMethod(), map, e.getMessage(), e);
            return "redirect:account?error=" + e.getMessage();
        }
    }
}
