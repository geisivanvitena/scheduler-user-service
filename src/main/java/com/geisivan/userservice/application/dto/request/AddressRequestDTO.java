package com.geisivan.userservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO (

        @NotBlank(message = "Street is required")
        @Size(max = 150, message = "Street must have at most 150 characters")
        String street,

        @NotNull(message = "Number is required")
        Long number,

        @Size(max = 50, message = "Complement must have at most 50 characters")
        String complement,

        @NotBlank(message = "Neighborhood is required")
        @Size(max = 100, message = "Neighborhood must have at most 100 characters")
        String neighborhood,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City must have at most 100 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(min = 2, max = 2, message = "State must have 2 characters (UF)")
        String state,

        @NotBlank(message = "ZIP code is required")
        @Pattern(regexp = "\\d{5}-\\d{3}", message =
                "ZIP code must be in the format 00000-000")
        String zip
){}



