package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.CardService;
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
@RequestMapping("card")
public class CardController {
    @Autowired
    private CardService service;

    @GetMapping
    public String page(@AuthenticationPrincipal User user, Model model){
        List<Map<String, String>> list = service.getList(user.getId());
        model.addAttribute("list", list);
        return "card";
    }
    
    @PostMapping
    public String page(@RequestParam Map<String, String> map,
                      @RequestParam(name = "m") String method,
                      @AuthenticationPrincipal User user,
                      HttpServletRequest request){
        
        map.put("userId", user.getId());
        boolean isValid = service.edit(map, method);
        try {
            if(isValid) return "redirect:card";
            else return "redirect:card?error=처리 중 오류가 발생했습니다.";
        } catch (Exception e) {
            log.error("Request failed uri={} method={} params={} msg={}",
                    request.getRequestURI(), request.getMethod(), map, e.getMessage(), e);
            return "redirect:card?error=" + e.getMessage();
        }
        
        
    }
}
