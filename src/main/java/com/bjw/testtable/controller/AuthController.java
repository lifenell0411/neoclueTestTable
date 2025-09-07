package com.bjw.testtable.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage() {
        // templates/login.html이 생기면 그걸 렌더링함
        return "login";
    }
}
