package com.geisivan.userservice.infrastructure.security.service;

import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.auth.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;
    private static final String USER_NOT_FOUND = "User not found";

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserPrincipal loadUserByUsername(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                USER_NOT_FOUND));

        return map(user);
    }

    public UserPrincipal loadUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                USER_NOT_FOUND));

        return map(user);
    }

    private UserPrincipal map(User user) {
        var roles = (user.getRoles() == null)
                ? new HashSet<RoleName>()
                : user.getRoles()
                  .stream()
                  .map(Role::getName)
                  .collect(Collectors.toSet());

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                roles
        );
    }
}