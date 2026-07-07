package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(UserRequestDTO dto);
}
