package com.geisivan.userservice.application.dto.response;

public record AddressResponseDTO (
        Long id,
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String postalCode
) {}
