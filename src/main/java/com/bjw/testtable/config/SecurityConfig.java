package com.bjw.testtable.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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
                .authenticationProvider(provider)
                // SmartEditor2가 iframe을 써서 필요
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth
                        // 표준 정적 리소스( /static/** ) 전부 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // 부트 자동 커버 밖에 있는 것들만 문자열 패턴으로 허용
                        .requestMatchers("/dist/**", "/plugins/**", "/se2/**","/error", "/js**").permitAll()
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
                .logout(lo -> lo.logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .exceptionHandling(ex -> ex.accessDeniedHandler((req, res, e) -> {
                    String msg = java.net.URLEncoder.encode("권한이 없습니다.", java.nio.charset.StandardCharsets.UTF_8);
                    res.sendRedirect("/posts/list?error=" + msg);
                }));

        return http.build();
    }
}