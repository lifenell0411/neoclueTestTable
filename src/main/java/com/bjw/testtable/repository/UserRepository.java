package com.bjw.testtable.repository;

import com.bjw.testtable.entity.User; // 네 패키지명에 맞춰 수정
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId); // 로그인용
}
