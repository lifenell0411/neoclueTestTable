package com.bjw.testtable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordConfig { //비밀번호 인코더 등록 (BCrypt)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); } //시큐리티는 평문 비교를 안 함. 로그인 시 입력값과 DB의 BCrypt 해시를 matches()로 비교함.
}
