package com.geisivan.userservice.infrastructure.exception.response;

import com.geisivan.userservice.infrastructure.exception.code.ErrorCode;
import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        ErrorCode errorCode,
        List<String> errorDetails
) {}
