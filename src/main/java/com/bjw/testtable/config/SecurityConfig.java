package com.bjw.testtable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스/로그인 페이지는 모두 허용
                        .requestMatchers("/css/**","/js/**","/images/**","/login","/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")              // 커스텀 로그인 페이지 경로 (GET)
                        .loginProcessingUrl("/login")     // 로그인 폼의 action(POST)
                        .usernameParameter("user_id")     // 폼 <input name="user_id">
                        .passwordParameter("password")    // 폼 <input name="password">
                        .defaultSuccessUrl("/", true)     // 로그인 성공시 이동
                        .failureUrl("/login?error")       // 실패시 이동
                        .permitAll()                      // 로그인 페이지는 누구나 접근
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
