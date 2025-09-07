package com.bjw.testtable.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
     public class LoginController {

    @GetMapping("/login") //주소
    public String test(Model model) {


        return "home";//hello라는 html찾으러가야됨
    }
}