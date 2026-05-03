package com.geisivan.userservice.infrastructure.exception;

import org.springframework.http.HttpStatus;

public class EnvironmentVariableNotFoundException extends ApiException {

    public EnvironmentVariableNotFoundException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
