package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {

    UserResponseDTO createUser(AdminUserRequestDTO dto);

    Page<UserResponseDTO> findAllUsers(Pageable pageable);
}
