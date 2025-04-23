package com.gaayong.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("fixed")
public class FixedController {

    @GetMapping
    public String page(){
        return "fixed";
    }
}
