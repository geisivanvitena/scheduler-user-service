package com.geisivan.userservice.infrastructure.handler;

import com.geisivan.userservice.infrastructure.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // Handler business exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDTO> handleApiException(
            ApiException exception,
            HttpServletRequest request) {

        log.warn("Business exception: {}", exception.getMessage());

        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(new ErrorResponseDTO(
                        LocalDateTime.now(),
                        exception.getHttpStatus().value(),
                        exception.getHttpStatus().getReasonPhrase(),
                        exception.getMessage(),
                        request.getRequestURI(),
                        ErrorCode.BUSINESS_ERROR,
                        List.of()));
    }

    // Handles unexpected exceptions.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Unexpected error occurred", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Internal server error. Please contact support.",
                        request.getRequestURI(),
                        ErrorCode.INTERNAL_ERROR,
                        List.of()));
    }
}
