package com.bjw.testtable.user.service;

import com.bjw.testtable.domain.user.AppUser;
import com.bjw.testtable.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 기본 로그인 페이지는 파라미터 name="username" → 우리는 이를 users.user_id와 매칭
        AppUser u = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));


        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUserId())// ← Security의 username
                .password(u.getPassword()) // DB의 BCrypt 해시
                .authorities(new SimpleGrantedAuthority(u.getRole().getName()))
                .build();
    }
}