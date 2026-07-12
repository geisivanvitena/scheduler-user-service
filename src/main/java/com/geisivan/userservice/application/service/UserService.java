package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.UserUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO findAuthenticatedUser();

    UserResponseDTO updateAuthenticatedUser(UserUpdateRequestDTO dto);

    void deleteAuthenticatedUser();
}
