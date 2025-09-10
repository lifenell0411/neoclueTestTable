package com.bjw.testtable.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        // templates/login.html이 생기면 그걸 렌더링함
        return "login";
    }


    @GetMapping("/test")
    public String test(Model model){
        model.addAttribute("msg","Hello World");

        return "test";
    }

}
