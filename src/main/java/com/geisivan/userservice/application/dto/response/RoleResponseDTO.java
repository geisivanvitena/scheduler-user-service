package com.geisivan.userservice.application.dto.response;

import com.geisivan.userservice.domain.enums.RoleName;

public record RoleResponseDTO(
        Long id,
        RoleName name,
        String description
) {}
