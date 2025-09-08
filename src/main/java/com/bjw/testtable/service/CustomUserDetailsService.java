package com.bjw.testtable.service;

import com.bjw.testtable.entity.User;
import com.bjw.testtable.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 기본 로그인 페이지는 파라미터 name="username" → 우리는 이를 users.user_id와 매칭
        User u = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));

        // DB의 role.name은 반드시 "ROLE_ADMIN", "ROLE_USER" 형태여야 함
        var authorities = List.of(new SimpleGrantedAuthority(u.getRole().getName()));

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUserId())
                .password(u.getPassword()) // DB의 BCrypt 해시
                .authorities(new SimpleGrantedAuthority(u.getRole().getName()))
                .build();
    }
}
