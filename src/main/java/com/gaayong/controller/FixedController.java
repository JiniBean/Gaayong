package com.gaayong.controller;

import com.gaayong.entity.User;
import com.gaayong.service.CategoryService;
import com.gaayong.service.FixedService;
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
@RequestMapping("fixed")
public class FixedController {
    @Autowired
    private FixedService service;
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping()
    public String page(@RequestParam(name = "error", required = false) String error,
                       @RequestParam(name = "f", required = false) Boolean flag,
                       @AuthenticationPrincipal User user, Model model){
        Integer total = service.getTotal(user.getId());
        Integer unpaid = service.getUnpaid(user.getId());
        List<Map<String, Object>> list = service.getList(user.getId(), flag);
        List<Map<String, Object>> categoryList = categoryService.getList(user.getId(), "E");

        model.addAttribute("error", error);
        model.addAttribute("total", total);
        model.addAttribute("unpaid", unpaid);
        model.addAttribute("list", list);
        model.addAttribute("categoryList", categoryList);
        return "fixed";
    }


    @PostMapping()
    public String page(@RequestParam Map<String, String> map,
                       @RequestParam(name = "m") String method,
                       @AuthenticationPrincipal User user){

        map.put("userId", user.getId());

        System.out.println(map);
        try {
            boolean isValid = service.edit(map, method);
            if(isValid) return "redirect:fixed";
            else return "redirect:fixed/error=" + "고정비용 변경에 실패했습니다.";
        } catch (Exception e) {
            return "redirect:fixed/error=" + e.getMessage();
        }
    }
}
