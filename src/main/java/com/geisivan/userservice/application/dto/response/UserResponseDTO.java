package com.geisivan.userservice.application.dto.response;

import com.geisivan.userservice.domain.enums.UserStatus;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        UserStatus status,
        Set<RoleResponseDTO> roles,
        List<AddressResponseDTO> addresses,
        List<PhoneResponseDTO> phones,
        Instant createdAt,
        Instant updatedAt
) {}
