package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.AdminUserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;

public interface AdminUserService {

    UserResponseDTO createUser(AdminUserRequestDTO dto);
}
