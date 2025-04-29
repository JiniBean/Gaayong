package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.CategoryService;
import com.gaayong.service.CodeService;
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
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService service;

    @Autowired
    private CodeService codeService;

    @GetMapping()
    public String page(@RequestParam(name = "c", required = false) String code,
                       @AuthenticationPrincipal User user, Model model){
        List<Map<String, Object>> list = service.getList(user.getId(), code);
        List<Map<String, String>> codeList = codeService.getList("CTG_CD");

        model.addAttribute("list", list);
        model.addAttribute("codeList", codeList);
        return "category";
    }

    @PostMapping()
    public String page(@RequestParam Map<String, String> map,
                       @RequestParam(name = "m") String method,
                       @AuthenticationPrincipal User user){

        map.put("userId", user.getId());
        try {
            boolean isValid = service.edit(map, method);
            if(isValid) return "redirect:category";
            else return "redirect:category?error=처리 중 오류가 발생했습니다.";
        } catch (Exception e) {
            return "redirect:category?error=" + e.getMessage();
        }
    }
}
