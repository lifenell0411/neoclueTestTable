package com.bjw.testtable.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public DaoAuthenticationProvider daoAuthProvider(UserDetailsService uds,
                                                     BCryptPasswordEncoder encoder) {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(encoder); // ★ 여기서 BCrypt 강제
        return p;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider provider) throws Exception {
        http
                .authenticationProvider(provider) // ★ 명시적으로 등록
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/dist/**", "/plugins/**", "/login", "/error","/test").permitAll()
                        .anyRequest().authenticated()
                )
                // ★ 403(권한없음) → /posts/list?error=... 으로 리다이렉트
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((req, res, e) -> {
                            String msg = java.net.URLEncoder.encode("권한이 없습니다.", java.nio.charset.StandardCharsets.UTF_8);
                            res.sendRedirect("/posts/list?error=" + msg);
                        })
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("user_id")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/posts/list", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")                  // 로그아웃 요청 URL
                        .logoutSuccessUrl("/login?logout")     // 로그아웃 후 이동할 페이지
                        .invalidateHttpSession(true)           // 세션 무효화
                        .deleteCookies("JSESSIONID"));          // 세션 쿠키 삭제);
        return http.build();
    }


}