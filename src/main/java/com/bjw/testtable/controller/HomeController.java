package com.bjw.testtable.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
     public class HomeController {

    @GetMapping("/") //주소
    public String home(@AuthenticationPrincipal UserDetails user) { //스프링시큐리티 전용 어노테이션. 디테일은 스프링 시큐리티가 관리하는 표준 사용자 정보 인터페이스(유저네임, 패스워드, 권한목록등)
        return "Hello, world " + user.getUsername() + " (로그인 성공)"; //로그인한 사용자의 정보를 세션에서 꺼내와서 user변수에 넣어줌
    }


}