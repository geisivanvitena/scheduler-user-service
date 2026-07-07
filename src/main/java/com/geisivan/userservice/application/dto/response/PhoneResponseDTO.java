package com.geisivan.userservice.application.dto.response;

import com.geisivan.userservice.domain.enums.PhoneType;

public record PhoneResponseDTO(
        Long id,
        String areaCode,
        String phoneNumber,
        PhoneType phoneType
) {}
