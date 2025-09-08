package com.bjw.testtable.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    // SecurityConfig.java
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
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/dist/**", "/plugins/**", "/login", "/error","/posts/create","/posts/**").permitAll()
                        .anyRequest().authenticated()
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
                .logout(l -> l.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());
        return http.build();
    }
}