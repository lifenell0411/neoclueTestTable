package com.bjw.testtable.domain.user.repository;

import com.bjw.testtable.domain.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    @Query("""
           select u
           from AppUser u
           join fetch u.role
           where u.userId = :userId
           """)
    Optional<AppUser> findByUserId(String userId);
}