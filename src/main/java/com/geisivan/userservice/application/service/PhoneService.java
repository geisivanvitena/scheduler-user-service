package com.geisivan.userservice.application.service;

import com.geisivan.userservice.application.dto.request.PhoneRequestDTO;
import com.geisivan.userservice.application.dto.request.PhoneUpdateRequestDTO;
import com.geisivan.userservice.application.dto.response.PhoneResponseDTO;
import java.util.List;

public interface PhoneService {

    PhoneResponseDTO createAuthenticatedUserPhone(PhoneRequestDTO dto);

    List<PhoneResponseDTO> findAuthenticatedUserPhones();

    PhoneResponseDTO updateAuthenticatedUserPhone(Long phoneId, PhoneUpdateRequestDTO dto);

    void deleteAuthenticatedUserPhone(Long phoneId);
}
