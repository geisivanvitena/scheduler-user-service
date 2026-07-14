package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;

public interface PhoneService {

    PhoneResponseDTO createAuthenticatedUserPhone(PhoneRequestDTO dto);
}
