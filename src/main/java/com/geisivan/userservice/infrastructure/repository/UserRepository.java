package com.geisivan.userservice.infrastructure.repository;

import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("""
           SELECT DISTINCT u
           FROM User u
           LEFT JOIN FETCH u.roles
           WHERE u.id = :id
           """)
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    @Query("""
           SELECT DISTINCT u
           FROM User u
           LEFT JOIN FETCH u.roles
           WHERE u.email = :email
           """)
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        LEFT JOIN u.roles r
        WHERE (:status IS NULL OR u.status = :status)
        AND (:role IS NULL OR r.name = :role)
        """)
    Page<User> findAllWithFilters(
            @Param("status") UserStatus status,
            @Param("role") RoleName role,
            Pageable pageable);
}
