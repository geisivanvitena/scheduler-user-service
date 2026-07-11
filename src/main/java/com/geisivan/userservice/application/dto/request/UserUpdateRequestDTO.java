package com.geisivan.userservice.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UserUpdateRequestDTO(

        @Size(min = 3, message = "Name must have at least 3 characters")
        String name,

        @Email(message = "Email must be valid")
        String email,

        @Size(min = 6, message = "Password must have at least 6 characters")
        String password,

        @Valid
        List<AddressRequestDTO> addresses,

        @Valid
        List<PhoneRequestDTO> phones
) {}
