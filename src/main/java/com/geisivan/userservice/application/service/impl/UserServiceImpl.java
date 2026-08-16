package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.CurrentUserService;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.application.validator.UserValidator;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CurrentUserService currentUserService;
    private final UserValidator  userValidator;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO findUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO findAuthenticatedUser() {

        User user = currentUserService.getAuthenticatedUser();

        return userMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO updateAuthenticatedUser(UserUpdateRequestDTO dto) {

        User user = currentUserService.getAuthenticatedUser();

        userValidator.validateEmailUpdate(user, dto.email());

        userMapper.update(dto, user);

        updatePassword(dto, user);

        // Ensures @LastModifiedDate is updated before mapping the response
        userRepository.flush();

        return userMapper.toDTO(user);
    }

    @Transactional
    @Override
    public void deleteAuthenticatedUser() {

        User user = currentUserService.getAuthenticatedUser();

        userRepository.delete(user);
    }

    private void updatePassword(UserUpdateRequestDTO dto, User user) {

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
    }
}
