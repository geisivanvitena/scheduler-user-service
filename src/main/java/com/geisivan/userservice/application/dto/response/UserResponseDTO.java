package com.geisivan.userservice.application.dto.response;

import java.util.List;

public record UserResponseDTO (
        Long id,
        String name,
        String email,
        List<AddressResponseDTO> address,
        List<PhoneResponseDTO> phones
){}
