package com.geisivan.userservice.application.dto.response;

public record LoginResponseDTO(

        String token,
        Long userId,
        String email
) {}
