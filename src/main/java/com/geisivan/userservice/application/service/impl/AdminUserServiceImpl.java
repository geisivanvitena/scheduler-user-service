package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.AdminUserMapper;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.AdminUserService;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.infrastructure.exception.custom.ConflictException;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository  userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDTO createUser(AdminUserRequestDTO dto) {

        validateEmailExists(dto.email());

        User user = buildAdminUser(dto);

        return userMapper.toDTO(userRepository.save(user));
    }

    private User buildAdminUser(AdminUserRequestDTO dto){

        User user = adminUserMapper.toEntity(dto);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = dto.roles()
                .stream()
                .map(this::findRole)
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return user;
    }

    private void validateEmailExists(String email) {

        if (userRepository.existsByEmail(email)){
            throw new ConflictException(
                    "Email " + email + "already exists");
        }
    }

    private Role findRole(RoleName  roleName) {

        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role " + roleName + " not found"));
    }
}
