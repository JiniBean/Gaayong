package com.gaayong.controller;

import com.gaayong.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

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
        if(error != null) {
            String msg = switch (error) {
                case "1" -> "회원가입에 실패했습니다";
                case "2" -> "이미 사용하고 있는 아이디입니다";
                case "3" -> "오류가 발생했습니다";
                default -> null;
            };
            model.addAttribute("error", msg);
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam Map<String, String> user, HttpServletRequest request) {

        try {
            boolean isAdd = userService.addUser(user);
            if(isAdd) return "redirect:/signin?registered=true";
            else return "/signup?error=1";
        } catch (IllegalStateException e){
            log.error("Request failed uri={} method={} params={} msg={}",
                    request.getRequestURI(), request.getMethod(), user, e.getMessage(), e);
            return "redirect:/signup?error=2";
        }
        catch (Exception e) {
            log.error("Request failed uri={} method={} params={} msg={}",
                    request.getRequestURI(), request.getMethod(), user, e.getMessage(), e);
            return "redirect:/signup?error=3";
        }
    }
} 