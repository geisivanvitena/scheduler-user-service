package com.geisivan.userservice.infrastructure.repository;

import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
