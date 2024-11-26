package com.chensoul.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(Authentication authentication) {
        return "Hello, " + getName(authentication) + "!";
    }

    private static String getName(Authentication authentication) {
        return authentication!=null ? authentication.getName():"unknown";
    }

}