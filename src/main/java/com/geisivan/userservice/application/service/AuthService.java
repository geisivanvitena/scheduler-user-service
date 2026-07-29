package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.LoginRequestDTO;
import com.geisivan.userservice.application.dto.request.UserRequestDTO;
import com.geisivan.userservice.application.dto.response.LoginResponseDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;

public interface AuthService {

    UserResponseDTO register(UserRequestDTO dto);

    LoginResponseDTO login(LoginRequestDTO dto);
}
