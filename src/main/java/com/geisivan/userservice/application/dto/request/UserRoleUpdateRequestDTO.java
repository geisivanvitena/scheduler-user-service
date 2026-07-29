package com.geisivan.userservice.application.dto.request;

import com.geisivan.userservice.domain.enums.RoleName;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record UserRoleUpdateRequestDTO (
        @NotNull
        Set<RoleName> roles
) {}
