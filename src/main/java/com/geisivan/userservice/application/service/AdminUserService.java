package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRoleUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserStatusUpdateRequestDTO;
import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PageResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    UserResponseDTO createUser(AdminUserRequestDTO dto);

    PageResponseDTO<UserResponseDTO> findAllUsers(
            UserStatus status, RoleName role, Pageable pageable);

    UserResponseDTO findUserById(Long id);

    UserResponseDTO findUserByEmail(String email);

    UserResponseDTO updateUser(Long id, UserUpdateRequestDTO dto);

    UserResponseDTO updateUserStatus(Long id, UserStatusUpdateRequestDTO dto);

    UserResponseDTO updateUserRole(Long id, UserRoleUpdateRequestDTO dto);
}
