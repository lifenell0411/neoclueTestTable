package com.bjw.testtable.repository;

import com.bjw.testtable.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name); // ex) ROLE_ADMIN
}
