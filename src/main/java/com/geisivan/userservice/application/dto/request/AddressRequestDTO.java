package com.geisivan.userservice.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO(

        @NotBlank(message = "Street is required")
        @Size(
                max = 150,
                message = "Street must have at most 150 characters")
        String street,

        @NotBlank(message = "Number is required")
        @Size(max = 20, message = "Number must have at most 20 characters")
        String number,

        @NotBlank(message = "Neighborhood is required")
        @Size(
                max = 100,
                message = "Neighborhood must have at most 100 characters")
        String neighborhood,

        @NotBlank(message = "City is required")
        @Size(
                max = 100,
                message = "City must have at most 100 characters")
        String city,

        @NotBlank(message = "State is required")
        @Size(
                min = 2,
                max = 2,
                message = "State must have 2 characters (UF)")
        String state,

        @NotBlank(message = "Postal code is required")
        @Pattern(
                regexp = "\\d{5}-\\d{3}",
                message = "Postal code must be in the format 00000-000")
        String postalCode
) {}
