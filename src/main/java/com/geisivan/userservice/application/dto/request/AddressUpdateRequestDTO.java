package com.geisivan.userservice.application.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressUpdateRequestDTO(

        @Size(
                max = 150,
                message = "Street must have at most 150 characters")
        String street,

        @Size(max = 20, message = "Number must have at most 20 characters")
        String number,

        @Size(
                max = 100,
                message = "Neighborhood must have at most 100 characters")
        String neighborhood,

        @Size(
                max = 100,
                message = "City must have at most 100 characters")
        String city,

        @Size(
                min = 2,
                max = 2,
                message = "State must have 2 characters (UF)")
        String state,

        @Pattern(
                regexp = "\\d{5}-\\d{3}",
                message = "Postal code must be in the format 00000-000")
        String postalCode
) {}
