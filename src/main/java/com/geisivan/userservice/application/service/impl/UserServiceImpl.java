package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.UserService;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import com.geisivan.userservice.infrastructure.security.util.AuthenticatedUserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDTO findAuthenticatedUser() {
        Long userId = AuthenticatedUserUtil.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));


        return userMapper.toDTO(user);
    }
}
