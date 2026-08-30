package com.geisivan.userservice.infrastructure.security.service;

import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.auth.MainUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService {

    private final UserRepository userRepository;
    private static final String USER_NOT_FOUND = "User not found";

    public UserDetailsServiceImpl(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    @NonNull
    public MainUser loadUserByUsername(@NonNull String email) {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                USER_NOT_FOUND));

        return map(user);
    }

    public MainUser loadUserById(Long id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                USER_NOT_FOUND));

        return map(user);
    }

    private MainUser map(User user) {
        var roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new MainUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                roles,
                user.getStatus()
        );
    }
}
