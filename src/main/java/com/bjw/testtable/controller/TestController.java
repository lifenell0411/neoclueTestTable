package com.bjw.testtable.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;




@Controller

public class TestController {

    @GetMapping("/hello") //주소
    public String test(Model model){
        model.addAttribute("msg","hello world");//모델에 밸류값 담음
        model.addAttribute("name","jiwon");

        return "hello";//hello라는 html찾으러가야됨
    }

}
