package com.gaayong.config.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
            "/",
            "/expense",
            "/income",
            "/fixed",
            "/pay",
            "/budget",
            "/account",
            "/card",
            "/category",
            "/signin",
            "/signup"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
