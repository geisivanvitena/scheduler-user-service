package com.geisivan.userservice.infrastructure.config.init;

import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.infrastructure.exception.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${ADMIN_NAME}")
    private String adminName;

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD}")
    private String adminPassword;

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder encoder
    ) {
        return args -> {
            createRoleIfNotExists(
                    roleRepository,
                    RoleName.ROLE_ADMIN
            );

            createRoleIfNotExists(
                    roleRepository,
                    RoleName.ROLE_USER
            );

            createAdminIfNotExists(
                    roleRepository,
                    userRepository,
                    encoder
            );
        };
    }

    private void createRoleIfNotExists(
            RoleRepository repository,
            RoleName roleName
    ) {

        boolean exists =
                repository.findByName(roleName).isPresent();

        if (!exists) {
            repository.save(new Role(null, roleName));
        }
    }

    private void createAdminIfNotExists(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder encoder
    ) {

        boolean adminExists =
                userRepository.existsByEmail(adminEmail);

        if (adminExists) {
            return;
        }

        Role adminRole = roleRepository
                .findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ROLE_ADMIN not found"));

        User admin = new User();

        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(encoder.encode(adminPassword));
        admin.getRoles().add(adminRole);

        userRepository.save(admin);
    }
}
