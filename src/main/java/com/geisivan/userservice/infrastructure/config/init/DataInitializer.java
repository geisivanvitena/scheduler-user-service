package com.geisivan.userservice.infrastructure.config.init;

import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initRoles(RoleRepository repository) {
        return args -> {

            if (repository.findByName(RoleName.ROLE_ADMIN).isEmpty()) {
                repository.save(new Role(null, RoleName.ROLE_ADMIN));
            }

            if (repository.findByName(RoleName.ROLE_USER).isEmpty()) {
                repository.save(new Role(null, RoleName.ROLE_USER));
            }
        };
    }
}
