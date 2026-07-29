package com.geisivan.userservice.application.dto.response;

public record LoginResponseDTO(
        String token,
        String type,
        Long userId,
        String email
) {}
