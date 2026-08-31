package com.geisivan.userservice.application.dto.request;

import com.geisivan.userservice.domain.enums.RoleName;
import com.geisivan.userservice.domain.enums.UserStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public record AdminUserRequestDTO(

        @NotBlank(message = "Name is required")
        @Size(
                max = 150,
                message = "Name must have at most 150 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        @Size(
                max = 150,
                message = "Email must have at most 150 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(
                min = 6,
                max = 255,
                message = "Password must be between 6 and 255 characters")
        String password,

        @NotEmpty(message = "Roles cannot be empty")
        Set<RoleName> roles,

        @NotNull(message = "Status is required")
        UserStatus status,

        List<@Valid AddressRequestDTO> addresses,

        List<@Valid PhoneRequestDTO> phones
) {}
