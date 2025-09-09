package com.bjw.testtable.domain.user.repository;

import com.bjw.testtable.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
           select u
           from User u
           join fetch u.role
           where u.userId = :userId
           """)
    Optional<User> findByUserId(String userId); // 로그인용
}
