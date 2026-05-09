package com.geisivan.userservice.application.dto.response;

public record AddressResponseDTO(
        Long id,
        String street,
        Long number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zip
){}
