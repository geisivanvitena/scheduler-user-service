package com.geisivan.userservice.application.service;

import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.util.AuthenticatedUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getAuthenticatedUser() {

        Long userId = AuthenticatedUserUtil.getId();

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Authenticated user not found"));
    }
}
