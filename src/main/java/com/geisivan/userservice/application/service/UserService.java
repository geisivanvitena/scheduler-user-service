package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO findAuthenticatedUser();
}
