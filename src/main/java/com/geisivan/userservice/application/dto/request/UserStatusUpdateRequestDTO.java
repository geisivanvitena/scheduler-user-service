package com.geisivan.userservice.application.dto.request;

import com.geisivan.userservice.domain.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequestDTO(
        @NotNull
        UserStatus status
) {}
