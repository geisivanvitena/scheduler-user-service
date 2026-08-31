package com.geisivan.userservice.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserRequestDTO(

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

        List<@Valid AddressRequestDTO> addresses,

        List<@Valid PhoneRequestDTO> phones
) {}
