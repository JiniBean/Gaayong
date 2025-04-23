package com.gaayong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("income")
public class IncomeController {

    @GetMapping
    public String page(){
        return "income";
    }
}
