package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.util.AuthenticatedUserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO findAuthenticatedUser() {

        Long userId = AuthenticatedUserUtil.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        return userMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO updateAuthenticatedUser(UserUpdateRequestDTO dto) {

        Long userId = AuthenticatedUserUtil.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        userMapper.update(dto, user);

        if (dto.password() != null && !dto.password().isBlank()){
            user.setPassword(passwordEncoder.encode(dto.password()));
        }

        userRepository.flush();

        return userMapper.toDTO(user);
    }
}
