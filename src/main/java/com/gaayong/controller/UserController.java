package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Slf4j
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/signin")
    public String signin(@RequestParam(required = false) String error, Model model) {
        if(error != null)
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");

        return "signin";
    }

    @GetMapping("/signup")
    public String signup(@RequestParam(required = false) String error, Model model) {
        if(error != null)
            model.addAttribute("error", error);
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam Map<String, String> user) {

        try {
            int isAdd = userService.addUser(user);
            if(isAdd == 1) return "redirect:/signin?registered=true";
            else return "redirect:/signup?error=" + "회원 등록에 실패했습니다";
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage(), e);
            return "redirect:/signup?error=" + e.getMessage();
        }
    }
} 