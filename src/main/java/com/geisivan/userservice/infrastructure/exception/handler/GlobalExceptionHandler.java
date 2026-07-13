package com.geisivan.userservice.infrastructure.exception.handler;

import com.geisivan.userservice.infrastructure.exception.code.ErrorCode;
import com.geisivan.userservice.infrastructure.exception.custom.ApiException;
import com.geisivan.userservice.infrastructure.exception.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
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

        log.warn("Business exception | status: {} | method: {} | path: {} | message: {}",
                exception.getHttpStatus().value(),
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage());

        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(new ErrorResponseDTO(
                        Instant.now(),
                        exception.getHttpStatus().value(),
                        exception.getHttpStatus().getReasonPhrase(),
                        exception.getMessage(),
                        request.getRequestURI(),
                        ErrorCode.BUSINESS_ERROR,
                        List.of()));
    }

    // Handler authentication exception
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request){

        log.warn("Authentication failed | status: {} | method: {} | path: {}",
                HttpStatus.UNAUTHORIZED.value(),
                request.getMethod(),
                request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponseDTO(
                        Instant.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                        exception.getMessage(),
                        request.getRequestURI(),
                        ErrorCode.UNAUTHORIZED,
                        List.of()));
    }

    // Handles validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDTO(
                        Instant.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Validation error",
                        request.getRequestURI(),
                        ErrorCode.VALIDATION_ERROR,
                        errors));
    }

    // Handles unexpected exceptions.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception exception,
            HttpServletRequest request) {

        log.error("Unexpected error | status: {} | method: {} | path: {}",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getMethod(),
                request.getRequestURI(),
                exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDTO(
                        Instant.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        "Internal server error. Please contact support.",
                        request.getRequestURI(),
                        ErrorCode.INTERNAL_ERROR,
                        List.of()));
    }
}
