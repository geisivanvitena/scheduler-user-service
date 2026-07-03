package com.geisivan.userservice.infrastructure.config.init;

import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Set;

@Slf4j
@Configuration
public class DataInitializer {

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            createRoleIfNotExists(roleRepository, RoleName.ROLE_ADMIN);
            createRoleIfNotExists(roleRepository, RoleName.ROLE_USER);
            createAdminIfNotExists(roleRepository, userRepository, passwordEncoder);
        };
    }

    private void createRoleIfNotExists(
            RoleRepository roleRepository,
            RoleName roleName) {

        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder()
                    .name(roleName)
                    .description(switch (roleName) {
                        case ROLE_ADMIN -> "Administrator role with full access";
                        case ROLE_USER -> "Standard user role with limited access";
                    })
                    .build();

            roleRepository.save(role);

            log.info("Default role created successfully: {}", roleName);
        }
    }

    private void createAdminIfNotExists(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.debug("Default admin already exists: {}", adminEmail);

            return;
        }

        Role adminRole = roleRepository
                .findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Default role ROLE_ADMIN not found during application initialization"));

        User admin = User.builder()
                .name(adminName)
                .email(adminEmail)
                .passwordHash(passwordEncoder.encode(adminPassword))
                .status(UserStatus.ACTIVE)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);

        log.info("Default admin created successfully: {}", adminEmail);
    }
}
