package com.bjw.testtable.domain.user.repository;

import com.bjw.testtable.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // ex) ROLE_ADMIN
}
