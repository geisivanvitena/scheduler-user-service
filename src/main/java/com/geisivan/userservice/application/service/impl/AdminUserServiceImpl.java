package com.geisivan.userservice.application.service.impl;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRoleUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserStatusUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PageResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.application.mapper.AdminUserMapper;
import com.geisivan.userservice.application.mapper.UserMapper;
import com.geisivan.userservice.application.service.AdminUserService;
import com.geisivan.userservice.application.validator.UserValidator;
import com.geisivan.userservice.domain.entity.Role;
import com.geisivan.userservice.domain.entity.User;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import com.geisivan.userservice.infrastructure.exception.custom.ResourceNotFoundException;
import com.geisivan.userservice.infrastructure.repository.RoleRepository;
import com.geisivan.userservice.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserValidator userValidator;
    private final AdminUserMapper adminUserMapper;
    private final UserMapper userMapper;
    private final UserRepository  userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String NOT_FOUND = " not found";

    @Transactional
    @Override
    public UserResponseDTO createUser(AdminUserRequestDTO dto) {

        userValidator.validateEmailExists(dto.email());

        User user = buildAdminUser(dto);

        return userMapper.toDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<UserResponseDTO> findAllUsers(
            UserStatus status, RoleName role, Pageable pageable) {

        Page<UserResponseDTO> usersPage = userRepository
                .findAllWithFilters(status, role, pageable)
                .map(userMapper::toDTO);

        return new PageResponseDTO<>(
                usersPage.getContent(),
                usersPage.getNumber(),
                usersPage.getSize(),
                usersPage.getTotalElements(),
                usersPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO findUserById(Long id) {

        User user = getUserById(id);

        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponseDTO findUserByEmail(String email) {

        User user = getUserByEmail(email);

        return userMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto) {

        User user = getUserById(id);

        userValidator.validateEmailUpdate(user, dto.email());

        userMapper.update(dto, user);

        updatePassword(dto, user);

        // Ensures @LastModifiedDate is updated before mapping the response
        userRepository.flush();

        return userMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUserStatus(
            Long id, UserStatusUpdateRequestDTO dto) {

        User user = getUserById(id);

        user.setStatus(dto.status());

        return userMapper.toDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUserRole(
            Long id, UserRoleUpdateRequestDTO dto) {

        User user = getUserById(id);

        user.setRoles(buildRoles(dto.roles()));

        return userMapper.toDTO(user);
    }

    private User buildAdminUser(AdminUserRequestDTO dto){

        User user = adminUserMapper.toEntity(dto);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRoles(buildRoles(dto.roles()));

        return user;
    }

    private Set<Role> buildRoles(Set<RoleName> roleNames){

        return  roleNames.stream()
                .map(this::findRole)
                .collect(Collectors.toSet());
    }

    private void updatePassword(UserUpdateRequestDTO dto, User user) {

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password()));
        }
    }

    private Role findRole(RoleName  roleName) {

        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Role " + roleName + NOT_FOUND));
    }

    private User getUserById(Long id){

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with ID " + id + NOT_FOUND));
    }

    private User getUserByEmail(String email){

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with email " + email + NOT_FOUND));
    }
}
