package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    @PostMapping("/login-fail")
    public String failPage() {
        return "loginPages/login-fail";
    }

    @GetMapping("/forbidden")
    public String forbiddenPage() {
        return "loginPages/forbidden";
    }
}
