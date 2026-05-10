package com.geisivan.userservice.infrastructure.handler;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseDTO(

        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        ErrorCode code,
        List<String> errors
) {}
